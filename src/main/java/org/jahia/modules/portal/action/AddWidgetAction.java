/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2017 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.portal.action;

import org.apache.commons.collections.CollectionUtils;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.portal.PortalConstants;
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
    private static final String COL_PARAM = "col";
    private static final String BEFORE_WIDGET_PARAM = "beforeWidget";

    private PortalService portalService;

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
                                  JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        List<String> nodetype =  parameters.get(NODETYPE_PARAM);
        List<String> names =  parameters.get(NAME_PARAM);
        List<String> col =  parameters.get(COL_PARAM);
        List<String> beforeWidget =  parameters.get(BEFORE_WIDGET_PARAM);

        if(CollectionUtils.isNotEmpty(nodetype) && CollectionUtils.isNotEmpty(col)){
            String name = CollectionUtils.isNotEmpty(names) ? names.get(0) : null;
            String beforeWidgetPath = CollectionUtils.isNotEmpty(beforeWidget) ? beforeWidget.get(0) : null;
            JCRNodeWrapper widgetNode = portalService.addWidgetToPortal(resource.getNode(), nodetype.get(0), name, col.get(0), beforeWidgetPath, session);
            if(widgetNode != null){
                JSONObject result = new JSONObject();
                result.put("id", widgetNode.getIdentifier());
                result.put("col_id", widgetNode.getParent().getIdentifier());
                result.put("path", widgetNode.getPath());
                result.put("isGadget", widgetNode.isNodeType(PortalConstants.JMIX_PORTAL_GADGET));

                return new ActionResult(HttpServletResponse.SC_OK, null, result);
            }
        }

        return ActionResult.INTERNAL_ERROR_JSON;
    }
}
