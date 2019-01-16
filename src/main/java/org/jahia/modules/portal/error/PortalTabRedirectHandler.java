/*
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2019 Jahia Solutions Group SA. All rights reserved.
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
