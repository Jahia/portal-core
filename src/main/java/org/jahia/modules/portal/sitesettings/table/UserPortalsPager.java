package org.jahia.modules.portal.sitesettings.table;

import java.io.Serializable;

/**
 * Created by kevan on 24/02/14.
 */
public class UserPortalsPager implements Serializable {
    private static final long serialVersionUID = -1833561200160987033L;

    private long maxResults = 0;
    private long page = 1;
    private long itemsPerPage = 50;
    private long pageEntries = 5;
    private Long[] itemsPerPageEntries = new Long[]{50l, 100l, 200l, 400l, 800l};
    private boolean sortAsc = true;
    private String sortBy = null;

    public long getPages() {
        return maxResults < itemsPerPage ? 1 : ((maxResults / itemsPerPage) + (maxResults % itemsPerPage));
    }

    public boolean isStart() {
        return page == 1;
    }

    public boolean isEnd() {
        return page == getPages();
    }

    public long getFirstEntry() {
        if (getLastEntry() <= pageEntries) {
            return 1;
        } else {
            return getLastEntry() - (pageEntries - 1);
        }
    }

    public long getLastEntry() {
        if (getPages() < pageEntries) {
            return getPages();
        } else {
            return page < getPages() - 1 ? pageEntries : getPages();
        }
    }

    public long getPageEntries() {
        return pageEntries;
    }

    public void setPageEntries(long pageEntries) {
        this.pageEntries = pageEntries;
    }

    public Long[] getItemsPerPageEntries() {
        return itemsPerPageEntries;
    }

    public void setItemsPerPageEntries(Long[] itemsPerPageEntries) {
        this.itemsPerPageEntries = itemsPerPageEntries;
    }

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
