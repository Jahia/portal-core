/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2015 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ======================================================================================
 *
 *     IF YOU DECIDE TO CHOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     "This program is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation; either version 2
 *     of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 *     As a special exception to the terms and conditions of version 2.0 of
 *     the GPL (or any later version), you may redistribute this Program in connection
 *     with Free/Libre and Open Source Software ("FLOSS") applications as described
 *     in Jahia's FLOSS exception. You should have received a copy of the text
 *     describing the FLOSS exception, also available here:
 *     http://www.jahia.com/license"
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ======================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 *
 *
 * ==========================================================================================
 * =                                   ABOUT JAHIA                                          =
 * ==========================================================================================
 *
 *     Rooted in Open Source CMS, Jahia’s Digital Industrialization paradigm is about
 *     streamlining Enterprise digital projects across channels to truly control
 *     time-to-market and TCO, project after project.
 *     Putting an end to “the Tunnel effect”, the Jahia Studio enables IT and
 *     marketing teams to collaboratively and iteratively build cutting-edge
 *     online business solutions.
 *     These, in turn, are securely and easily deployed as modules and apps,
 *     reusable across any digital projects, thanks to the Jahia Private App Store Software.
 *     Each solution provided by Jahia stems from this overarching vision:
 *     Digital Factory, Workspace Factory, Portal Factory and eCommerce Factory.
 *     Founded in 2002 and headquartered in Geneva, Switzerland,
 *     Jahia Solutions Group has its North American headquarters in Washington DC,
 *     with offices in Chicago, Toronto and throughout Europe.
 *     Jahia counts hundreds of global brands and governmental organizations
 *     among its loyal customers, in more than 20 countries across the globe.
 *
 *     For more information, please visit http://www.jahia.com
 */
package org.jahia.modules.portal.filter;

import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.*;
import org.jahia.modules.portal.service.bean.*;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;

import javax.jcr.NodeIterator;
import javax.jcr.query.QueryManager;
import java.util.LinkedList;


/**
 * Created by kevan on 23/04/14.
 */
public class PortalInitFilter extends AbstractFilter{

    private PortalService portalService;

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {

        if(renderContext.getRequest().getAttribute("portalContext") != null){
            // Portal already init
            return null;
        }

        JCRNodeWrapper portalTabNode = renderContext.getMainResource().getNode().isNodeType(PortalConstants.JNT_PORTAL_TAB)
                ? renderContext.getMainResource().getNode() : JCRContentUtils.getParentOfType(renderContext.getMainResource().getNode(), PortalConstants.JNT_PORTAL_TAB);

        if (portalTabNode != null){
            JCRNodeWrapper portalNode = JCRContentUtils.getParentOfType(portalTabNode, PortalConstants.JMIX_PORTAL);
            boolean updateLastViewed = resource.getNode().isNodeType(PortalConstants.JNT_PORTAL_TAB);
            PortalContext portalContext = portalService.buildPortalContext(renderContext, portalTabNode, resource.getNode().getSession(), updateLastViewed);

            //set tabs
            portalContext.setPortalTabs(new LinkedList<PortalTab>());
            QueryManager queryManager = resource.getNode().getSession().getWorkspace().getQueryManager();
            if (queryManager != null) {
                NodeIterator result = portalNode.getNodes();

                while (result.hasNext()) {
                    JCRNodeWrapper tabNode = (JCRNodeWrapper) result.next();
                    if(tabNode.isNodeType(PortalConstants.JNT_PORTAL_TAB)){
                        PortalTab portalTab = new PortalTab();
                        portalTab.setPath(tabNode.getPath());
                        portalTab.setDisplayableName(tabNode.getDisplayableName());
                        portalTab.setUrl(portalContext.getBaseUrl() + tabNode.getPath() + ".html");
                        portalTab.setCurrent(tabNode.getIdentifier().equals(portalTabNode.getIdentifier()));
                        portalTab.setTemplateKey(tabNode.getProperty(PortalConstants.J_TEMPLATE_NAME).getString());
                        portalTab.setSkinKey(tabNode.getProperty(PortalConstants.J_WIDGET_SKIN).getString());
                        portalTab.setAccessibility(tabNode.hasProperty(PortalConstants.J_ACCESSIBILITY) ? tabNode.getProperty(PortalConstants.J_ACCESSIBILITY).getString() : "me");
                        portalContext.getPortalTabs().add(portalTab);
                        resource.getDependencies().add(portalTab.getPath());
                    }
                }
            }

            // Add dependency to model portal
            if(!portalContext.isModel()){
                resource.getDependencies().add(portalContext.getModelPath());
            }
            // Add dependency to parent portal
            resource.getDependencies().add(portalContext.getPath());

            // Add portal bean in request attributes, so it's can be used in jsp
            renderContext.getRequest().setAttribute("portalContext", portalContext);
        }
        return null;
    }

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }
}
