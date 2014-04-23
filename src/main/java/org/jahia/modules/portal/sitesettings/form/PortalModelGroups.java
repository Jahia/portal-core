package org.jahia.modules.portal.sitesettings.form;


import org.jahia.services.usermanager.SearchCriteria;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kevan on 22/04/14.
 */
public class PortalModelGroups implements Serializable{
    private static final long serialVersionUID = -8448050126315831852L;

    private String portalIdentifier;
    private List<String> groupsKey;
    private SearchCriteria searchCriteria;
    private String currentRestrictions;

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

    public String getCurrentRestrictions() {
        return currentRestrictions;
    }

    public void setCurrentRestrictions(String currentRestrictions) {
        this.currentRestrictions = currentRestrictions;
    }
}
