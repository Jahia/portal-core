package org.jahia.modules.portal.filter;

import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 22/01/14
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class PortalSiteFilter extends AbstractFilter{
    PortalService portalService;

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        portalService.fixPortalSiteInContext(renderContext, resource.getNode());
        return null;
    }
}
