package org.jahia.modules.portal.service.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kevan on 24/04/14.
 */
public class PortalWidgetType implements Serializable{
    private static final long serialVersionUID = -9198409193514919779L;

    private String name;
    private String displayableName;
    private List<PortalWidgetTypeView> views;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayableName() {
        return displayableName;
    }

    public void setDisplayableName(String displayableName) {
        this.displayableName = displayableName;
    }

    public List<PortalWidgetTypeView> getViews() {
        return views;
    }

    public void setViews(List<PortalWidgetTypeView> views) {
        this.views = views;
    }
}
