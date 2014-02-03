package org.jahia.modules.portal.sitesettings.form;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import org.jahia.modules.portal.sitesettings.form.constraint.PortalTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 30/01/14
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
public class PortalForm implements Serializable {
    private static final long serialVersionUID = -5905664285663810283L;

    private String portalModelIdentifier;

    @NotEmpty
    private String name;
    @NotEmpty
    private String[] allowedWidgetTypes;
	@PortalTemplate
    private String templateFull;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAllowedWidgetTypes() {
        return allowedWidgetTypes;
    }

    public void setAllowedWidgetTypes(String[] allowedWidgetTypes) {
        this.allowedWidgetTypes = allowedWidgetTypes;
    }

    public String getTemplateFull() {
        return templateFull;
    }

    public void setTemplateFull(String templateFull) {
        this.templateFull = templateFull;
    }

    public String getPortalModelIdentifier() {
        return portalModelIdentifier;
    }

    public void setPortalModelIdentifier(String portalModelIdentifier) {
        this.portalModelIdentifier = portalModelIdentifier;
    }
}
