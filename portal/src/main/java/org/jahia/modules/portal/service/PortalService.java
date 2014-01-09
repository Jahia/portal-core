package org.jahia.modules.portal.service;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.sitesettings.form.PortalForm;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 23/12/13
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class PortalService {
    private static Logger logger = LoggerFactory.getLogger(PortalService.class);

    public JCRNodeWrapper getSitePortalsRootFolder(JCRSiteNode site, JCRSessionWrapper session) {
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
                return null;
            }

            StringBuilder q = new StringBuilder();
            q.append("select * from [" + PortalConstants.JNT_PORTALS_FOLDER + "] where isdescendantnode([").append(site.getPath())
                    .append("])");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);
            NodeIterator nodes = query.execute().getNodes();
            return nodes.hasNext() ? (JCRNodeWrapper) nodes.next() : null;
        } catch (Exception e) {
            logger.error("Error retrieving portals root folder for site " + site.getDisplayableName(), e);
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
        JCRNodeWrapper portalsRootFolderNode = getSitePortalsRootFolder(site, session);
        if (portalsRootFolderNode == null) {
            portalsRootFolderNode = site.addNode("portals", PortalConstants.JNT_PORTALS_FOLDER);
        }

        // Create portal
        JCRNodeWrapper portalNode = portalsRootFolderNode.addNode(JCRContentUtils.generateNodeName(form.getName(), 32), PortalConstants.JNT_PORTAL_MODEL);
        portalNode.setProperty(PortalConstants.J_TEMPLATE_ROOT_PATH, form.getTemplateRootPath());

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
}
