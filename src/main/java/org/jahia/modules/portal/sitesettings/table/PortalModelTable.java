package org.jahia.modules.portal.sitesettings.table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kevan on 28/04/14.
 */
public class PortalModelTable implements Serializable{
    private static final long serialVersionUID = -3670861784434623335L;

    private List<PortalModelTableRow> portalModelTableRows;

    public List<PortalModelTableRow> getPortalModelTableRows() {
        return portalModelTableRows;
    }

    public void setPortalModelTableRows(List<PortalModelTableRow> portalModelTableRows) {
        this.portalModelTableRows = portalModelTableRows;
    }
}
