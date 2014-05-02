package org.jahia.modules.portal.filter;

import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;

/**
 * Created by kevan on 02/05/14.
 */
public class PortalSiteFilter extends AbstractFilter{
    PortalService portalService;

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        portalService.fixPortalSiteInContext(renderContext, resource.getNode().getPath(), resource.getNode().getSession());
        return null;
    }
}
