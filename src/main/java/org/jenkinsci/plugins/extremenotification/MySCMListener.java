package org.jenkinsci.plugins.extremenotification;

import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_SCM_CHANGELOG_PARSED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_SCM_CHECKOUT;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.asMap;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.SCMListener;
import hudson.scm.ChangeLogSet;
import hudson.scm.SCM;
import hudson.scm.SCMRevisionState;
import jenkins.YesNoMaybe;

import java.io.File;

@Extension(dynamicLoadable=YesNoMaybe.YES)
public class MySCMListener extends SCMListener {

	@Override
	public void onChangeLogParsed(final Run<?, ?> build, final SCM scm, final TaskListener listener, final ChangeLogSet<?> changelog) throws Exception {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_SCM_CHANGELOG_PARSED,
			asMap("build", build, "scm", scm, "listener", listener, "changelog", changelog),
			asMap()
		));
	}

	@Override
	public void onCheckout(final Run<?, ?> build, final SCM scm, final FilePath workspace, final TaskListener listener, final File changelogFile, final SCMRevisionState pollingBaseline) throws Exception {
		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_SCM_CHECKOUT,
			asMap("build", build, "scm", scm, "listener", listener, "changelogFile", changelogFile, "pollingBaseline", pollingBaseline),
			asMap("changelogFile", changelogFile.toString())
		));
	}
}
