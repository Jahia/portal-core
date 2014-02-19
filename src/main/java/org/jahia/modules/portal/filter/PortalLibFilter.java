package org.jahia.modules.portal.filter;


import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 30/12/13
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class PortalLibFilter extends AbstractFilter {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PortalLibFilter.class);

    private static final String JS_API_FILE = "jahia-portal.js";

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        String out = previousOut;

        // add portal API lib
        String path = URLEncoder.encode("/modules/" + renderContext.getMainResource().getNode().getPrimaryNodeType().getTemplatePackage().getBundle().getSymbolicName()
                + "/javascript/" + JS_API_FILE, "UTF-8");
        out += ("<jahia:resource type='javascript' path='" + path + "' insert='false' resource='" + JS_API_FILE + "'/>");

        return out;
    }
}
