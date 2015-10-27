package org.jenkinsci.plugins.extremenotification;

import static hudson.init.InitMilestone.PLUGINS_STARTED;
import hudson.Plugin;
import hudson.init.Initializer;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.extremenotification.NotificationEndpoint.EndpointEvent;
import org.kohsuke.stapler.StaplerRequest;

public class MyPlugin extends Plugin {
	
	private static final Logger LOGGER = Logger.getLogger(MyPlugin.class.getName());
	
	public static final String JENKINS_BUILD_STEP_START = "jenkins.build.step.start";
	
	public static final String JENKINS_BUILD_STEP_FINISH = "jenkins.build.step.finish";

	public static final String JENKINS_COMPUTER_CONFIGURATION = "jenkins.computer.configuration";
	
	public static final String JENKINS_COMPUTER_FAILURE = "jenkins.computer.failure";
	
	public static final String JENKINS_COMPUTER_OFFLINE = "jenkins.computer.offline";

	public static final String JENKINS_COMPUTER_ONLINE = "jenkins.computer.online";
	
	public static final String JENKINS_COMPUTER_TEMPORARILY_OFFLINE = "jenkins.computer.temporarily-offline";
	
	public static final String JENKINS_COMPUTER_TEMPORARILY_ONLINE = "jenkins.computer.temporarily-online";

	public static final String JENKINS_SHUTDOWN = "jenkins.shutdown";
	
	public static final String JENKINS_ITEM_COPIED = "jenkins.item.copied";
	
	public static final String JENKINS_ITEM_CREATED = "jenkins.item.created";
	
	public static final String JENKINS_ITEM_DELETED = "jenkins.item.deleted";
	
	public static final String JENKINS_LOADED = "jenkins.loaded";
	
	public static final String JENKINS_ITEM_RENAMED = "jenkins.item.renamed";
	
	public static final String JENKINS_ITEM_UPDATED = "jenkins.item.updated";

	public static final String JENKINS_JOB_STARTED = "jenkins.job.started";
	
	public static final String JENKINS_JOB_COMPLETED = "jenkins.job.completed";
	
	public static final String JENKINS_JOB_FINALIZED = "jenkins.job.finalized";
	
	public static final String JENKINS_JOB_DELETED = "jenkins.job.deleted";

	public static final String JENKINS_JOB_MODULE_COMPLETED = "jenkins.job.module.completed";

	public static final String JENKINS_JOB_MODULE_FINALIZED = "jenkins.job.module.finalized";
	
	public static final String JENKINS_MATRIX_CONFIG_STARTED = "jenkins.matrix-config.started";
	
	public static final String JENKINS_MATRIX_CONFIG_COMPLETED = "jenkins.matrix-config.completed";
	
	public static final String JENKINS_MATRIX_CONFIG_FINALIZED = "jenkins.matrix-config.finalized";
	
	public static final String JENKINS_MATRIX_CONFIG_DELETED = "jenkins.matrix-config.deleted";
	
	public static final String JENKINS_SAVEABLE_CHANGE = "jenkins.saveable.change";
	
	public static final String JENKINS_SCM_CHANGELOG_PARSED = "jenkins.scm.changelog.parsed";

	public static final String JENKINS_SCM_CHECKOUT = "jenkins.scm.checkout";

	public static final String JENKINS_SCM_POLL_BEFORE = "jenkins.scm.poll.before";
	
	public static final String JENKINS_SCM_POLL_SUCCESS = "jenkins.scm.poll.success";
	
	public static final String JENKINS_SCM_POLL_FAILED = "jenkins.scm.poll.failed";

	public static final String JENKINS_STARTED = "jenkins.started";
	
	public static final String JENKINS_PLUGINS_LISTED = "jenkins.plugins.listed";
	
	public static final String JENKINS_PLUGINS_PREPARED = "jenkins.plugins.prepared";
	
	public static final String JENKINS_PLUGINS_STARTED = "jenkins.plugins.started";
	
	public static final String JENKINS_PLUGINS_AUGMENTED = "jenkins.plugins.augmented";
	
	public static final String JENKINS_JOBS_LOADED = "jenkins.jobs.loaded";
	
	public static final String JENKINS_COMPLETED = "jenkins.completed";
	
