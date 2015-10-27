package org.jenkinsci.plugins.extremenotification;

import static org.apache.commons.httpclient.util.URIUtil.encodeQuery;
import hudson.Extension;
import hudson.util.ListBoxModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.URIException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.jenkinsci.plugins.extremenotification.MyPlugin.Event;
import org.kohsuke.stapler.DataBoundConstructor;

@Extension
public class WebHookNotificationEndpoint extends NotificationEndpoint {
	
	private static final Logger LOGGER = Logger.getLogger(WebHookNotificationEndpoint.class.getName());
	
	private String url;

	private String method;
	
	private long timeout;

	public WebHookNotificationEndpoint() {
		// Required for annotated class, according to the compiler
	}

	@DataBoundConstructor
	public WebHookNotificationEndpoint(String url, String method, long timeout) {
		this.url = url;
		this.method = "POST".equals(method) ? "POST" : "GET";
		this.timeout = timeout;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public void notify(Event event) {
		requestURL(event, url);
	}
	
	@Override
	public void notify(Event event, EndpointEvent endpointEvent) {
		final WebHookEndpointEventCustom custom = (WebHookEndpointEventCustom) endpointEvent.getCustom();
		requestURL(event, custom == null ? this.url : custom.getURL());
	}
	
	private void requestURL(Event event, String url) {
		final Map<String, Object> extra = new HashMap<String, Object>();
		extra.put("url", interpolate(this.url, event));

		final String localUrl;
		try {
			localUrl = encodeQuery(interpolate(url, event, extra));
		} catch (URIException e) {
			LOGGER.log(Level.SEVERE, "malformed URL: {}", url);
			return;
		}

		final HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setStaleCheckingEnabled(client.getParams(), true);

		final HttpRequestBase request = buildRequest(localUrl, event);

		ScheduledExecutorService singleThreadPool = Executors.newScheduledThreadPool(1);
		singleThreadPool.schedule(new Runnable() {
			public void run() {
				request.abort();
			}
		}, this.timeout, TimeUnit.SECONDS);

		try {
			final HttpResponse response = client.execute(request);
			LOGGER.log(Level.INFO, "{0} status {1}", new Object[]{localUrl, response});
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "communication failure: {0}", e.getMessage());
		} finally {
			request.releaseConnection();
			singleThreadPool.shutdownNow();
		}
	}

	private HttpRequestBase buildRequest(final String localUrl, final Event event) {
		if ("GET".equals(method)) {
			return new HttpGet(localUrl);
		}

		HttpPost post = new HttpPost(localUrl);

		Map<String,Object> payload = new HashMap<String, Object>();
		payload.put("eventName", event.getName().toString());
		payload.put("eventTimestamp", event.getTimestamp());
		payload.putAll(event.getPayload());

		try {
			JSONObject jsonObject = JSONObject.fromObject(payload);
			post.setEntity(new StringEntity(jsonObject.toString()));
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to serialise event to JSON", e);
		}

		return post;
	}

	private Object readResolve() {
		setUrl(url);
		return this;
	}

	@Extension
    public static final class DescriptorImpl extends NotificationEndpoint.DescriptorImpl {
		
        public String getDisplayName() {
            return Messages.WebHookNotificationEndpoint_DisplayName();
        }

        @Override
		protected EndpointEventCustom parseCustom(JSONObject event) {
        	final JSONObject customJSON = event.getJSONObject("custom");
			if (!customJSON.isNullObject()) {
				return new WebHookEndpointEventCustom(customJSON.getString("url"));
			}
			
			return null;
		}

		public ListBoxModel doFillMethodItems() {
			ListBoxModel options = new ListBoxModel();
			options.add("GET");
			options.add("POST");
			return options;
		}
    }
	
	public static class WebHookEndpointEventCustom implements EndpointEventCustom {
		private final String url;
		public WebHookEndpointEventCustom(String url) {
			this.url = url;
		}
		public String getURL() {
			return url;
		}
	}
}
