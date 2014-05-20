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
