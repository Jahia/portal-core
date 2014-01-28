package org.jahia.modules.portal.sitesettings.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.jahia.modules.portal.sitesettings.form.constraint.PortalTemplates;

import java.io.Serializable;
import java.util.List;

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
    private List<String> allowedwidgetsType;
    @NotEmpty
    @PortalTemplates
    private String templateRootPath;
    private String templateFull;

    @NotEmpty
    private String tabName;
    @NotEmpty
    private String tabWidgetsSkin;


    public String getTabWidgetsSkin() {
        return tabWidgetsSkin;
    }

    public void setTabWidgetsSkin(String widgetsSkin) {
        this.tabWidgetsSkin = widgetsSkin;
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

    public String getTemplateFull() {
        return templateFull;
    }

    public void setTemplateFull(String templateFull) {
        this.templateFull = templateFull;
    }
}
