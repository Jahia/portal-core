package org.jahia.modules.portal.sitesettings.table;

import org.jahia.services.usermanager.JahiaGroup;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kevan on 28/04/14.
 */
public class PortalModelTableRow implements Serializable{
    private static final long serialVersionUID = -3442734889122999019L;

    private String uuid;
    private String path;
    private String name;
    private long userPortals;
    private List<JahiaGroup> restrictedGroups;
    private boolean enabled;
    private long lastUsed;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserPortals() {
        return userPortals;
    }

    public void setUserPortals(long userPortals) {
        this.userPortals = userPortals;
    }

    public List<JahiaGroup> getRestrictedGroups() {
        return restrictedGroups;
    }

    public void setRestrictedGroups(List<JahiaGroup> restrictedGroups) {
        this.restrictedGroups = restrictedGroups;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }
}
