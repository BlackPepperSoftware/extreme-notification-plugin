package org.jenkinsci.plugins.extremenotification;

import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_BUILD_STEP_FINISH;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_BUILD_STEP_START;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.asMap;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.orDefault;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.BuildStepListener;
import hudson.tasks.BuildStep;
import jenkins.YesNoMaybe;

import java.util.HashMap;
import java.util.Map;

@Extension(dynamicLoadable=YesNoMaybe.YES)
public class MyBuildStepListener extends BuildStepListener {

	@Override
	public void started(AbstractBuild build, BuildStep bs, BuildListener listener) {
		Map<String, Object> buildMap = getCommonBuildMap(build);
		buildMap.put("estimatedDuration", build.getEstimatedDuration());

		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_BUILD_STEP_START,
			asMap("build", build, "buildStep", bs, "listener", listener),
			buildMap
		));
	}

	@Override
	public void finished(AbstractBuild build, BuildStep bs, BuildListener listener, boolean canContinue) {
		Map<String, Object> buildMap = getCommonBuildMap(build);
		buildMap.put("duration", build.getDuration());

		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_BUILD_STEP_FINISH,
			asMap("build", build, "buildStep", bs, "listener", listener, "canContinue", canContinue),
			buildMap
		));
	}

	private static Map<String, Object> getCommonBuildMap(final AbstractBuild build) {
		Map<String,Object> buildMap = new HashMap<>();
		buildMap.put("builtOn", orDefault(build.getBuiltOnStr(), "master"));
		buildMap.put("description", build.getDescription());
		buildMap.put("number", build.getNumber());
		buildMap.put("startTimeInMillis", build.getStartTimeInMillis());
		return buildMap;
	}
}
