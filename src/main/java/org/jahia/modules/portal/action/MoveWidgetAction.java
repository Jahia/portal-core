package org.jahia.modules.portal.action;

import org.apache.commons.lang.StringUtils;
import org.jahia.ajax.gwt.helper.ContentManagerHelper;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 07/01/14
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class MoveWidgetAction extends Action {
    private ContentManagerHelper contentManager;
    private PortalService portalService;

    public void setContentManager(ContentManagerHelper contentManager) {
        this.contentManager = contentManager;
    }

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
                                  JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        String area = req.getParameter("toArea");
        String widgetPath = req.getParameter("widget");
        String onTopOfWidgetPath = req.getParameter("onTopOfWidget");
        String colPath = resource.getNode().getPath() + "/" + area;

        JCRSessionWrapper jcrSessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession(resource.getWorkspace(), resource.getLocale());

        if(StringUtils.isNotEmpty(onTopOfWidgetPath)){
            contentManager.moveOnTopOf(widgetPath, onTopOfWidgetPath, jcrSessionWrapper);
        } else {
            contentManager.moveAtEnd(widgetPath, portalService.getColumn(colPath, jcrSessionWrapper).getPath(), jcrSessionWrapper);
        }

        JSONObject result = new JSONObject();
        result.put("path", colPath + "/" + StringUtils.substringAfterLast(widgetPath, "/"));

        return new ActionResult(HttpServletResponse.SC_OK, null, result);
    }
}