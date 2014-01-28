package org.jahia.modules.portal.service;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.sitesettings.form.PortalForm;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.SiteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 23/12/13
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class PortalService {
    private static Logger logger = LoggerFactory.getLogger(PortalService.class);
    private static final Comparator<? super JCRNodeWrapper> PORTALS_COMPARATOR = new Comparator<JCRNodeWrapper>() {

        public int compare(JCRNodeWrapper portalNode1, JCRNodeWrapper portalNode2) {
            // we invert the order to sort with most recent dates on top.
            return portalNode1.getDisplayableName().compareTo(portalNode2.getDisplayableName());
        }

    };

    public JCRNodeWrapper getPortalFolder(JCRNodeWrapper node, String folderName, boolean createIfNotExist) {
        try {
            JCRNodeWrapper portalFolder;
            try {
                return node.getNode(folderName);
            } catch (PathNotFoundException e) {
                if (createIfNotExist) {
                    return node.addNode(folderName, PortalConstants.JNT_PORTALS_FOLDER);
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving portal folder '" + folderName + "' under node " + node.getPath(), e);
        }
        return null;

    }

    public List<JCRNodeWrapper> getSitePortalModels(JCRSiteNode site, String orderBy, boolean orderAscending, JCRSessionWrapper session) {
        long timer = System.currentTimeMillis();

        final List<JCRNodeWrapper> portalsNode = new LinkedList<JCRNodeWrapper>();
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
                return portalsNode;
            }

            StringBuilder q = new StringBuilder();
            q.append("select * from [" + PortalConstants.JNT_PORTAL_MODEL + "] where isdescendantnode([").append(site.getPath())
                    .append("])");
            if (orderBy != null) {
                q.append(" order by [").append(orderBy).append("]").append(orderAscending ? "asc" : "desc");
            }
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            for (NodeIterator nodes = query.execute().getNodes(); nodes.hasNext(); ) {
                portalsNode.add((JCRNodeWrapper) nodes.next());
            }
        } catch (RepositoryException e) {
            logger.error("Error retrieving portals for site " + site.getDisplayableName(), e);
        }

        if (logger.isDebugEnabled()) {
            logger.info("Site portals search took " + (System.currentTimeMillis() - timer) + " ms. Returning " +
                    portalsNode.size() + " portal(s)");
        }

        return portalsNode;
    }

    public void createPortalModel(PortalForm form, JCRSiteNode site, JCRSessionWrapper session) throws RepositoryException {
        // Create portals root folder
        JCRNodeWrapper portalsRootFolderNode = getPortalFolder(session.getNode(site.getPath()), "portals", true);
        if (portalsRootFolderNode == null) {
            return;
        }

        // Create portal
        JCRNodeWrapper portalNode = portalsRootFolderNode.addNode(JCRContentUtils.generateNodeName(form.getName(), 32), PortalConstants.JNT_PORTAL_MODEL);
        portalNode.setProperty(PortalConstants.JCR_TITLE, form.getName());
        portalNode.setProperty(PortalConstants.J_TEMPLATE_ROOT_PATH, form.getTemplateRootPath());
        portalNode.setProperty(PortalConstants.J_FULL_TEMPLATE, form.getTemplateFull());
        setReadRoleForPortalModel(portalNode, false);

        // Create first tab
        JCRNodeWrapper portalTab = portalNode.addNode(JCRContentUtils.generateNodeName(form.getTabName(), 32), PortalConstants.JNT_PORTAL_TAB);
        portalTab.setProperty(PortalConstants.JCR_TITLE, form.getTabName());
        portalTab.setProperty(PortalConstants.J_WIDGETS_SKIN, form.getWidgetsSkin());
        List<JCRNodeWrapper> portalTabTemplates = getPortalTabTemplates(form.getTemplateRootPath(), session);
        if (portalTabTemplates.size() > 0) {
            portalTab.setProperty(PortalConstants.J_TEMPLATE_NAME, portalTabTemplates.get(0).getName());
        }

        session.save();
    }

    public List<JCRNodeWrapper> getPortalTabs(JCRNodeWrapper portalNode, JCRSessionWrapper session) {
        List<JCRNodeWrapper> portalTabs = new LinkedList<JCRNodeWrapper>();

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        if (queryManager == null) {
            logger.error("Unable to obtain QueryManager instance");
        }

        try {
            StringBuilder q = new StringBuilder();
            q.append("select * from [" + PortalConstants.JNT_PORTAL_TAB + "] as t where isdescendantnode(t, ['").append(portalNode.getPath())
                    .append("'])");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            NodeIterator nodes = query.execute().getNodes();
            while (nodes.hasNext()) {
                JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();
                portalTabs.add(node);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return portalTabs;
    }

    public List<JCRNodeWrapper> getPortalTabTemplates(String templateRootPath, JCRSessionWrapper session) {
        List<JCRNodeWrapper> portalTabTemplates = new LinkedList<JCRNodeWrapper>();

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        if (queryManager == null) {
            logger.error("Unable to obtain QueryManager instance");
        }

        try {
            JCRNodeWrapper templateRootNode = session.getNode(templateRootPath);
            StringBuilder q = new StringBuilder();
            q.append("select * from [" + PortalConstants.JNT_CONTENT_TEMPLATE + "] as t where isdescendantnode(t, ['").append(templateRootNode.getPath())
                    .append("'])  and contains(t.['" + PortalConstants.J_APPLY_ON + "'], '" + PortalConstants.JNT_PORTAL_TAB + "')");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            NodeIterator nodes = query.execute().getNodes();
            while (nodes.hasNext()) {
                JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();
                portalTabTemplates.add(node);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return portalTabTemplates;
    }

    public JCRNodeWrapper addWidgetToPortal(JCRNodeWrapper portalTabNode, String nodetype, String nodeName, JCRSessionWrapper session) {
        JCRNodeWrapper columnNode = getColumn(portalTabNode, 0);

        try {
            JCRNodeWrapper widget = columnNode.addNode(JCRContentUtils.findAvailableNodeName(columnNode, JCRContentUtils.generateNodeName(nodeName)), nodetype);
            widget.setProperty(PortalConstants.JCR_TITLE, nodeName);
            session.save();

            return widget;
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public JCRNodeWrapper getColumn(JCRNodeWrapper portalTabNode, int index) {
        String columnName = "col-" + index;
        JCRNodeWrapper columnNode;
        try {
            columnNode = portalTabNode.getNode(columnName);
            return columnNode;
        } catch (RepositoryException e) {
            try {
                columnNode = portalTabNode.addNode(JCRContentUtils.generateNodeName(columnName, 32), PortalConstants.JNT_PORTAL_COLUMN);
                return columnNode;
            } catch (RepositoryException e1) {
                logger.error(e.getMessage(), e);
            }
        }

        return null;
    }

    public JCRNodeWrapper getColumn(String path, JCRSessionWrapper sessionWrapper) {
        JCRNodeWrapper columnNode;
        try {
            columnNode = sessionWrapper.getNode(path);
        } catch (RepositoryException e) {
            try {
                JCRNodeWrapper portalTabNode = sessionWrapper.getNode(StringUtils.substringBeforeLast(path, "/"));
                columnNode = portalTabNode.addNode(JCRContentUtils.generateNodeName(StringUtils.substringAfterLast(path, "/"), 32), PortalConstants.JNT_PORTAL_COLUMN);
                sessionWrapper.save();
            } catch (RepositoryException e1) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }

        return columnNode;
    }

    public JCRNodeWrapper getPortalTabTemplateNode(String templateName, String templateRootPath, JCRSessionWrapper sessionWrapper) {
        QueryManager queryManager = sessionWrapper.getWorkspace().getQueryManager();
        if (queryManager == null) {
            logger.error("Unable to obtain QueryManager instance");
        }

        try {
            JCRNodeWrapper templateRootNode = sessionWrapper.getNode(templateRootPath);
            StringBuilder q = new StringBuilder();
            q.append("select * from [" + PortalConstants.JNT_CONTENT_TEMPLATE + "] as t where isdescendantnode(t, ['").append(templateRootNode.getPath())
                    .append("'])  and t.['j:nodename'] = '").append(templateName).append("'");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            NodeIterator nodes = query.execute().getNodes();
            while (nodes.hasNext()) {
                return (JCRNodeWrapper) nodes.next();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public void switchPortalModelActivation(JCRSessionWrapper sessionWrapper, String portalModelIdentifier, boolean enabled) {
        try {
            JCRNodeWrapper node = sessionWrapper.getNodeByUUID(portalModelIdentifier);
            node.setProperty(PortalConstants.J_ENABLED, enabled);
            setReadRoleForPortalModel(node, enabled);
            sessionWrapper.save();
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public JCRNodeWrapper getUserPortalByModel(JCRNodeWrapper modelNode) {
        try {
            JCRSessionWrapper sessionWrapper = modelNode.getSession();
            QueryManager queryManager = sessionWrapper.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
            }

            StringBuilder q = new StringBuilder();
            q.append("select * from [" + PortalConstants.JNT_PORTAL_USER + "] as p where isdescendantnode(p, ['").append(sessionWrapper.getUser().getLocalPath())
                    .append("'])  and p.['j:model'] = '").append(modelNode.getIdentifier()).append("'");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            NodeIterator nodes = query.execute().getNodes();
            while (nodes.hasNext()) {
                return (JCRNodeWrapper) nodes.next();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public SortedSet<JCRNodeWrapper> getUserPortalsBySite(String siteKey) {
        SortedSet<JCRNodeWrapper> portals = new TreeSet<JCRNodeWrapper>(PORTALS_COMPARATOR);
        try {
            JCRSessionWrapper sessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession("live");
            QueryManager queryManager = sessionWrapper.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
            }

            StringBuilder q = new StringBuilder();
            q.append("select * from [" + PortalConstants.JNT_PORTAL_MODEL + "] as p where isdescendantnode(p, ['").append("/sites/" + siteKey)
                    .append("'])");
            NodeIterator result = queryManager.createQuery(q.toString(), Query.JCR_SQL2).execute().getNodes();

            while (result.hasNext()) {
                JCRNodeWrapper modelNode = (JCRNodeWrapper) result.next();
                JCRNodeWrapper userPortalNode = getUserPortalByModel(modelNode);
                if (userPortalNode != null) {
                    // return user portal if exist
                    portals.add(userPortalNode);
                } else if (modelNode.hasProperty(PortalConstants.J_ENABLED) && modelNode.getProperty(PortalConstants.J_ENABLED).getBoolean()) {
                    // return model portal if this one is enabled
                    portals.add(modelNode);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return portals;
    }

    public JCRNodeWrapper initUserPortalFromModel(JCRNodeWrapper modelNode, JCRSessionWrapper sessionWrapper) {
        try {
            // Create/get portal folders
            JCRNodeWrapper portalFolderRoot = getPortalFolder(sessionWrapper.getNode(sessionWrapper.getUser().getLocalPath()), "portals", true);
            JCRNodeWrapper sitePortalFolder = getPortalFolder(portalFolderRoot, modelNode.getResolveSite().getSiteKey(), true);

            // Create portal
            JCRNodeWrapper portal = sitePortalFolder.addNode(modelNode.getName(), PortalConstants.JNT_PORTAL_USER);
            portal.setProperty(PortalConstants.J_TEMPLATE_ROOT_PATH, modelNode.getPropertyAsString(PortalConstants.J_TEMPLATE_ROOT_PATH));
            portal.setProperty(PortalConstants.JCR_TITLE, modelNode.getDisplayableName());
            portal.setProperty(PortalConstants.J_FULL_TEMPLATE, modelNode.getPropertyAsString(PortalConstants.J_FULL_TEMPLATE));
            portal.setProperty(PortalConstants.J_MODEL, modelNode);

            //copy tabs
            List<JCRNodeWrapper> tabNodes = getPortalTabs(modelNode, sessionWrapper);
            for (JCRNodeWrapper tabNode : tabNodes) {
                tabNode.copy(portal.getPath());
            }

            //set roles
            portal.denyRoles("g:users", Collections.singleton("reader"));
            portal.grantRoles(sessionWrapper.getUser().getUserKey(), Collections.singleton("reader"));
            portal.grantRoles(sessionWrapper.getUser().getUserKey(), Collections.singleton("owner"));

            sessionWrapper.save();

            return portal;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public void fixPortalSiteInContext(RenderContext renderContext, JCRNodeWrapper node) throws RepositoryException {
        JCRNodeWrapper portalNode = node.isNodeType(PortalConstants.JMIX_PORTAL) ? node : JCRContentUtils.getParentOfType(node, PortalConstants.JMIX_PORTAL);

        if (portalNode.isNodeType(PortalConstants.JMIX_HAS_MODEL)) {
            JCRSiteNode portalSite = getPortalSite(portalNode);
            renderContext.setSite(portalSite);
            renderContext.setSiteInfo(new SiteInfo(portalSite));
        }
    }

    public JCRSiteNode getPortalSite(JCRNodeWrapper portalNode) throws RepositoryException {
        if (portalNode.isNodeType(PortalConstants.JMIX_HAS_MODEL)) {
            return ((JCRNodeWrapper) portalNode.getProperty(PortalConstants.J_MODEL).getNode()).getResolveSite();
        } else {
            return portalNode.getResolveSite();
        }
    }

    private void setReadRoleForPortalModel(JCRNodeWrapper portalModelNode, boolean enabled) {
        Set<String> roles = Collections.singleton("reader");
        try {
            portalModelNode.denyRoles("u:guest", roles);

            if (enabled) {
                portalModelNode.grantRoles("g:users", roles);
            } else {
                portalModelNode.denyRoles("g:users", roles);
            }
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
