package org.jahia.modules.portal.service.bean;

import java.io.Serializable;

/**
 * Created by kevan on 24/04/14.
 */
public class PortalWidgetTypeView implements Serializable{
    private static final long serialVersionUID = 3990113088683878447L;

    private String path;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
