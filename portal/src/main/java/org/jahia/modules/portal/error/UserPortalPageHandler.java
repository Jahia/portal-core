package org.jahia.modules.portal.error;

import org.jahia.bin.errors.ErrorHandler;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.render.URLResolverFactory;
import org.slf4j.Logger;

import javax.jcr.PathNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 20/12/13
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */
public class UserPortalPageHandler implements ErrorHandler {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserPortalPageHandler.class);

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

        /*try {
            URLResolver urlResolver = urlResolverFactory.createURLResolver(request.getPathInfo(), request.getServerName(), request);
            JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentUserSession(urlResolver.getWorkspace(), urlResolver.getLocale());

            JahiaUser user = session.getUser();
            final RenderContext renderContext = new RenderContext(request, response, user);
            renderContext.setWorkspace("live");
            renderContext.setEditMode(false);
            renderContext.setServletPath("/cms/render");

            String[] explodedPath = request.getPathInfo().split("/");
            int indexOfPortalsRoot = ArrayUtils.indexOf(explodedPath, "portals");
            final String sitePath = "/sites/" + explodedPath[indexOfPortalsRoot + 1];
            final String portalName = explodedPath[indexOfPortalsRoot + 2];

            QueryManager queryManager = session.getWorkspace().getQueryManager();
            StringBuilder q = new StringBuilder();

            q.append("select * from [" + PortalConstants.JMIX_PORTAL + "] as p where isdescendantnode(p, ['").append(sitePath)
                    .append("'])  and p.['j:nodename'] = '").append(portalName).append("'");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            String out;
            NodeIterator nodes = query.execute().getNodes();
            while (nodes.hasNext()) {
                JCRNodeWrapper portalNode = (JCRNodeWrapper) nodes.next();
                List<JCRNodeWrapper> portalTabs = portalService.getPortalTabs(portalNode, session);

                if(portalTabs.size() > 0){
                    Resource resource = new Resource(portalTabs.get(0), "html", null, "page");
                    renderContext.setMainResource(resource);
                    renderContext.setSite(portalNode.getResolveSite());
                    renderContext.setSiteInfo(new SiteInfo(portalNode.getResolveSite()));
                    try {
                        out = RenderService.getInstance().render(resource, renderContext);
                        response.setContentType(renderContext.getContentType() != null ? renderContext.getContentType() : "text/html; charset=UTF-8");
                        response.getWriter().print(out);
                        return true;
                    } catch (RenderException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }

            }

            return false;
        } catch (Exception ex) {
            logger.error(e.getMessage(), ex);
        } */

        return false;
    }
}
