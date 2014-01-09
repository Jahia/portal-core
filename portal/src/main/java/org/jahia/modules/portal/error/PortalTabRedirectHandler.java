package org.jahia.modules.portal.error;

import org.apache.commons.lang.StringUtils;
import org.jahia.bin.errors.ErrorHandler;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.URLResolver;
import org.jahia.services.render.URLResolverFactory;
import org.slf4j.Logger;

import javax.jcr.PathNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 30/12/13
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class PortalTabRedirectHandler implements ErrorHandler {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PortalTabRedirectHandler.class);

    private URLResolverFactory urlResolverFactory;
    private PortalService portalService;

    public void setUrlResolverFactory(URLResolverFactory urlResolverFactory) {
        this.urlResolverFactory = urlResolverFactory;
    }

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    @Override
    public boolean handle(Throwable e, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!(e instanceof PathNotFoundException)) {
            return false;
        }
        try {
            URLResolver urlResolver = urlResolverFactory.createURLResolver(request.getPathInfo(), request.getServerName(), request);
            JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentUserSession(urlResolver.getWorkspace(), urlResolver.getLocale());
            JCRNodeWrapper portalNode = session.getNode(urlResolver.getPath());
            if(portalNode != null && portalNode.isNodeType(PortalConstants.JMIX_PORTAL)){
                // redirect to first tab
                List<JCRNodeWrapper> portalTabs = portalService.getPortalTabs(portalNode, session);
                if(portalTabs.size() > 0){
                    String link = request.getContextPath() + request.getServletPath() + "/" + StringUtils.substringBefore(
                            request.getPathInfo().substring(1),
                            "/") + "/" + urlResolver.getWorkspace() + "/" + urlResolver.getLocale() + portalTabs.get(0).getPath() + ".html";
                    response.sendRedirect(link);
                    return true;
                }
            }

        }catch (Exception e1){
            logger.error(e1.getMessage(), e1);
        }

        return false;
    }
}
