package org.jahia.modules.portal.filter;

import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.*;
import org.jahia.modules.portal.service.bean.*;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.SiteInfo;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.sites.JahiaSitesService;


/**
 * Created by kevan on 23/04/14.
 */
public class PortalInitFilter extends AbstractFilter{

    private PortalService portalService;

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {

        if(renderContext.getRequest().getAttribute("portalContext") != null){
            // Portal already init
            return null;
        }

        boolean updateLastViewed = resource.getNode().isNodeType(PortalConstants.JNT_PORTAL_TAB);
        PortalContext portal = portalService.buildPortalFromTabNode(renderContext, resource.getNode(), resource.getNode().getSession(), updateLastViewed);

        // Add dependency to other tabs
        for (PortalTab portalTab : portal.getPortalTabs()){
            if (!portalTab.isCurrent()){
                resource.getDependencies().add(portalTab.getPath());
            }
        }

        // Add dependency to model portal
        if(!portal.isModel()){
            resource.getDependencies().add(portal.getModelPath());
        }
        // Add dependency to parent portal
        resource.getDependencies().add(portal.getPath());

        // Add portal bean in request attributes, so it's can be used in jsp
        renderContext.getRequest().setAttribute("portalContext", portal);

        // Fix site in rendercontext in case of user portal
        if(!portal.isModel()){
            JCRSiteNode site = JahiaSitesService.getInstance().getSite(portal.getSiteId(), resource.getNode().getSession());
            renderContext.setSite(site);
            renderContext.setSiteInfo(new SiteInfo(site));
        }
        return null;
    }

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }
}
