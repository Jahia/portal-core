package org.jahia.modules.portal.action;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 03/01/14
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
public class AddWidgetsAction extends Action{
    private static final String NODETYPES_PARAM = "nodetypes";

    private PortalService portalService;

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
                                  JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        List<String> nodetypes =  parameters.get(NODETYPES_PARAM);
        if(nodetypes != null && nodetypes.size() > 0){
            portalService.addWidgetsToPortal(resource.getNode(), nodetypes, session);
        }

        return ActionResult.OK_JSON;
    }
}
