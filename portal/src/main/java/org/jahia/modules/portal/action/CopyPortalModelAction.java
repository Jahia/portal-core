package org.jahia.modules.portal.action;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.content.JCRNodeWrapper;
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
 * Date: 22/01/14
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class CopyPortalModelAction extends Action{
    PortalService portalService;

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        JCRNodeWrapper portal = portalService.initUserPortalFromModel(resource.getNode(), session);
        if(portal != null){
            JSONObject result = new JSONObject();
            result.put("path", portal.getPath());

            return new ActionResult(HttpServletResponse.SC_OK, null, result);
        }else {
            return ActionResult.INTERNAL_ERROR_JSON;
        }
    }
}
