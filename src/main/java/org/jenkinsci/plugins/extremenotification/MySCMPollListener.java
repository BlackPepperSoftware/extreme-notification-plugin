package org.jenkinsci.plugins.extremenotification;

import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_SCM_POLL_BEFORE;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_SCM_POLL_FAILED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_SCM_POLL_SUCCESS;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.asMap;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.TaskListener;
import hudson.model.listeners.SCMPollListener;
import hudson.scm.PollingResult;
import jenkins.YesNoMaybe;

@Extension(dynamicLoadable=YesNoMaybe.YES)
public class MySCMPollListener extends SCMPollListener {

	@Override
	public void onBeforePolling(hudson.model.AbstractProject<?,?> project, TaskListener listener) {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_SCM_POLL_BEFORE,
			asMap("project", project, "listener", listener),
			asMap("name", project.getName())
		));
	}
	
	@Override
	public void onPollingSuccess(AbstractProject<?, ?> project, TaskListener listener, PollingResult result) {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_SCM_POLL_SUCCESS,
			asMap("project", project, "listener", listener, "result", result),
			asMap("name", project.getName(), "hasChanges", result.hasChanges())
		));
	}
	
	@Override
	public void onPollingFailed(hudson.model.AbstractProject<?,?> project, TaskListener listener, Throwable exception) {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_SCM_POLL_FAILED,
			asMap("project", project, "listener", listener, "exception", exception),
			asMap("name", project.getName(), "error", exception.getMessage())
		));
	}
	
}
