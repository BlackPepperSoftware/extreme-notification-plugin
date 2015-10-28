package org.jenkinsci.plugins.extremenotification.listeners;

import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_JOB_COMPLETED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_JOB_DELETED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_JOB_FINALIZED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_JOB_MODULE_COMPLETED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_JOB_MODULE_FINALIZED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_JOB_STARTED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_MATRIX_CONFIG_COMPLETED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_MATRIX_CONFIG_DELETED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_MATRIX_CONFIG_FINALIZED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_MATRIX_CONFIG_STARTED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.asMap;
import hudson.Extension;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractItem;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import jenkins.YesNoMaybe;

import java.util.Map;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.extremenotification.MyPlugin;

@Extension(dynamicLoadable=YesNoMaybe.YES)
public class MyRunListener extends RunListener<Run<?, ?>> {

	@Override
	public void onStarted(Run<?, ?> run, TaskListener listener) {
		Map<String, Object> payload = getPayload(run);
		payload.put("duration", run.getDuration());
		payload.put("estimatedDuration", run.getEstimatedDuration());

		MyPlugin.notify(new MyPlugin.Event(
			run instanceof MatrixRun ? JENKINS_MATRIX_CONFIG_STARTED : JENKINS_JOB_STARTED,
			asMap("run", run, "listener", listener),
			getPayload(run)
		));
	}

	@Override
	public void onCompleted(Run<?, ?> run, @Nonnull TaskListener listener) {
		Map<String, Object> payload = getPayload(run);
		payload.put("duration", run.getDuration());

		final String eventType = run instanceof MatrixRun
			? JENKINS_MATRIX_CONFIG_COMPLETED
			: isTopLevel(run)
				? JENKINS_JOB_COMPLETED
				: JENKINS_JOB_MODULE_COMPLETED;

		MyPlugin.notify(new MyPlugin.Event(
			eventType,
			asMap("run", run, "listener", listener),
			payload
		));
	}
	
	@Override
	public void onFinalized(Run<?, ?> run) {
		Map<String, Object> payload = getPayload(run);
		payload.put("duration", run.getDuration());

		final String eventType = run instanceof MatrixRun
			? JENKINS_MATRIX_CONFIG_FINALIZED
			: isTopLevel(run)
				? JENKINS_JOB_FINALIZED
				: JENKINS_JOB_MODULE_FINALIZED;

		MyPlugin.notify(new MyPlugin.Event(
			eventType,
			asMap("run", run),
			payload
		));
	}

	@Override
	public void onDeleted(Run<?, ?> run) {
		MyPlugin.notify(new MyPlugin.Event(
			run instanceof MatrixRun ? JENKINS_MATRIX_CONFIG_DELETED : JENKINS_JOB_DELETED,
			asMap("run", run),
			getPayload(run)
		));
	}

	private static Map<String, Object> getPayload(final Run<?, ?> run) {
		return asMap(
			"name", getName(run),
			"fullDisplayName", run.getFullDisplayName(),
			"parent", run.getParent().getFullDisplayName(),
			"number", run.getNumber(),
			"description", run.getDescription(),
			"startTimeMillis", run.getStartTimeInMillis());
	}

	private static String getName(Run<?,?> run) {
		final String name = run.getDisplayName();
		int hashIndex = name.indexOf('#');
		return hashIndex < 0 ? name : name.substring(0, hashIndex);
	}

	/**
	 * Inferring this property from the impl of {@link AbstractItem#getFullDisplayName()}
	 */
	private static boolean isTopLevel(final Run<?, ?> run) {
		return run.getParent().getParent().getFullDisplayName().isEmpty();
	}
}
