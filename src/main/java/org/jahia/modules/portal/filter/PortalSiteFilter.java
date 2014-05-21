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
        JCRNodeWrapper portalTabNode = renderContext.getMainResource().getNode().isNodeType(PortalConstants.JNT_PORTAL_TAB)
                ? renderContext.getMainResource().getNode() : JCRContentUtils.getParentOfType(renderContext.getMainResource().getNode(), PortalConstants.JNT_PORTAL_TAB);

        portalService.fixPortalSiteInContext(renderContext, portalTabNode.getParent());
        
        return null;
    }
}
