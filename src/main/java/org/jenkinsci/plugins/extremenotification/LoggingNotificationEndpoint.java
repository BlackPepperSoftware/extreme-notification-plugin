package org.jenkinsci.plugins.extremenotification;

import hudson.Extension;
import hudson.util.ListBoxModel;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.extremenotification.MyPlugin.Event;
import org.kohsuke.stapler.DataBoundConstructor;

@Extension
public class LoggingNotificationEndpoint extends NotificationEndpoint {
	
	private static final Level[] LEVELS = new Level[] {
		Level.ALL, 
		Level.FINEST, 
		Level.FINER, 
		Level.FINE, 
		Level.CONFIG, 
		Level.INFO, 
		Level.WARNING, 
		Level.SEVERE
	};
	
	private transient Logger logger;
	
	private transient Level level;
	
	private String loggerName;
	
	private String levelName;
	
	public LoggingNotificationEndpoint() {
		
	}
	
	@DataBoundConstructor
	public LoggingNotificationEndpoint(String loggerName, String levelName) {
		setLoggerName(loggerName);
		setLevelName(levelName);
	}
	
	public String getLoggerName() {
		return loggerName;
	}
	
	public void setLoggerName(String logger) {
		this.loggerName = logger;
		this.logger = Logger.getLogger(logger);
	}
	
	public String getLevelName() {
		return levelName;
	}
	
	public void setLevelName(String levelName) {
		this.levelName = levelName;
		this.level = Level.parse(levelName);
	}
	
	@Override
	public void notify(Event event) {
		logger.log(level, event.getName());
	}
	
	@Override
	public void notify(Event event, EndpointEvent endpointEvent) {
		// TODO Auto-generated method stub
		
	}
	
	private Object readResolve() {
		setLoggerName(loggerName);
		setLevelName(levelName);
		return this;
	}

	@Extension
    public static final class DescriptorImpl extends NotificationEndpoint.DescriptorImpl {
		
        public String getDisplayName() {
            return Messages.LoggingNotificationEndpoint_DisplayName();
        }

        public ListBoxModel doFillLevelNameItems() {
        	final ListBoxModel listBoxModel = new ListBoxModel();
        	for (Level level : LEVELS) {
        		listBoxModel.add(level.getName());
        	}
			return listBoxModel;
        }
        
        @Override
		protected EndpointEventCustom parseCustom(JSONObject event) {
        	final JSONObject customJSON = event.getJSONObject("custom");
			if (!customJSON.isNullObject()) {
				return new LoggingEndpointEventCustom(customJSON.getString("format"));
			}
			
			return null;
		}
    	
    	public static class LoggingEndpointEventCustom implements EndpointEventCustom {
    		private final String format;
    		public LoggingEndpointEventCustom(String format) {
    			this.format = format;
    		}
    		public String getUrl() {
				return format;
			}
    	}
        
    }
}
