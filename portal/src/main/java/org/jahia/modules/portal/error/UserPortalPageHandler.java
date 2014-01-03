package org.jahia.modules.portal.error;

import org.jahia.bin.errors.ErrorHandler;

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


    @Override
    public boolean handle(Throwable e, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!(e instanceof PathNotFoundException)) {
            return false;
        }


        // TODO
            /*JahiaUser user = JCRSessionFactory.getInstance().getCurrentUser();
            RenderContext renderContext = new RenderContext(request, response, user);
            renderContext.setWorkspace("default");
            renderContext.setEditMode(false);
            renderContext.setServletPath("/cms/render");

            Resource resource = new Resource(pageNode, "html", null, "page");
            renderContext.setMainResource(resource);
            renderContext.setSite(pageNode.getResolveSite());

            String out = RenderService.getInstance().render(resource, renderContext);
            response.setContentType(renderContext.getContentType() != null ? renderContext.getContentType() : "text/html; charset=UTF-8");
            response.getWriter().print(out);
            return true;*/
        return false;
    }
}
