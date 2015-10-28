package org.jenkinsci.plugins.extremenotification.listeners;

import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_ITEM_COPIED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_ITEM_CREATED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_ITEM_DELETED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_ITEM_RENAMED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_ITEM_UPDATED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_LOADED;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.JENKINS_SHUTDOWN;
import static org.jenkinsci.plugins.extremenotification.MyPlugin.asMap;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import jenkins.YesNoMaybe;

import org.jenkinsci.plugins.extremenotification.MyPlugin;

@Extension(dynamicLoadable=YesNoMaybe.YES)
public class MyItemListener extends ItemListener {

	public void onBeforeShutdown() {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_SHUTDOWN));
	}
	
	@Override
	public void onCopied(Item src, Item item) {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_ITEM_COPIED,
			asMap("item", item),
			asMap("src", src.getFullName(), "item", item.getFullName())
		));
	}
	
	public void onCreated(Item item) {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_ITEM_CREATED,
			asMap("item", item),
			asMap("item", item.getFullName())
		));
	}
	
	@Override
	public void onDeleted(Item item) {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_ITEM_DELETED,
			asMap("item", item),
			asMap("item", item.getFullName())
		));
	}
	
	public void onLoaded() {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_LOADED));
	}
	
	public void onRenamed(Item item, String oldName, String newName) {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_ITEM_RENAMED,
			asMap("item", item, "oldName", oldName, "newName", newName),
			asMap("item", item.getFullName(), "oldName", oldName, "newName", newName)
		));
	}
	
	@Override
	public void onUpdated(Item item) {
		MyPlugin.notify(new MyPlugin.Event(JENKINS_ITEM_UPDATED,
			asMap("item", item),
			asMap("item", item.getFullName())
		));
	}
	
}
