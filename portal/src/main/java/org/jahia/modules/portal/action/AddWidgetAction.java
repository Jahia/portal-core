package org.jahia.modules.portal.action;

import org.apache.commons.collections.CollectionUtils;
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
 * Date: 03/01/14
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
public class AddWidgetAction extends Action{
    private static final String NODETYPE_PARAM = "nodetype";
    private static final String NAME_PARAM = "name";

    private PortalService portalService;

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
                                  JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        List<String> nodetype =  parameters.get(NODETYPE_PARAM);
        List<String> name =  parameters.get(NAME_PARAM);
        if(CollectionUtils.isNotEmpty(nodetype) && CollectionUtils.isNotEmpty(name)){
            JCRNodeWrapper widgetNode = portalService.addWidgetToPortal(resource.getNode(), nodetype.get(0), name.get(0), session);
            if(widgetNode != null){
                JSONObject result = new JSONObject();
                result.put("path", widgetNode.getPath());
                return new ActionResult(HttpServletResponse.SC_OK, null, result);
            }
        }

        return ActionResult.INTERNAL_ERROR_JSON;
    }
}
