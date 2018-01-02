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
package org.jahia.modules.portal.service;

import org.apache.commons.lang.StringUtils;
import org.jahia.ajax.gwt.helper.ContentManagerHelper;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.bean.PortalContext;
import org.jahia.modules.portal.service.bean.PortalKeyNameObject;
import org.jahia.modules.portal.service.bean.PortalWidgetType;
import org.jahia.modules.portal.service.bean.PortalWidgetTypeView;
import org.jahia.modules.portal.sitesettings.form.PortalForm;
import org.jahia.modules.portal.sitesettings.form.PortalModelForm;
import org.jahia.services.content.*;
import org.jahia.services.content.decorator.JCRGroupNode;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderService;
import org.jahia.services.render.SiteInfo;
import org.jahia.services.render.View;
import org.jahia.services.sites.JahiaSitesService;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.JahiaGroupManagerService;
import org.jahia.utils.i18n.Messages;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeTypeIterator;
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
            return portalNode1.getDisplayableName().compareTo(portalNode2.getDisplayableName());
        }

    };

    private static final Comparator<? super ExtendedNodeType> NODE_TYPES_COMPARATOR = new Comparator<ExtendedNodeType>() {

        public int compare(ExtendedNodeType nodeType1, ExtendedNodeType nodeType2) {
            return nodeType1.getName().compareTo(nodeType2.getName());
        }
    };

    private static final Comparator<? super PortalWidgetType> WIDGET_TYPES_COMPARATOR = new Comparator<PortalWidgetType>() {
        @Override
        public int compare(PortalWidgetType o1, PortalWidgetType o2) {
            return o1.getDisplayableName().compareTo(o2.getDisplayableName());
        }
    };

    private ContentManagerHelper contentManager;

    private JahiaGroupManagerService groupManagerService;

    public void setContentManager(ContentManagerHelper contentManager) {
        this.contentManager = contentManager;
    }

    public void setGroupManagerService(JahiaGroupManagerService groupManagerService) {
        this.groupManagerService = groupManagerService;
    }

    public JCRNodeWrapper getPortalFolder(JCRNodeWrapper node, String folderName, boolean createIfNotExist) {
        try {
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

    public void createPortalModel(PortalModelForm form, JCRSiteNode site, JCRSessionWrapper session) throws RepositoryException {
        // Create portals root folder
        JCRNodeWrapper portalsRootFolderNode = getPortalFolder(session.getNode(site.getPath()), "portals", true);
        if (portalsRootFolderNode == null) {
            return;
        }

        // Create portal
        JCRNodeWrapper portalNode = portalsRootFolderNode.addNode(JCRContentUtils.generateNodeName(form.getPortal().getName(), 32), PortalConstants.JNT_PORTAL_MODEL);
        portalNode.setProperty(PortalConstants.JCR_TITLE, form.getPortal().getName());
        portalNode.setProperty(PortalConstants.J_TEMPLATE_ROOT_PATH, form.getTemplateRootPath());
        if (StringUtils.isNotEmpty(form.getPortal().getTemplateFull())) {
            portalNode.setProperty(PortalConstants.J_FULL_TEMPLATE, getPortalTabTemplate(form.getPortal().getTemplateFull(),
                    JCRSessionFactory.getInstance().getCurrentUserSession()).getName());
        } else {
            portalNode.setProperty(PortalConstants.J_FULL_TEMPLATE, "");
        }
        portalNode.setProperty(PortalConstants.J_ALLOW_CUSTOMIZATION, form.getPortal().getAllowCustomization());
        portalNode.setProperty(PortalConstants.J_ALLOWED_WIDGET_TYPES, form.getPortal().getAllowedWidgetTypes());
        setReadRoleForPortalModel(portalNode, false);

        // Create first tab
        JCRNodeWrapper portalTab = portalNode.addNode(JCRContentUtils.generateNodeName(form.getTabName(), 32), PortalConstants.JNT_PORTAL_TAB);
        portalTab.setProperty(PortalConstants.JCR_TITLE, form.getTabName());
        portalTab.setProperty(PortalConstants.J_WIDGET_SKIN, form.getTabWidgetSkin());
        List<JCRNodeWrapper> portalTabTemplates = getPortalTabTemplates(form.getTemplateRootPath(), JCRSessionFactory.getInstance().getCurrentUserSession());
        if (portalTabTemplates.size() > 0) {
            portalTab.setProperty(PortalConstants.J_TEMPLATE_NAME, portalTabTemplates.get(0).getName());
        }

        session.save();
    }

    public void updatePortalModel(PortalForm form, JCRSessionWrapper sessionWrapper) throws RepositoryException {
        JCRNodeWrapper portalModelNode = sessionWrapper.getNodeByIdentifier(form.getPortalModelIdentifier());
        portalModelNode.setProperty(PortalConstants.JCR_TITLE, form.getName());
        portalModelNode.setProperty(PortalConstants.J_ALLOW_CUSTOMIZATION, form.getAllowCustomization());
        // TODO: make it works (update full template)
        //portalModelNode.setProperty(PortalConstants.J_FULL_TEMPLATE, form.getTemplateFull());
        portalModelNode.setProperty(PortalConstants.J_ALLOWED_WIDGET_TYPES, form.getAllowedWidgetTypes());
        sessionWrapper.save();
    }

    public List<JCRNodeWrapper> getPortalTabs(JCRNodeWrapper portalNode, JCRSessionWrapper session) {
        List<JCRNodeWrapper> portalTabs = new LinkedList<JCRNodeWrapper>();

        try {
            NodeIterator nodes = portalNode.getNodes();
            while (nodes.hasNext()) {
                JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();
                if (node.isNodeType(PortalConstants.JNT_PORTAL_TAB)) {
                    portalTabs.add(node);
                }
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
                    .append("']) and contains(t.['" + PortalConstants.J_APPLY_ON + "'], '" + PortalConstants.JNT_PORTAL_TAB + "')");
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

    public JCRNodeWrapper getPortalTabTemplate(String templatePath, JCRSessionWrapper sessionWrapper) {
        JCRNodeWrapper templatePortalNode = null;
        try {
            JCRNodeWrapper templateNode = sessionWrapper.getNode(templatePath);
            for (JCRValueWrapper value : templateNode.getProperty(PortalConstants.J_APPLY_ON).getValues()) {
                if (value.getString().equals(PortalConstants.JNT_PORTAL_TAB)) {
                    templatePortalNode = templateNode;
                }
            }
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }

        return templatePortalNode;
    }

    public JCRNodeWrapper addWidgetToPortal(JCRNodeWrapper portalTabNode, String nodetypeName, String nodeName, String colname, String beforeNodePath, JCRSessionWrapper session) {
        JCRNodeWrapper columnNode = getColumn(portalTabNode, colname);

        try {
            if (StringUtils.isEmpty(nodeName)) {
                ExtendedNodeType nodetype = NodeTypeRegistry.getInstance().getNodeType(nodetypeName);
                nodeName = getI18NodeTypeName(nodetype, session.getLocale());
            }
            JCRNodeWrapper widget = columnNode.addNode(JCRContentUtils.findAvailableNodeName(columnNode, JCRContentUtils.generateNodeName(nodeName)), nodetypeName);
            if (StringUtils.isNotEmpty(beforeNodePath)) {
                contentManager.moveOnTopOf(widget.getPath(), beforeNodePath, session);
            }
            widget.setProperty(PortalConstants.JCR_TITLE, nodeName);
            if (portalIsModel(portalTabNode.getParent())) {
                widget.addMixin(PortalConstants.JMIX_PORTAL_WIDGET_MODEL);
            }
            session.save();

            return widget;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public String getI18NodeTypeName(ExtendedNodeType nodeType, Locale locale) {
        try {
            return Messages.get(nodeType.getTemplatePackage(), nodeType.getName().replace(":", "_"), locale);
        } catch (Exception e) {
            return nodeType.getName();
        }
    }

    public JCRNodeWrapper getColumn(JCRNodeWrapper portalTabNode, String name) {
        JCRNodeWrapper columnNode;
        try {
            columnNode = portalTabNode.getNode(name);
            return columnNode;
        } catch (RepositoryException e) {
            try {
                columnNode = portalTabNode.addNode(name, PortalConstants.JNT_PORTAL_COLUMN);
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

    public boolean isPortalModelEnabled(JCRNodeWrapper modelNode) throws RepositoryException {
        return modelNode.hasProperty(PortalConstants.J_ENABLED) && modelNode.getProperty(PortalConstants.J_ENABLED).getBoolean();
    }

    public JCRNodeWrapper getUserPortalByModel(String modelId, JCRSessionWrapper sessionWrapper) {
        try {
            QueryManager queryManager = sessionWrapper.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
            }

            StringBuilder q = new StringBuilder();
            q.append("select * from [" + PortalConstants.JNT_PORTAL_USER + "] as p where isdescendantnode(p, ['").append(sessionWrapper.getUser().getLocalPath())
                    .append("'])  and p.['j:model'] = '").append(modelId).append("'");
            Query query = queryManager.createQuery(q.toString(), Query.JCR_SQL2);

            NodeIterator nodes = query.execute().getNodes();
            if (nodes.hasNext()) {
                return (JCRNodeWrapper) nodes.next();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public SortedSet<JCRNodeWrapper> getUserPortalsBySite(String siteKey, Locale locale) {
        SortedSet<JCRNodeWrapper> portals = new TreeSet<JCRNodeWrapper>(PORTALS_COMPARATOR);
        List<String> userPortalModelIds = new ArrayList<String>();
        try {
            JCRSessionWrapper sessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession("live", locale);
            QueryManager queryManager = sessionWrapper.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
                return portals;
            }

            NodeIterator result = queryManager.createQuery("select * from [" + PortalConstants.JNT_PORTAL_USER
                    + "] as p where isdescendantnode(p, ['" + sessionWrapper.getUser().getLocalPath() + "/portals/" + siteKey + "'])", Query.JCR_SQL2).execute().getNodes();

            while (result.hasNext()) {
                JCRNodeWrapper userPortal = (JCRNodeWrapper) result.next();
                userPortalModelIds.add(userPortal.getProperty(PortalConstants.J_MODEL).getString());
                portals.add(userPortal);
            }

            result = queryManager.createQuery("select * from [" + PortalConstants.JNT_PORTAL_MODEL
                    + "] as p where isdescendantnode(p, ['/sites/" + siteKey + "'])", Query.JCR_SQL2).execute().getNodes();

            while (result.hasNext()) {
                JCRNodeWrapper modelNode = (JCRNodeWrapper) result.next();
                if (!userPortalModelIds.contains(modelNode.getIdentifier()) && modelNode.hasProperty(PortalConstants.J_ENABLED) && modelNode.getProperty(PortalConstants.J_ENABLED).getBoolean()) {
                    portals.add(modelNode);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return portals;
    }

    public void fixPortalSiteInContext(RenderContext renderContext, JCRNodeWrapper portalNode) throws RepositoryException {
        if (!portalIsModel(portalNode)) {
            JCRSiteNode portalSite = JahiaSitesService.getInstance().getSiteByKey(portalNode.getProperty(PortalConstants.J_SITEKEY).getString(), portalNode.getSession());
            renderContext.setSite(portalSite);
            renderContext.setSiteInfo(new SiteInfo(portalSite));
        }
    }

    public Collection<JCRNodeWrapper> getUserPortalsByModel(JCRNodeWrapper portalModelNode) {
        Set<JCRNodeWrapper> portals = new HashSet<JCRNodeWrapper>();

        try {
            if (portalIsModel(portalModelNode)) {
                JCRSessionWrapper sessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession("live");
                QueryManager queryManager = sessionWrapper.getWorkspace().getQueryManager();
                if (queryManager == null) {
                    logger.error("Unable to obtain QueryManager instance");
                    return portals;
                }

                NodeIterator result = queryManager.createQuery(("select * from [" + PortalConstants.JNT_PORTAL_USER +
                        "] as p where p.[" + PortalConstants.J_MODEL + "] = '") + portalModelNode.getIdentifier() +
                        "'", Query.JCR_SQL2).execute().getNodes();

                while (result.hasNext()) {
                    portals.add((JCRNodeWrapper) result.next());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return portals;
    }

    public JCRNodeWrapper initUserPortalFromModel(JCRNodeWrapper modelNode, JCRSessionWrapper sessionWrapper) {
        try {
            // test if the model is customizable
            if (modelNode.hasProperty(PortalConstants.J_ALLOW_CUSTOMIZATION) &&
                    !modelNode.getProperty(PortalConstants.J_ALLOW_CUSTOMIZATION).getBoolean()) {
                return null;
            }

            // Create/get portal folders
            JCRNodeWrapper portalFolderRoot = getPortalFolder(sessionWrapper.getNode(sessionWrapper.getUser().getLocalPath()), "portals", true);
            JCRNodeWrapper sitePortalFolder = getPortalFolder(portalFolderRoot, modelNode.getResolveSite().getSiteKey(), true);

            // Create portal
            JCRNodeWrapper portal = sitePortalFolder.addNode(modelNode.getName(), PortalConstants.JNT_PORTAL_USER);
            portal.setProperty(PortalConstants.J_TEMPLATE_ROOT_PATH, modelNode.getPropertyAsString(PortalConstants.J_TEMPLATE_ROOT_PATH));
            portal.setProperty(PortalConstants.J_ALLOWED_WIDGET_TYPES, modelNode.getProperty(PortalConstants.J_ALLOWED_WIDGET_TYPES).getValues());
            portal.setProperty(PortalConstants.JCR_TITLE, modelNode.getDisplayableName());
            portal.setProperty(PortalConstants.J_FULL_TEMPLATE, modelNode.getPropertyAsString(PortalConstants.J_FULL_TEMPLATE));
            portal.setProperty(PortalConstants.J_MODEL, modelNode);
            portal.setProperty(PortalConstants.J_SITEKEY, modelNode.getResolveSite().getSiteKey());
            portal.grantRoles("u:guest", Collections.singleton("reader"));

            //copy tabs
            List<JCRNodeWrapper> tabNodes = getPortalTabs(modelNode, sessionWrapper);
            for (JCRNodeWrapper tabNode : tabNodes) {
                tabNode.copy(portal.getPath());
            }

            // copy/ref behavior for widgets
            for (JCRNodeWrapper portalTab : JCRContentUtils.getChildrenOfType(portal, PortalConstants.JNT_PORTAL_TAB)) {
                portalTab.denyRoles("g:users", Collections.singleton("reader"));
                portalTab.denyRoles("u:guest", Collections.singleton("reader"));
                portalTab.grantRoles("u:"+sessionWrapper.getUser().getUsername(), Collections.singleton("reader"));
                portalTab.grantRoles("u:"+sessionWrapper.getUser().getUsername(), Collections.singleton("owner"));

                for (JCRNodeWrapper portalColumn : JCRContentUtils.getChildrenOfType(portalTab, PortalConstants.JNT_PORTAL_COLUMN)) {
                    for (JCRNodeWrapper widget : JCRContentUtils.getChildrenOfType(portalColumn, PortalConstants.JMIX_PORTAL_WIDGET_MODEL)) {
                        if (widget.hasProperty(PortalConstants.J_BEHAVIOR) && widget.getProperty(PortalConstants.J_BEHAVIOR).getString().equals(PortalConstants.J_BEHAVIOR_REF)) {
                            JCRNodeWrapper widgetRef = portalColumn.addNode(widget.getName() + "_ref", PortalConstants.JNT_PORTAL_WIDGET_REFERENCE);
                            widgetRef.setProperty("j:node", sessionWrapper.getNode(modelNode.getPath() + "/" + portalTab.getName() + "/" + portalColumn.getName() + "/" + widget.getName()));
                            contentManager.moveOnTopOf(widgetRef.getPath(), widget.getPath(), sessionWrapper);
                            widget.remove();
                        }
                    }
                }
            }
            sessionWrapper.save();

            return portal;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public PortalContext buildPortalContext(RenderContext renderContext, JCRNodeWrapper portalTabNode, JCRSessionWrapper sessionWrapper, final boolean updateLastPortalUsed) throws RepositoryException {
        final String currentPath = portalTabNode.getPath();
        final boolean isEditable = portalTabNode.hasPermission("jcr:write_live");
        final Locale mainResourceLocale = renderContext.getMainResourceLocale();
        final String siteKey = renderContext.getSite().getSiteKey();

        PortalContext portalContext = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), sessionWrapper.getWorkspace().getName(), sessionWrapper.getLocale(), new JCRCallback<PortalContext>() {
            @Override
            public PortalContext doInJCR(JCRSessionWrapper session) throws RepositoryException {
                JCRNodeWrapper portalTabNode = session.getNode(currentPath);
                JCRNodeWrapper portalNode = JCRContentUtils.getParentOfType(portalTabNode, PortalConstants.JMIX_PORTAL);

                PortalContext portalContext = new PortalContext();
                boolean isModel = portalNode.isNodeType(PortalConstants.JNT_PORTAL_MODEL);
                portalContext.setEditable(isEditable);
                portalContext.setPath(portalNode.getPath());
                portalContext.setIdentifier(portalNode.getIdentifier());
                portalContext.setTabPath(portalTabNode.getPath());
                portalContext.setTabIdentifier(portalTabNode.getIdentifier());
                portalContext.setFullTemplate(portalNode.getProperty(PortalConstants.J_FULL_TEMPLATE).getString());
                portalContext.setLock(portalNode.hasProperty(PortalConstants.J_LOCKED) && portalNode.getProperty(PortalConstants.J_LOCKED).getBoolean());
                portalContext.setModel(isModel);
                portalContext.setCustomizable(isModel && portalNode.hasProperty(PortalConstants.J_ALLOW_CUSTOMIZATION) && portalNode.getProperty(PortalConstants.J_ALLOW_CUSTOMIZATION).getBoolean());
                JCRSiteNode site = JahiaSitesService.getInstance().getSiteByKey(siteKey, session);

                JCRNodeWrapper modelNode = null;
                boolean modelDeleted = false;
                if (isModel) {
                    portalContext.setEnabled(portalNode.hasProperty(PortalConstants.J_ENABLED) && portalNode.getProperty(PortalConstants.J_ENABLED).getBoolean());
                } else {
                    try {
                        modelNode = (JCRNodeWrapper) portalNode.getProperty(PortalConstants.J_MODEL).getNode();
                    } catch (Exception e) {
                        // model deleted
                    }
                    if (modelNode != null) {
                        portalContext.setModelPath(modelNode.getPath());
                        portalContext.setModelIdentifier(modelNode.getIdentifier());
                        portalContext.setEnabled(modelNode.hasProperty(PortalConstants.J_ENABLED) && modelNode.getProperty(PortalConstants.J_ENABLED).getBoolean());
                    } else {
                        portalContext.setEnabled(false);
                        modelDeleted = true;
                    }
                }

                portalContext.setSiteKey(site.getSiteKey());
                portalContext.setPortalTabTemplates(new ArrayList<PortalKeyNameObject>());
                portalContext.setPortalTabSkins(new ArrayList<PortalKeyNameObject>());
                portalContext.setPortalWidgetTypes(new TreeSet<PortalWidgetType>(WIDGET_TYPES_COMPARATOR));
                if (isEditable) {

                    // Templates for portal tabs
                    List<JCRNodeWrapper> templates = getPortalTabTemplates(portalNode.getProperty(PortalConstants.J_TEMPLATE_ROOT_PATH).getString(), session);
                    for (JCRNodeWrapper template : templates) {
                        PortalKeyNameObject portalTabTemplate = new PortalKeyNameObject();
                        portalTabTemplate.setName(template.getDisplayableName());
                        portalTabTemplate.setKey(template.getName());
                        portalContext.getPortalTabTemplates().add(portalTabTemplate);
                    }

                    // Widget skins
                    SortedSet<View> widgetSkins = getViewSet(PortalConstants.JMIX_PORTAL_WIDGET, site);
                    for (View widgetView : widgetSkins) {
                        if (widgetView.getKey().startsWith("box")) {
                            PortalKeyNameObject portalTabSkin = new PortalKeyNameObject();
                            try {
                                portalTabSkin.setName(Messages.get(widgetView.getModule(), widgetView.getKey(), mainResourceLocale));
                            } catch (MissingResourceException e) {
                                // no resourceBundle for skin
                                portalTabSkin.setName(widgetView.getKey());
                            }

                            portalTabSkin.setKey(widgetView.getKey());
                            portalContext.getPortalTabSkins().add(portalTabSkin);
                        }
                    }

                    // Widget types
                    Collection<ExtendedNodeType> widgetTypes = getPortalWidgetNodeTypes(portalNode);
                    Collection<ExtendedNodeType> modelWidgetTypes = !isModel && !modelDeleted ? getPortalWidgetNodeTypes(modelNode) : null;

                    for (ExtendedNodeType widgetType : widgetTypes) {
                        if (!isModel && !modelDeleted) {
                            if (modelWidgetTypes.contains(widgetType)) {
                                PortalWidgetType portalWidgetType = buildPortalWidgetType(widgetType, site, mainResourceLocale, false);
                                portalContext.getPortalWidgetTypes().add(portalWidgetType);
                                modelWidgetTypes.remove(widgetType);
                            }
                        } else {
                            PortalWidgetType portalWidgetType = buildPortalWidgetType(widgetType, site, mainResourceLocale, false);
                            portalContext.getPortalWidgetTypes().add(portalWidgetType);
                        }
                    }
                    if (!isModel && !modelDeleted) {
                        for (ExtendedNodeType modelWidgetType : modelWidgetTypes) {
                            PortalWidgetType portalWidgetType = buildPortalWidgetType(modelWidgetType, site, mainResourceLocale, true);
                            portalContext.getPortalWidgetTypes().add(portalWidgetType);
                        }
                    }
                }

                if (updateLastPortalUsed) {
                    DateTime currentDateTime = new DateTime();
                    String lastViewed = portalNode.getPropertyAsString(PortalConstants.J_LASTVIEWED);

                    boolean firstView = StringUtils.isEmpty(lastViewed);
                    if (firstView || currentDateTime.getDayOfYear() != ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(lastViewed).getDayOfYear()) {
                        portalNode.setProperty(PortalConstants.J_LASTVIEWED, currentDateTime.toCalendar(mainResourceLocale));
                        portalNode.saveSession();
                    }
                }

                return portalContext;
            }
        });

        portalContext.setBaseUrl(StringUtils.isNotEmpty(renderContext.getURLGenerator().getContext())
                ? renderContext.getURLGenerator().getContext() + renderContext.getURLGenerator().getBaseLive()
                : renderContext.getURLGenerator().getBaseLive());


        return portalContext;
    }

    private PortalWidgetType buildPortalWidgetType(ExtendedNodeType widgetType, JCRSiteNode site, Locale locale, boolean isNew) {
        PortalWidgetType portalWidgetType = new PortalWidgetType();
        portalWidgetType.setName(widgetType.getName());
        portalWidgetType.setDisplayableName(getI18NodeTypeName(widgetType, locale));
        portalWidgetType.setViews(new ArrayList<PortalWidgetTypeView>());
        portalWidgetType.setNew(isNew);

        SortedSet<View> widgetViews = getViewSet(widgetType.getName(), site);
        for (View widgetView : widgetViews) {
            if (widgetView != null && widgetView.getKey().startsWith("portal.")) {
                PortalWidgetTypeView widgetViewTypeView = new PortalWidgetTypeView();
                widgetViewTypeView.setPath(widgetView.getPath());
                widgetViewTypeView.setKey(widgetView.getKey());
                portalWidgetType.getViews().add(widgetViewTypeView);
            }
        }

        return portalWidgetType;
    }

    public SortedSet<View> getViewSet(String nt, JCRSiteNode site) {
        try {
            return RenderService.getInstance().getViewsSet(NodeTypeRegistry.getInstance().getNodeType(nt), site, "html");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new TreeSet<View>();
    }

    public boolean portalIsModel(JCRNodeWrapper portalNode) throws RepositoryException {
        return portalNode.isNodeType(PortalConstants.JNT_PORTAL_MODEL);
    }

    public boolean portalHasModel(JCRNodeWrapper portalNode) throws RepositoryException {
        return portalNode.isNodeType(PortalConstants.JMIX_HAS_MODEL);
    }

    public JCRNodeWrapper getPortalModel(JCRNodeWrapper portalNode) throws RepositoryException {
        if (portalHasModel(portalNode)) {
            return (JCRNodeWrapper) portalNode.getProperty(PortalConstants.J_MODEL).getNode();
        }
        return null;
    }

    public Collection<ExtendedNodeType> getWidgetNodeTypes(JCRSiteNode site) {
        SortedSet<ExtendedNodeType> widgetTypes = new TreeSet<ExtendedNodeType>(NODE_TYPES_COMPARATOR);
        Set<String> installedModules = site.getInstalledModulesWithAllDependencies();

        NodeTypeIterator nodeTypes = NodeTypeRegistry.getInstance().getAllNodeTypes();
        while (nodeTypes.hasNext()) {
            ExtendedNodeType nodeType = (ExtendedNodeType) nodeTypes.next();
            for (ExtendedNodeType superType : nodeType.getSupertypes()) {
                if (superType.getName().equals(PortalConstants.JMIX_PORTAL_WIDGET)
                        && !nodeType.isNodeType(PortalConstants.JMIX_PORTAL_WIDGET_CORE)
                        && installedModules.contains(nodeType.getSystemId())) {
                    widgetTypes.add(nodeType);
                    break;
                }
            }
        }
        return widgetTypes;
    }

    public Collection<ExtendedNodeType> getPortalWidgetNodeTypes(JCRNodeWrapper portalNode) throws RepositoryException {
        List<ExtendedNodeType> widgetTypes = new ArrayList<ExtendedNodeType>();
        JCRValueWrapper[] values = portalNode.getProperty(PortalConstants.J_ALLOWED_WIDGET_TYPES).getValues();
        for (JCRValueWrapper value : values) {
            widgetTypes.add(NodeTypeRegistry.getInstance().getNodeType(value.getString()));
        }
        return widgetTypes;
    }

    public List<String> getRestrictedGroupNames(JCRNodeWrapper modelNode) throws RepositoryException {
        ArrayList<String> allowedGroups = new ArrayList<String>();
        if (modelNode.hasProperty(PortalConstants.J_ALLOWED_USER_GROUPS) && modelNode.getProperty(PortalConstants.J_ALLOWED_USER_GROUPS).getValues().length > 0) {
            for (JCRValueWrapper value : modelNode.getProperty(PortalConstants.J_ALLOWED_USER_GROUPS).getValues()) {
                allowedGroups.add(value.getString());
            }
        }
        return allowedGroups;
    }

    public void addRestrictedGroupsToModel(JCRNodeWrapper modelNode, List<String> groupKeys) throws RepositoryException {
        Set<String> allowedGroups = new HashSet<String>(getRestrictedGroupNames(modelNode));
        if (groupKeys != null && groupKeys.size() > 0) {
            for (String groupKey : groupKeys) {
                allowedGroups.add(groupKey);
            }
            modelNode.setProperty(PortalConstants.J_ALLOWED_USER_GROUPS, allowedGroups.toArray(new String[allowedGroups.size()]));
        }
        modelNode.getSession().save();

        if (isPortalModelEnabled(modelNode)) {
            setReadRoleForPortalModel(modelNode, true);
        }
    }

    public void removeRestrictedGroupsFromModel(JCRNodeWrapper modelNode, List<String> groupKeys) throws RepositoryException {
        List<String> allowedGroups = new ArrayList<String>(getRestrictedGroupNames(modelNode));
        if (groupKeys != null && groupKeys.size() > 0) {
            for (String groupKey : groupKeys) {
                allowedGroups.remove(groupKey);
            }
            modelNode.setProperty(PortalConstants.J_ALLOWED_USER_GROUPS, allowedGroups.toArray(new String[allowedGroups.size()]));
        }
        modelNode.getSession().save();

        if (isPortalModelEnabled(modelNode)) {
            setReadRoleForPortalModel(modelNode, true);
        }
    }

    public JCRGroupNode getGroupFromKey(String grpKey) {
        return groupManagerService.lookupGroupByPath(grpKey);
    }

    public List<JahiaGroup> getRestrictedGroups(JCRNodeWrapper portal) {
        List<JahiaGroup> groups = new ArrayList<JahiaGroup>();
        try {
            for (String groupKey : getRestrictedGroupNames(portal)) {
                try {
                    groups.add(getGroupFromKey(groupKey).getJahiaGroup());
                } catch (Exception e) {
                    logger.error("Cannot find group with key: " + groupKey, e);
                }
            }
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        return groups;
    }

    private void setReadRoleForPortalModel(JCRNodeWrapper portalModelNode, boolean enabled) {
        Set<String> roles = Collections.singleton("reader");
        try {
            portalModelNode.revokeAllRoles();
            portalModelNode.denyRoles("u:guest", roles);
            portalModelNode.denyRoles("g:users", roles);

            if (enabled) {
                List<String> restrictedGroups = getRestrictedGroupNames(portalModelNode);
                if (restrictedGroups != null && restrictedGroups.size() > 0) {
                    for (String groupKey : restrictedGroups) {
                        try {
                            JCRGroupNode group = groupManagerService.lookupGroupByPath(groupKey);
                            portalModelNode.grantRoles("g:" + group.getName(), roles);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                } else {
                    portalModelNode.grantRoles("g:users", roles);
                }
            }

            portalModelNode.getSession().save();
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /*@Override
    public void afterPropertiesSet() throws Exception {
        if (!LicenseCheckerService.Stub.isAllowed("org.jahia.portal")) {
            throw new LicenseCheckException("No license found for portal factory");
        }
    }*/
}
