package org.jahia.modules.portal.sitesettings.table;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by kevan on 24/02/14.
 */
public class UserPortalsTable implements Serializable{
    private static final long serialVersionUID = -1244205399332002048L;

    //results
    private UserPortalsPager pager;
    private UserPortalsSearchCriteria searchCriteria;
    private HashMap<String, UserPortalsTableRow> rows;

    public UserPortalsPager getPager() {
        return pager;
    }

    public UserPortalsSearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(UserPortalsSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public void setPager(UserPortalsPager pager) {
        this.pager = pager;
    }

    public HashMap<String, UserPortalsTableRow> getRows() {
        return rows;
    }

    public void setRows(HashMap<String, UserPortalsTableRow> rows) {
        this.rows = rows;
    }
}
