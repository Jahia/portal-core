/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2016 Jahia Solutions Group SA. All rights reserved.
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
