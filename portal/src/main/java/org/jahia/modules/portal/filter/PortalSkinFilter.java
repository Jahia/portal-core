package org.jahia.modules.portal.filter;

import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 06/01/14
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public class PortalSkinFilter extends AbstractFilter {
    private String skin;

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        resource.pushWrapper(skin);
        return null;
    }
}