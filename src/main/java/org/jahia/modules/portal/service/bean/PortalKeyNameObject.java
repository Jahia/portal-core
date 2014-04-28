package org.jahia.modules.portal.service.bean;

import java.io.Serializable;

/**
 * Created by kevan on 24/04/14.
 */
public class PortalKeyNameObject implements Serializable{
    private static final long serialVersionUID = -262889587150714274L;

    private String name;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
