/*
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2019 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
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
    private Boolean allowCustomization;

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

    public Boolean getAllowCustomization() {
        return allowCustomization;
    }

    public void setAllowCustomization(Boolean allowCustomization) {
        this.allowCustomization = allowCustomization;
    }
}
