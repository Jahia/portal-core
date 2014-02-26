package org.jahia.modules.portal.sitesettings.table;

import java.io.Serializable;

/**
 * Created by kevan on 26/02/14.
 */
public class UserPortalsSearchCriteria implements Serializable{
    private static final long serialVersionUID = 1983045760545350084L;
    
    private String searchString;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
