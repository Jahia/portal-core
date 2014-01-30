package org.jahia.modules.portal.sitesettings.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.jahia.modules.portal.sitesettings.form.constraint.PortalTemplates;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 23/12/13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class PortalModelForm implements Serializable{
    private static final long serialVersionUID = -2739282645546704933L;

    public PortalModelForm() {
        portal = new PortalForm();
    }

    @Valid
    private PortalForm portal;
    @NotEmpty
    @PortalTemplates
    private String templateRootPath;

    @NotEmpty
    private String tabName;
    @NotEmpty
    private String tabWidgetSkin;


    public String getTabWidgetSkin() {
        return tabWidgetSkin;
    }

    public void setTabWidgetSkin(String widgetsSkin) {
        this.tabWidgetSkin = widgetsSkin;
    }

    public String getTabName() {
        return tabName;
    }

    public String getTemplateRootPath() {
        return templateRootPath;
    }

    public void setTemplateRootPath(String templateRootPath) {
        this.templateRootPath = templateRootPath;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public PortalForm getPortal() {
        return portal;
    }

    public void setPortal(PortalForm portal) {
        this.portal = portal;
    }
}
