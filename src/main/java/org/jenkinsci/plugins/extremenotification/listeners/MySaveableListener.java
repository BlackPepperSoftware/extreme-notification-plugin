package org.jenkinsci.plugins.extremenotification.listeners;

import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_SAVEABLE_CHANGE;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.asMap;
import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import jenkins.YesNoMaybe;

import org.jenkinsci.plugins.extremenotification.MyPlugin;

@Extension(dynamicLoadable=YesNoMaybe.YES)
public class MySaveableListener extends SaveableListener {

	@Override
	public void onChange(Saveable saveable, XmlFile file) {

		MyPlugin.notify(new MyPlugin.Event(
			JENKINS_SAVEABLE_CHANGE,
			asMap("saveable", saveable, "file", file),
			asMap("path", file.toString())
		));
	}
	
}
