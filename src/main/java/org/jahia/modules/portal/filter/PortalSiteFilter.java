package org.jahia.modules.portal.filter;

import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
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
        JCRNodeWrapper portalNode = null;
        if(resource.getNode().isNodeType(PortalConstants.JMIX_PORTAL)){
            portalNode = resource.getNode();
        } else {
            portalNode = JCRContentUtils.getParentOfType(resource.getNode(), PortalConstants.JMIX_PORTAL);
        }
        if(portalNode != null){
            portalService.fixPortalSiteInContext(renderContext, portalNode);
        }
        
        return null;
    }
}
