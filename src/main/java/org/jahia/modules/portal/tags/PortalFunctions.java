package org.jahia.modules.portal.tags;

import org.apache.commons.collections.CollectionUtils;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.render.RenderService;
import org.jahia.services.render.View;
import org.jahia.utils.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeTypeIterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 02/01/14
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PortalFunctions {
    private static final Logger logger = LoggerFactory.getLogger(PortalFunctions.class);

    private static PortalService portalService;

    @Autowired(required = true)
    public void setPortalService(PortalService portalService) {
        PortalFunctions.portalService = portalService;
    }

    public static NodeTypeIterator getNodeTypes() {
        return NodeTypeRegistry.getInstance().getAllNodeTypes();
    }

    public static String getI18nNodetype(JahiaTemplatesPackage pkg, String key, Locale locale) {
        try {
            return Messages.get(pkg, key, locale);
        }catch (Exception e){
            return key;
        }
    }

    public static JCRNodeWrapper getTemplateNodeForName(String name, JCRNodeWrapper portalNode) {
        try {
            return portalService.getPortalTabTemplateNode(name, portalNode.getPropertyAsString(PortalConstants.J_TEMPLATE_ROOT_PATH), portalNode.getSession());
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static List<JCRNodeWrapper> getPortalTabTemplates(JCRNodeWrapper portalNode) {
        try {
            return portalService.getPortalTabTemplates(portalNode.getPropertyAsString(PortalConstants.J_TEMPLATE_ROOT_PATH), portalNode.getSession());
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static View getSpecificView(String nt, String view, JCRNodeWrapper portalNode) {
        SortedSet<View> skins = getViewSet(nt, portalNode);
        if (CollectionUtils.isNotEmpty(skins)) {
            for (View skin : skins) {
                if (skin.getKey().equals(view)) {
                    return skin;
                }
            }
        }
        return null;
    }

    public static SortedSet<View> getViewSet(String nt, JCRNodeWrapper portalNode){
        try {
            return RenderService.getInstance().getViewsSet(NodeTypeRegistry.getInstance().getNodeType(nt), portalService.getPortalSite(portalNode), "html");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static boolean userPortalExist(JCRNodeWrapper modelNode){
        JCRNodeWrapper userPortal = portalService.getUserPortalByModel(modelNode);
        return userPortal != null;
    }

    public static JCRNodeWrapper getWidgetNode(String widgetId, JCRNodeWrapper portalNode){
        JCRNodeWrapper widgetNode = null;
        try {
            widgetNode = portalNode.getSession().getNodeByIdentifier(widgetId);
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        return widgetNode;
    }

    public static Set<JCRNodeWrapper> getUserPortalsBySite(String siteKey) {
        return portalService.getUserPortalsBySite(siteKey);
    }
}
