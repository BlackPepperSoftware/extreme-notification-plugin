package org.jenkinsci.plugins.extremenotification;

import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_COMPUTER_CONFIGURATION;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_COMPUTER_FAILURE;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_COMPUTER_OFFLINE;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_COMPUTER_ONLINE;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_COMPUTER_TEMPORARILY_OFFLINE;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_COMPUTER_TEMPORARILY_ONLINE;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.asMap;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.orDefault;
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.ComputerListener;
import hudson.slaves.OfflineCause;
import jenkins.YesNoMaybe;

import java.io.IOException;
import javax.annotation.Nonnull;

@Extension(dynamicLoadable=YesNoMaybe.YES)
public class MyComputerListener extends ComputerListener {

	@Override
	public void onConfigurationChange() {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_COMPUTER_CONFIGURATION, asMap(), asMap()));
	}

	@Override
	public void onLaunchFailure(Computer computer, TaskListener listener) {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_COMPUTER_FAILURE,
			asMap("computer", computer),
			asMap("computer", getName(computer), "master")
		));
	};
	
	@Override
	public void onOffline(@Nonnull Computer computer, OfflineCause cause) {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_COMPUTER_OFFLINE,
			asMap("computer", computer, "cause", cause),
			asMap("computer", getName(computer),
				"cause", cause.toString(),
				"connectTime", computer.getConnectTime())
		));
	}

	@Override
	public void onOnline(Computer computer, TaskListener listener) throws IOException, InterruptedException {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_COMPUTER_ONLINE,
			asMap("computer", computer),
			asMap("computer", getName(computer))
		));
	}
	
	@Override
	public void onTemporarilyOffline(Computer computer, OfflineCause cause) {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_COMPUTER_TEMPORARILY_OFFLINE,
			asMap("computer", computer, "cause", cause),
			asMap("computer", getName(computer), "cause", cause.toString())
		));
	}
	
	@Override
	public void onTemporarilyOnline(Computer computer) {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_COMPUTER_TEMPORARILY_ONLINE,
			asMap("computer", computer),
			asMap("computer", getName(computer))
		));
	}

	public MyComputerListener() {
		super();
	}

	private static String getName(Computer computer) {
		return orDefault(computer.getName(), "master");
	}
}