	public static final String[] ENDPOINTS = new String[] {
		JENKINS_BUILD_STEP_START,
		JENKINS_BUILD_STEP_FINISH,
		JENKINS_COMPUTER_CONFIGURATION,
		JENKINS_COMPUTER_FAILURE,
		JENKINS_COMPUTER_OFFLINE,
		JENKINS_COMPUTER_ONLINE,
		JENKINS_COMPUTER_TEMPORARILY_OFFLINE,
		JENKINS_COMPUTER_TEMPORARILY_ONLINE,
		JENKINS_SHUTDOWN,
		JENKINS_ITEM_COPIED,
		JENKINS_ITEM_CREATED,
		JENKINS_ITEM_DELETED,
		JENKINS_LOADED,
		JENKINS_ITEM_RENAMED,
		JENKINS_ITEM_UPDATED,
		JENKINS_JOB_STARTED,
		JENKINS_JOB_COMPLETED,
		JENKINS_JOB_FINALIZED,
		JENKINS_JOB_DELETED,
		JENKINS_MATRIX_CONFIG_STARTED,
		JENKINS_MATRIX_CONFIG_COMPLETED,
		JENKINS_MATRIX_CONFIG_FINALIZED,
		JENKINS_SAVEABLE_CHANGE,
		JENKINS_SCM_CHANGELOG_PARSED,
		JENKINS_SCM_POLL_BEFORE,
		JENKINS_SCM_POLL_SUCCESS,
		JENKINS_SCM_POLL_FAILED,
		JENKINS_STARTED,
		JENKINS_PLUGINS_LISTED,
		JENKINS_PLUGINS_PREPARED,
		JENKINS_PLUGINS_STARTED,
		JENKINS_PLUGINS_AUGMENTED,
		JENKINS_JOBS_LOADED,
		JENKINS_COMPLETED
	};
	
	private static MyPlugin instance;

	public static void notify(Event event) {
		if (instance != null) {
			instance._notify(event);
		}
	}
	
	@Initializer(after = PLUGINS_STARTED)
	public static void init() {
		instance = Jenkins.getInstance().getPlugin(MyPlugin.class);
	}
	
	private DescribableList<NotificationEndpoint, Descriptor<NotificationEndpoint>> endpoints = new DescribableList<>(this);
	
	@Override
	public void start() throws Exception {
		try {
			load();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to load", e);
		}
	}
	
	public DescribableList<NotificationEndpoint, Descriptor<NotificationEndpoint>> getEndpoints() {
		return endpoints;
	}
	
	@Override
	public void configure(StaplerRequest req, JSONObject formData) throws IOException, ServletException, FormException {
		try {
			endpoints.rebuildHetero(req, formData, NotificationEndpoint.all(), "endpoints");
			save();
	    } catch (IOException e) {
	        throw new FormException(e, "endpoints");
	    }
	}
	
	private void _notify(final Event event) {
		for (final NotificationEndpoint endpoint : endpoints) {
			if (endpoint.getEvents().isEmpty()) {
				start(new Runnable() {
					public void run() {
						endpoint.notify(event);
					}
				});
			} else if (endpoint.getEvents().containsKey(event.getName())) {
				final EndpointEvent endpointEvent = endpoint.getEvents().get(event.getName());
				start(new Runnable() {
					public void run() {
						endpoint.notify(event, endpointEvent);
					}
				});
			}
		}
	}

	private void start(Runnable runnable) {
		Executors.newSingleThreadExecutor().submit(runnable);
	}
	
	public static final class Event {
		
		private final Long timestamp;
		
		private final String name;
		
		private final Map<String, Object> args = new HashMap<>();

		private final Map<String, Object> payload = new HashMap<>();

		public Event(String name) {
			this.timestamp = System.currentTimeMillis();
			this.name = name;
			this.args.put("event", this);
		}
		
		public Event(String name, Map<String, Object> args, Map<String, Object> payload) {
			this(name);
			this.args.putAll(args);
			this.payload.putAll(payload);
		}
		
		public Long getTimestamp() {
			return timestamp;
		}
		
		public String getName() {
			return name;
		}
		
		public Map<String, Object> getArgs() {
			return args;
		}

		public Map<String, Object> getPayload() {
			return payload;
		}
	}

	public static Map<String,Object> asMap(Object... args) {
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException("Must supply even number of arguments");
		}

		Map <String,Object> values = new HashMap<>();

		for (int i = 0; i < args.length; i+=2) {
			values.put((String) args[i], args[i + 1]);
		}

		return values;
	}

	public static String orDefault(String value, String defaultVal) {
		return value == null || value.isEmpty() ? defaultVal : value;
	}
}
