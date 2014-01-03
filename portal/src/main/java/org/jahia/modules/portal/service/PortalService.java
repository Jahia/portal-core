package org.jahia.modules.portal.service;

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
    private static final String JNT_PORTAL = "jnt:portal";
    private static final String JNT_PORTAL_MODEL = "jnt:portalModel";
    private static final String JNT_PORTALS_FOLDER = "jnt:portalsFolder";
    private static final String JNT_PORTAL_TAB = "jnt:portalTab";
    private static final String JNT_PORTAL_COLUMN = "jnt:portalColumn";

    public JCRNodeWrapper getSitePortalsRootFolder(JCRSiteNode site, JCRSessionWrapper session){
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
                return null;
            }

            StringBuilder q = new StringBuilder();
            q.append("select * from [" + JNT_PORTALS_FOLDER + "] where isdescendantnode([").append(site.getPath())
                    .append("])");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);
            NodeIterator nodes = query.execute().getNodes();
            return nodes.hasNext() ? (JCRNodeWrapper) nodes.next() : null;
        } catch (Exception e) {
            logger.error("Error retrieving portals root folder for site " + site.getDisplayableName(), e);
        }
        return null;
    }

    public List<JCRNodeWrapper> getSitePortalModels(JCRSiteNode site, String orderBy, boolean orderAscending, JCRSessionWrapper session){
        long timer = System.currentTimeMillis();

        final List<JCRNodeWrapper> portalsNode = new LinkedList<JCRNodeWrapper>();
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
                return portalsNode;
            }

            StringBuilder q = new StringBuilder();
            q.append("select * from [" + JNT_PORTAL_MODEL + "] where isdescendantnode([").append(site.getPath())
                    .append("])");
            if (orderBy != null) {
                q.append(" order by [").append(orderBy).append("]").append(orderAscending ? "asc" : "desc");
            }
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            for (NodeIterator nodes = query.execute().getNodes(); nodes.hasNext();) {
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
        if(portalsRootFolderNode == null){
            portalsRootFolderNode = site.addNode("portals", JNT_PORTALS_FOLDER);
        }
        
        // Create portal
        JCRNodeWrapper portalNode = portalsRootFolderNode.addNode(JCRContentUtils.generateNodeName(form.getName(), 32), JNT_PORTAL_MODEL);
        portalNode.setProperty("j:templateRoot", form.getTemplateRoot());

        // Create first tab
        JCRNodeWrapper portalTab = portalNode.addNode(JCRContentUtils.generateNodeName("home", 32), JNT_PORTAL_TAB);
        List<JCRNodeWrapper> portalTabTemplates = getPortalTabTemplates(form.getTemplateRoot(), session);
        if(portalTabTemplates.size() > 0){
            portalTab.setProperty("j:templateName", portalTabTemplates.get(0).getName());
        }

        session.save();
    }

    public List<JCRNodeWrapper> getPortalTabs(JCRNodeWrapper portalNode, JCRSessionWrapper session){
        List<JCRNodeWrapper> portalTabs = new LinkedList<JCRNodeWrapper>();

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        if (queryManager == null) {
            logger.error("Unable to obtain QueryManager instance");
        }

        try {
            StringBuilder q = new StringBuilder();
            q.append("select * from [jnt:portalTab] as t where isdescendantnode(t, ['").append(portalNode.getPath())
                    .append("'])");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            NodeIterator nodes = query.execute().getNodes();
            while (nodes.hasNext()){
                JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();
                portalTabs.add(node);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return portalTabs;
    }

    public List<JCRNodeWrapper> getPortalTabTemplates(String templateRootNodeUUID, JCRSessionWrapper session) {
        List<JCRNodeWrapper> portalTabTemplates = new LinkedList<JCRNodeWrapper>();

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        if (queryManager == null) {
            logger.error("Unable to obtain QueryManager instance");
        }

        try {
            JCRNodeWrapper templateRootNode = session.getNodeByUUID(templateRootNodeUUID);
            StringBuilder q = new StringBuilder();
            q.append("select * from [jnt:contentTemplate] as t where isdescendantnode(t, ['").append(templateRootNode.getPath())
                    .append("'])  and contains(t.['j:applyOn'], '" + JNT_PORTAL_TAB + "')");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            NodeIterator nodes = query.execute().getNodes();
            while (nodes.hasNext()){
                JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();
                portalTabTemplates.add(node);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return portalTabTemplates;
    }

    public void addWidgetsToPortal(JCRNodeWrapper portalTabNode, List<String> nodetypes, JCRSessionWrapper session){
        JCRNodeWrapper columnNode = getColumn(portalTabNode, 0, session);

        for (String nodetype : nodetypes){
            try {
                columnNode.addNode(JCRContentUtils.generateNodeName("portal component", 32), nodetype);
                session.save();
            } catch (RepositoryException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public JCRNodeWrapper getColumn(JCRNodeWrapper portalTabNode, int index, JCRSessionWrapper session){
        QueryManager queryManager = session.getWorkspace().getQueryManager();

        if (queryManager == null) {
            logger.error("Unable to obtain QueryManager instance");
        }

        try {
            // Get first column in tab / create if not exist
            StringBuilder q = new StringBuilder();
            String columnName = "col-" + index;
            q.append("select * from [" + JNT_PORTAL_COLUMN + "] as c where isdescendantnode(c, ['").append(portalTabNode.getPath())
                    .append("']) and c.name = '" + columnName + "'");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            NodeIterator nodes = query.execute().getNodes();
            JCRNodeWrapper columnNode;
            if(nodes.hasNext()){
                columnNode = (JCRNodeWrapper) nodes.next();
            } else {
                columnNode = portalTabNode.addNode(JCRContentUtils.generateNodeName(columnName, 32), JNT_PORTAL_COLUMN);
            }

            return columnNode;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
