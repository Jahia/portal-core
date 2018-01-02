/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2018 Jahia Solutions Group SA. All rights reserved.
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


import org.jahia.services.usermanager.SearchCriteria;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by kevan on 22/04/14.
 */
public class PortalModelGroups implements Serializable{
    private static final long serialVersionUID = -8448050126315831852L;

    private String portalIdentifier;
    private String portalDisplayableName;
    private List<String> groupsKey;
    private Map<String, Boolean> currentRestrictions;
    private Boolean displayLimited = false;
    private Integer displayLimit;
    private SearchCriteria searchCriteria;

    public List<String> getGroupsKey() {
        return groupsKey;
    }

    public void setGroupsKey(List<String> groupsKey) {
        this.groupsKey = groupsKey;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public String getPortalIdentifier() {
        return portalIdentifier;
    }

    public void setPortalIdentifier(String portalIdentifier) {
        this.portalIdentifier = portalIdentifier;
    }

    public Map<String, Boolean> getCurrentRestrictions() {
        return currentRestrictions;
    }

    public void setCurrentRestrictions(Map<String, Boolean> currentRestrictions) {
        this.currentRestrictions = currentRestrictions;
    }

    public Boolean getDisplayLimited() {
        return displayLimited;
    }

    public void setDisplayLimited(Boolean displayLimited) {
        this.displayLimited = displayLimited;
    }

    public Integer getDisplayLimit() {
        return displayLimit;
    }

    public void setDisplayLimit(Integer displayLimit) {
        this.displayLimit = displayLimit;
    }

    public String getPortalDisplayableName() {
        return portalDisplayableName;
    }

    public void setPortalDisplayableName(String portalDisplayableName) {
        this.portalDisplayableName = portalDisplayableName;
    }
}
