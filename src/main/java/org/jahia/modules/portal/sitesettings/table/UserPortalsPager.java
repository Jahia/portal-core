/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2017 Jahia Solutions Group SA. All rights reserved.
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
