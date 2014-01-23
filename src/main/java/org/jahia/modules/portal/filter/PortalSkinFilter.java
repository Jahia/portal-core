package org.jahia.modules.portal.filter;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.taglibs.jcr.node.JCRTagUtils;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 06/01/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public class PortalSkinFilter extends AbstractFilter {
    private String defaultPortalWidgetsSkin;

    public void setDefaultPortalWidgetsSkin(String defaultPortalWidgetSkin) {
        this.defaultPortalWidgetsSkin = defaultPortalWidgetSkin;
    }

    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        JCRNodeWrapper portalTabNode = JCRTagUtils.getParentOfType(resource.getNode(), PortalConstants.JNT_PORTAL_TAB);
        if (portalTabNode != null){
            String skin = portalTabNode.getPropertyAsString(PortalConstants.J_WIDGETS_SKIN);
            if(StringUtils.isNotEmpty(skin)){
                resource.pushWrapper(skin);
                return null;
            }
        }

        resource.pushWrapper(defaultPortalWidgetsSkin);
        return null;
    }
}