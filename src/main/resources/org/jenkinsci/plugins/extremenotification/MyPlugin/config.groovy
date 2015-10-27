package org.jenkinsci.plugins.extremenotification.MyPlugin

import org.jenkinsci.plugins.extremenotification.NotificationEndpoint

def f=namespace(lib.FormTagLib)

f.section(title:_("Notifications")) {
    f.block {
        f.hetero_list(name:"endpoints", hasHeader:true, descriptors:NotificationEndpoint.all(), items:my.endpoints,
            addCaption:_("Add a new endpoint"), deleteCaption:_("Delete endpoint"))
    }
}
