package org.jahia.modules.portal.sitesettings.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.jahia.modules.portal.sitesettings.form.constraint.PortalTemplates;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 23/12/13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class PortalForm implements Serializable{
    private static final long serialVersionUID = -2739282645546704933L;

    @NotEmpty
    private String name;
    @NotEmpty
    private String tabName;
    @NotEmpty
    private String widgetsSkin;
    @NotEmpty
    @PortalTemplates
    private String templateRootPath;

    public String getWidgetsSkin() {
        return widgetsSkin;
    }

    public void setWidgetsSkin(String widgetsSkin) {
        this.widgetsSkin = widgetsSkin;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTemplateRootPath() {
        return templateRootPath;
    }

    public void setTemplateRootPath(String templateRootPath) {
        this.templateRootPath = templateRootPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
