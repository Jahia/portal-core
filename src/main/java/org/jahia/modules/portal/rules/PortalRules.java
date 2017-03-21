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
package org.jahia.modules.portal.rules;

import org.drools.core.spi.KnowledgeHelper;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.rules.AddedNodeFact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.Set;

/**
 * Created by kevan on 29/04/14.
 */
public class PortalRules {
    private static Logger logger = LoggerFactory.getLogger(PortalRules.class);

    public void updatePortalAccessibility(AddedNodeFact node, KnowledgeHelper drools) {
        try {
            String accessibility = node.getNode().getProperty(PortalConstants.J_ACCESSIBILITY).getString();
            JCRNodeWrapper jcrNodeWrapper = node.getNode();
            Set<String> roles = Collections.singleton("reader");
            if(accessibility.equals("me")){
                jcrNodeWrapper.denyRoles("u:guest", roles);
                jcrNodeWrapper.denyRoles("g:users", roles);
            } else if(accessibility.equals("all")){
                jcrNodeWrapper.grantRoles("u:guest", roles);
                jcrNodeWrapper.grantRoles("g:users", roles);
            } else if(accessibility.equals("users")){
                jcrNodeWrapper.denyRoles("u:guest", roles);
                jcrNodeWrapper.grantRoles("g:users", roles);
            }
            node.getNode().getSession().save();
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
