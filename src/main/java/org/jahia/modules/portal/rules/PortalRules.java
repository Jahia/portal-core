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
