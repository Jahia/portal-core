package org.jahia.modules.portal.sitesettings.table;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by kevan on 24/02/14.
 */
public class UserPortalsTableRow implements Serializable{
    private static final long serialVersionUID = 6097799258358946615L;

    private String userNodeIdentifier;
    private String modelName;
    private long lastUsed;
    private Date created;

    public String getUserNodeIdentifier() {
        return userNodeIdentifier;
    }

    public void setUserNodeIdentifier(String userNodeIdentifier) {
        this.userNodeIdentifier = userNodeIdentifier;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
