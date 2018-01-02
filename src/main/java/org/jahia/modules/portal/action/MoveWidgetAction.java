/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2018 Jahia Solutions Group SA. All rights reserved.
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