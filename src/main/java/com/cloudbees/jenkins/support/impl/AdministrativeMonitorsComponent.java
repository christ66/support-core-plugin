/*
 * The MIT License
 *
 * Copyright 2014 Jesse Glick.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cloudbees.jenkins.support.impl;

import com.cloudbees.jenkins.support.api.*;
import com.cloudbees.jenkins.support.model.AdminMonitors;
import hudson.Extension;
import hudson.diagnosis.OldDataMonitor;
import hudson.diagnosis.ReverseProxySetupMonitor;
import hudson.model.AdministrativeMonitor;
import hudson.model.Saveable;
import hudson.security.Permission;
import jenkins.model.Jenkins;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Warns if any administrative monitors are currently active.
 */
@Extension public final class AdministrativeMonitorsComponent extends Component {

    @Override public String getDisplayName() {
        return "Administrative monitors";
    }

    @Override public Set<Permission> getRequiredPermissions() {
        return Collections.singleton(Jenkins.ADMINISTER);
    }

    @Override public void addContents(Container result) {
        final Map<String,AdministrativeMonitor> activated = new TreeMap<String,AdministrativeMonitor>();
        for (AdministrativeMonitor monitor : AdministrativeMonitor.all()) {

            if (monitor instanceof ReverseProxySetupMonitor) {
                // This one is pretty special: always activated, but may or may not show anything in message.jelly.
                continue;
            }

            if (monitor.isActivated() && monitor.isEnabled()) {
                activated.put(monitor.id, monitor);
            }

            // disabled monitors could include RekeySecretAdminMonitor; no reason to show it
        }

        if (!activated.isEmpty()) {
            AdminMonitors monitors = getAdminMonitors(activated);

            result.add(new YamlContent("admin-monitors.yaml", monitors));
            result.add(new MarkdownContent("admin-monitors.yaml", monitors));
        }
    }

    private AdminMonitors getAdminMonitors(Map<String, AdministrativeMonitor> activated) {
        AdminMonitors monitors = new AdminMonitors();

        for (AdministrativeMonitor monitor : activated.values()) {

            if (monitor instanceof OldDataMonitor) {
                AdminMonitors.OldDataMonitor olddm = new AdminMonitors.OldDataMonitor();
                OldDataMonitor odm = (OldDataMonitor) monitor;
                for (Map.Entry<Saveable,OldDataMonitor.VersionRange> entry : odm.getData().entrySet()) {

                    AdminMonitors.OldDataMonitor.OldData data = new AdminMonitors.OldDataMonitor.OldData();
                    data.setProblematicObject(entry.getKey().toString());

                    OldDataMonitor.VersionRange value = entry.getValue();
                    data.setRange(value.toString());
                    data.setExtra(value.extra);
                }
                olddm.setActive(true);
                monitors.addMonitor(olddm);
            } else {
                // No specific content we can show; message.jelly is for HTML only.
                AdminMonitors.AdminMonitor mon = new AdminMonitors.AdminMonitor();
                mon.setId(monitor.id);
                mon.setActive(true);
                monitors.addMonitor(mon);
            }
        }

        return monitors;
    }
}
