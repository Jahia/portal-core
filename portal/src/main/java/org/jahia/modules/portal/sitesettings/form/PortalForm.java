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
    @PortalTemplates
    private String templateRoot;

    public String getTemplateRoot() {
        return templateRoot;
    }

    public void setTemplateRoot(String templateRoot) {
        this.templateRoot = templateRoot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
