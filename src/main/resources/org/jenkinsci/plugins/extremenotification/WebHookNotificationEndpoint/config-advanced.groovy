package org.jenkinsci.plugins.extremenotification.WebHookNotificationEndpoint

def f=namespace(lib.FormTagLib)

f.entry(title:_("URL"), field:"url") {
	f.textbox()
}

f.entry(title:_("Method"), field:"method") {
	f.select()
}

f.entry(title:_("Timeout"), field:"timeout") {
	f.number(default: 30)
}
