package org.jenkinsci.plugins.extremenotification.listeners;

import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_COMPLETED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_JOBS_LOADED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_PLUGINS_AUGMENTED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_PLUGINS_LISTED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_PLUGINS_PREPARED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_PLUGINS_STARTED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_STARTED;
import hudson.Extension;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import jenkins.YesNoMaybe;

import java.io.IOException;
import org.jenkinsci.plugins.extremenotification.MyPlugin;

@Extension(dynamicLoadable=YesNoMaybe.YES)
public class ServerStateListener {

	@Initializer(after=InitMilestone.STARTED)
	public static void started() throws IOException {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_STARTED));
	}
	
	@Initializer(after=InitMilestone.PLUGINS_LISTED)
	public static void pluginsListed() throws IOException {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_PLUGINS_LISTED));
	}
	
	@Initializer(after=InitMilestone.PLUGINS_PREPARED)
	public static void pluginsPrepared() throws IOException {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_PLUGINS_PREPARED));
	}
	
	@Initializer(after=InitMilestone.PLUGINS_STARTED)
	public static void pluginsStarted() throws IOException {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_PLUGINS_STARTED));
	}
	
	@Initializer(after=InitMilestone.EXTENSIONS_AUGMENTED)
	public static void extensionsAugmented() throws IOException {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_PLUGINS_AUGMENTED));
	}
	
	@Initializer(after=InitMilestone.JOB_LOADED)
	public static void jobLoaded() throws IOException {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_JOBS_LOADED));
	}
	
	@Initializer(after=InitMilestone.COMPLETED)
	public static void completed() throws IOException {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_COMPLETED));
	}
	
}
