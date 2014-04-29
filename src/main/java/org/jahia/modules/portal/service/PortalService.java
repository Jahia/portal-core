package org.jahia.modules.portal.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.jahia.ajax.gwt.helper.ContentManagerHelper;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.bean.*;
import org.jahia.modules.portal.sitesettings.form.PortalForm;
import org.jahia.modules.portal.sitesettings.form.PortalModelForm;
import org.jahia.modules.portal.sitesettings.form.PortalModelGroups;
import org.jahia.services.content.*;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.RenderService;
import org.jahia.services.render.View;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.JahiaGroupManagerService;
import org.jahia.utils.i18n.Messages;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
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
		if(StringUtils.isNotEmpty(form.getPortal().getTemplateFull())){
			portalNode.setProperty(PortalConstants.J_FULL_TEMPLATE, getPortalTabTemplate(form.getPortal().getTemplateFull(),
                    JCRSessionFactory.getInstance().getCurrentUserSession()).getName());
		}else {
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
                if(node.isNodeType(PortalConstants.JNT_PORTAL_TAB)){
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
		try{
			JCRNodeWrapper templateNode = sessionWrapper.getNode(templatePath);
			for (JCRValueWrapper value : templateNode.getProperty(PortalConstants.J_APPLY_ON).getValues()){
				if(value.getString().equals(PortalConstants.JNT_PORTAL_TAB)){
					templatePortalNode = templateNode;
				}
			}
		}catch (RepositoryException e){
			logger.error(e.getMessage(), e);
		}

		return templatePortalNode;
	}

    public JCRNodeWrapper addWidgetToPortal(JCRNodeWrapper portalTabNode, String nodetypeName, String nodeName, String colname, String beforeNodePath, JCRSessionWrapper session) {
        JCRNodeWrapper columnNode = getColumn(portalTabNode, colname);

        try {
            if(StringUtils.isEmpty(nodeName)){
                ExtendedNodeType nodetype = NodeTypeRegistry.getInstance().getNodeType(nodetypeName);
                nodeName = getI18NodeTypeName(nodetype, session.getLocale());
            }
            JCRNodeWrapper widget = columnNode.addNode(JCRContentUtils.findAvailableNodeName(columnNode, JCRContentUtils.generateNodeName(nodeName)), nodetypeName);
            if(StringUtils.isNotEmpty(beforeNodePath)){
                contentManager.moveOnTopOf(widget.getPath(), beforeNodePath, session);
            }
            widget.setProperty(PortalConstants.JCR_TITLE, nodeName);
            if(portalIsModel(portalTabNode.getParent())){
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
        }catch (Exception e){
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
            while (nodes.hasNext()) {
                return (JCRNodeWrapper) nodes.next();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public SortedSet<JCRNodeWrapper> getUserPortalsBySite(String siteKey, Locale locale) {
        SortedSet<JCRNodeWrapper> portals = new TreeSet<JCRNodeWrapper>(PORTALS_COMPARATOR);
        try {
            JCRSessionWrapper sessionWrapper = JCRSessionFactory.getInstance().getCurrentUserSession("live", locale);
            QueryManager queryManager = sessionWrapper.getWorkspace().getQueryManager();
            if (queryManager == null) {
                logger.error("Unable to obtain QueryManager instance");
                return portals;
            }

            NodeIterator result = queryManager.createQuery("select * from [" + PortalConstants.JNT_PORTAL_MODEL
                    + "] as p where isdescendantnode(p, ['" + "/sites/" + siteKey + "'])", Query.JCR_SQL2).execute().getNodes();

            while (result.hasNext()) {
                JCRNodeWrapper modelNode = (JCRNodeWrapper) result.next();
                JCRNodeWrapper userPortalNode = getUserPortalByModel(modelNode.getIdentifier(), sessionWrapper);
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
            if(modelNode.hasProperty(PortalConstants.J_ALLOW_CUSTOMIZATION) &&
                    !modelNode.getProperty(PortalConstants.J_ALLOW_CUSTOMIZATION).getBoolean()){
                return null;
            }

            // Create/get portal folders
            JCRNodeWrapper portalFolderRoot = getPortalFolder(sessionWrapper.getNode(sessionWrapper.getUser().getLocalPath()), "portals", true);
            JCRNodeWrapper sitePortalFolder = getPortalFolder(portalFolderRoot, modelNode.getResolveSite().getSiteKey(), true);

            // Create portal
            JCRNodeWrapper portal = sitePortalFolder.addNode(modelNode.getName(), PortalConstants.JNT_PORTAL_USER);
            portal.setProperty(PortalConstants.J_TEMPLATE_ROOT_PATH, modelNode.getPropertyAsString(PortalConstants.J_TEMPLATE_ROOT_PATH));
            portal.setProperty(PortalConstants.JCR_TITLE, modelNode.getDisplayableName());
            portal.setProperty(PortalConstants.J_FULL_TEMPLATE, modelNode.getPropertyAsString(PortalConstants.J_FULL_TEMPLATE));
            portal.setProperty(PortalConstants.J_MODEL, modelNode);
            portal.setProperty(PortalConstants.J_SITEKEY, modelNode.getResolveSite().getSiteKey());

            //copy tabs
            List<JCRNodeWrapper> tabNodes = getPortalTabs(modelNode, sessionWrapper);
            for (JCRNodeWrapper tabNode : tabNodes) {
                tabNode.copy(portal.getPath());
            }

            // copy/ref behavior for widgets
            for (JCRNodeWrapper portalTab : JCRContentUtils.getChildrenOfType(portal, PortalConstants.JNT_PORTAL_TAB)){
                for (JCRNodeWrapper portalColumn : JCRContentUtils.getChildrenOfType(portalTab, PortalConstants.JNT_PORTAL_COLUMN)){
                    for (JCRNodeWrapper widget : JCRContentUtils.getChildrenOfType(portalColumn, PortalConstants.JMIX_PORTAL_WIDGET_MODEL)){
                        if(widget.hasProperty(PortalConstants.J_BEHAVIOR) && widget.getProperty(PortalConstants.J_BEHAVIOR).getString().equals(PortalConstants.J_BEHAVIOR_REF)){
                            JCRNodeWrapper widgetRef = portalColumn.addNode(widget.getName() + "_ref", PortalConstants.JNT_PORTAL_WIDGET_REFERENCE);
                            widgetRef.setProperty("j:node", sessionWrapper.getNode(modelNode.getPath() + "/" + portalTab.getName() + "/" + portalColumn.getName() + "/" + widget.getName()));
                            contentManager.moveOnTopOf(widgetRef.getPath(), widget.getPath(), sessionWrapper);
                            widget.remove();
                        }
                    }
                }
            }

            //set roles
            portal.denyRoles("g:users", Collections.singleton("reader"));
            portal.denyRoles("u:guest", Collections.singleton("reader"));
            portal.grantRoles(sessionWrapper.getUser().getUserKey(), Collections.singleton("reader"));
            portal.grantRoles(sessionWrapper.getUser().getUserKey(), Collections.singleton("owner"));

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

        PortalContext portalContext = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), sessionWrapper.getWorkspace().getName(), sessionWrapper.getLocale(), new JCRCallback<PortalContext>() {
            @Override
            public PortalContext doInJCR(JCRSessionWrapper session) throws RepositoryException {
                JCRNodeWrapper currentNode = session.getNode(currentPath);
                JCRNodeWrapper portalTabNode = currentNode.isNodeType(PortalConstants.JNT_PORTAL_TAB) ? currentNode : JCRContentUtils.getParentOfType(currentNode, PortalConstants.JNT_PORTAL_TAB);
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
                portalContext.setEnabled(isModel && portalNode.hasProperty(PortalConstants.J_ENABLED) && portalNode.getProperty(PortalConstants.J_ENABLED).getBoolean());
                JCRSiteNode site;
                if(!isModel){
                    JCRNodeWrapper modelNode = portalNode.getSession().getNodeByIdentifier(portalNode.getProperty(PortalConstants.J_MODEL).getString());
                    portalContext.setModelPath(modelNode.getPath());
                    portalContext.setModelIdentifier(modelNode.getIdentifier());
                    site = modelNode.getResolveSite();
                } else {
                    site = portalNode.getResolveSite();
                }
                portalContext.setSiteId(site.getID());
                portalContext.setPortalTabTemplates(new ArrayList<PortalKeyNameObject>());
                portalContext.setPortalTabSkins(new ArrayList<PortalKeyNameObject>());
                portalContext.setPortalWidgetTypes(new ArrayList<PortalWidgetType>());
                if(isEditable){

                    // Templates for portal tabs
                    List<JCRNodeWrapper> templates = getPortalTabTemplates(portalNode.getProperty(PortalConstants.J_TEMPLATE_ROOT_PATH).getString(), session);
                    for (JCRNodeWrapper template : templates){
                        PortalKeyNameObject portalTabTemplate = new PortalKeyNameObject();
                        portalTabTemplate.setName(template.getDisplayableName());
                        portalTabTemplate.setKey(template.getName());
                        portalContext.getPortalTabTemplates().add(portalTabTemplate);
                    }

                    // Widget skins
                    SortedSet<View> widgetSkins = getViewSet(PortalConstants.JMIX_PORTAL_WIDGET, site);
                    for(View widgetView : widgetSkins){
                        if(widgetView.getKey().startsWith("box")){
                            PortalKeyNameObject portalTabSkin = new PortalKeyNameObject();
                            try{
                                portalTabSkin.setName(Messages.get(widgetView.getModule(), widgetView.getKey(), mainResourceLocale));
                            }catch (MissingResourceException e){
                                // no resourceBundle for skin
                                portalTabSkin.setName(widgetView.getKey());
                            }

                            portalTabSkin.setKey(widgetView.getKey());
                            portalContext.getPortalTabSkins().add(portalTabSkin);
                        }
                    }

                    // Widget types
                    Collection<ExtendedNodeType> widgetTypes = getPortalWidgetNodeTypes(site, portalNode);
                    for (ExtendedNodeType widgetType : widgetTypes){
                        PortalWidgetType portalWidgetType = new PortalWidgetType();
                        portalWidgetType.setName(widgetType.getName());
                        portalWidgetType.setDisplayableName(getI18NodeTypeName(widgetType, mainResourceLocale));
                        portalWidgetType.setViews(new ArrayList<PortalWidgetTypeView>());

                        SortedSet<View> widgetViews = getViewSet(widgetType.getName(), site);
                        for(View widgetView : widgetViews){
                            if(widgetView != null && widgetView.getKey().startsWith("portal.")){
                                PortalWidgetTypeView widgetViewTypeView = new PortalWidgetTypeView();
                                widgetViewTypeView.setPath(widgetView.getPath());
                                widgetViewTypeView.setKey(widgetView.getKey());
                                portalWidgetType.getViews().add(widgetViewTypeView);
                            }
                        }

                        portalContext.getPortalWidgetTypes().add(portalWidgetType);
                    }
                }

                if(updateLastPortalUsed){
                    DateTime currentDateTime = new DateTime();
                    String lastViewed = portalNode.getPropertyAsString(PortalConstants.J_LASTVIEWED);

                    boolean firstView = StringUtils.isEmpty(lastViewed);
                    if (firstView || currentDateTime.getDayOfYear() != ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(lastViewed).getDayOfYear()) {
                        portalNode.setProperty(PortalConstants.J_LASTVIEWED, currentDateTime.toCalendar(mainResourceLocale));
                        portalNode.saveSession();
                    }

                }

                //set tabs
                portalContext.setPortalTabs(new LinkedList<PortalTab>());
                QueryManager queryManager = session.getWorkspace().getQueryManager();
                if (queryManager != null) {
                    NodeIterator result = queryManager.createQuery("select * from [" + PortalConstants.JNT_PORTAL_TAB
                            + "] as p where isdescendantnode(p, ['" + portalContext.getPath() + "'])", Query.JCR_SQL2).execute().getNodes();

                    while (result.hasNext()) {
                        JCRNodeWrapper tabNode = (JCRNodeWrapper) result.next();
                        PortalTab portalTab = new PortalTab();
                        portalTab.setPath(tabNode.getPath());
                        portalContext.getPortalTabs().add(portalTab);
                    }
                }

                return portalContext;
            }
        });

        portalContext.setBaseUrl(StringUtils.isNotEmpty(renderContext.getURLGenerator().getContext())
                ? renderContext.getURLGenerator().getContext() + renderContext.getURLGenerator().getBaseLive()
                : renderContext.getURLGenerator().getBaseLive());

        // Filter on tabs allow to current user
        for(Iterator<PortalTab> itr = portalContext.getPortalTabs().iterator();itr.hasNext();)
        {
            PortalTab portalTab = itr.next();
            try{
                JCRNodeWrapper tabNode = sessionWrapper.getNode(portalTab.getPath());
                portalTab.setDisplayableName(tabNode.getDisplayableName());
                portalTab.setUrl(portalContext.getBaseUrl() + tabNode.getPath() + ".html");
                portalTab.setCurrent(tabNode.getIdentifier().equals(portalTabNode.getIdentifier()));
                portalTab.setTemplateKey(tabNode.getProperty(PortalConstants.J_TEMPLATE_NAME).getString());
                portalTab.setSkinKey(tabNode.getProperty(PortalConstants.J_WIDGET_SKIN).getString());
                portalTab.setAccessibility(tabNode.hasProperty(PortalConstants.J_ACCESSIBILITY) ? tabNode.getProperty(PortalConstants.J_ACCESSIBILITY).getString() : "me");
            }catch (PathNotFoundException e) {
                // path not found, the user doesn't have the permission to see the tab node, just remove it
                itr.remove();
            }
        }

        return portalContext;
    }

    public SortedSet<View> getViewSet(String nt, JCRSiteNode site){
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

    public Collection<ExtendedNodeType> getPortalWidgetNodeTypes(JCRSiteNode site, JCRNodeWrapper portalNode) {
        try {
            if (!portalIsModel(portalNode) && portalHasModel(portalNode)) {
                portalNode = getPortalModel(portalNode);
            }
            final JCRNodeWrapper portalModelNode = portalNode;
            if (portalModelNode != null && portalIsModel(portalModelNode)) {
                if (portalModelNode.hasProperty(PortalConstants.J_ALLOWED_WIDGET_TYPES)) {
                    Predicate<ExtendedNodeType> isAllowedWidgetPredicate = new Predicate<ExtendedNodeType>() {
                        @Override
                        public boolean apply(@Nullable ExtendedNodeType input) {
                            try {
                                if(input != null && !input.isNodeType(PortalConstants.JMIX_PORTAL_WIDGET_CORE)){
                                    JCRValueWrapper[] values = portalModelNode.getProperty(PortalConstants.J_ALLOWED_WIDGET_TYPES).getValues();
                                    for (JCRValueWrapper value : values) {
                                        if (input.getName().equals(value.getString())) {
                                            return true;
                                        }
                                    }
                                }
                            } catch (RepositoryException e) {
                                logger.error(e.getMessage(), e);
                            }
                            return false;
                        }
                    };

                    return Collections2.filter(getWidgetNodeTypes(site), isAllowedWidgetPredicate);
                }
            }
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }

        return Collections.emptySet();
    }

    public List<String> getRestrictedGroupNames(JCRNodeWrapper modelNode) throws RepositoryException {
        ArrayList<String> allowedGroups = new ArrayList<String>();
        if(modelNode.hasProperty(PortalConstants.J_ALLOWED_USER_GROUPS) && modelNode.getProperty(PortalConstants.J_ALLOWED_USER_GROUPS).getValues().length > 0){
            for (JCRValueWrapper value : modelNode.getProperty(PortalConstants.J_ALLOWED_USER_GROUPS).getValues()){
                allowedGroups.add(value.getString());
            }
        }
        return allowedGroups;
    }

    public void addRestrictedGroupsToModel(JCRNodeWrapper modelNode, PortalModelGroups portalModelGroups) throws RepositoryException {
        Set<String> allowedGroups = new HashSet<String>(getRestrictedGroupNames(modelNode));
        if(portalModelGroups.getGroupsKey() != null && portalModelGroups.getGroupsKey().size() > 0){
            for (String groupKey : portalModelGroups.getGroupsKey()){
                allowedGroups.add(groupKey);
            }
            modelNode.setProperty(PortalConstants.J_ALLOWED_USER_GROUPS, allowedGroups.toArray(new String[allowedGroups.size()]));
        }
        modelNode.getSession().save();

        if(isPortalModelEnabled(modelNode)){
            setReadRoleForPortalModel(modelNode, true);
        }
    }

    public void removeRestrictedGroupsFromModel(JCRNodeWrapper modelNode, PortalModelGroups portalModelGroups) throws RepositoryException {
        List<String> allowedGroups = new ArrayList<String>(getRestrictedGroupNames(modelNode));
        if(portalModelGroups.getGroupsKey() != null && portalModelGroups.getGroupsKey().size() > 0){
            for (String groupKey : portalModelGroups.getGroupsKey()){
                allowedGroups.remove(groupKey);
            }
            modelNode.setProperty(PortalConstants.J_ALLOWED_USER_GROUPS, allowedGroups.toArray(new String[allowedGroups.size()]));
        }
        modelNode.getSession().save();

        if(isPortalModelEnabled(modelNode)){
            setReadRoleForPortalModel(modelNode, true);
        }
    }

    public JahiaGroup getGroupFromKey(String grpKey){
        return groupManagerService.lookupGroup(grpKey);
    }

    public List<JahiaGroup> getRestrictedGroups(JCRNodeWrapper portal) {
        List<JahiaGroup> groups = new ArrayList<JahiaGroup>();
        try {
            for (String groupKey : getRestrictedGroupNames(portal)){
                try{
                    groups.add(getGroupFromKey(groupKey));
                }catch (Exception e){
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
                if(restrictedGroups != null && restrictedGroups.size() > 0) {
                    for(String groupKey : restrictedGroups){
                        try{
                            JahiaGroup group = groupManagerService.lookupGroup(groupKey);
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
}
