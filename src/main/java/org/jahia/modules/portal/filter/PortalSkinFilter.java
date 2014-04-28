package org.jahia.modules.portal.filter;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.bean.PortalContext;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;

import javax.jcr.RepositoryException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 06/01/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public class PortalSkinFilter extends AbstractFilter {

    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        // Add cache dependency to portal tab
        PortalContext portal = (PortalContext) renderContext.getRequest().getAttribute("portalContext");
        resource.getDependencies().add(portal.getTabPath());
        resource.getDependencies().add(portal.getPath());
        Serializable skipSkinParam = resource.getModuleParams().get("skipSkin");
        boolean skipSkin = skipSkinParam != null && Boolean.parseBoolean(skipSkinParam.toString());
        if(!skipSkin){
            if (portal.isLock()){
                resource.pushWrapper("locked");
            }else {
                pushWidgetSkinWrapperForNode(resource.getNode(), resource);
            }
        }
        return null;
    }

    private void pushWidgetSkinWrapperForNode(JCRNodeWrapper node, Resource resource) throws RepositoryException {
        if(node.hasProperty(PortalConstants.J_WIDGET_SKIN) && StringUtils.isNotEmpty(node.getPropertyAsString(PortalConstants.J_WIDGET_SKIN))){
            resource.pushWrapper(node.getPropertyAsString(PortalConstants.J_WIDGET_SKIN));
        }else {
            JCRNodeWrapper parentNodeWithWidgetSkinMixin = JCRContentUtils.getParentOfType(node, PortalConstants.JMIX_PORTAL_WIDGET_SKIN);
            if(parentNodeWithWidgetSkinMixin != null){
                pushWidgetSkinWrapperForNode(parentNodeWithWidgetSkinMixin, resource);
            }
        }
    }
}