package org.jahia.modules.portal.sitesettings.table;

import java.io.Serializable;

/**
 * Created by kevan on 24/02/14.
 */
public class UserPortalsPager implements Serializable{
    private static final long serialVersionUID = -1833561200160987033L;

    private long maxResults = 0;
    private long page = 1;
    private long itemsPerPage = 1;
    private boolean sortAsc = true;
    private String sortBy = null;

    public long getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(long maxResults) {
        this.maxResults = maxResults;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(long itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public boolean isSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
