package org.jahia.modules.portal.sitesettings;

import org.apache.commons.lang.StringUtils;
import org.jahia.data.viewhelper.principal.PrincipalViewHelper;
import org.jahia.modules.portal.PortalConstants;
import org.jahia.modules.portal.service.PortalService;
import org.jahia.modules.portal.service.bean.PortalKeyNameObject;
import org.jahia.modules.portal.sitesettings.form.PortalForm;
import org.jahia.modules.portal.sitesettings.form.PortalModelForm;
import org.jahia.modules.portal.sitesettings.form.PortalModelGroups;
import org.jahia.modules.portal.sitesettings.table.*;
import org.jahia.services.content.*;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.nodetypes.ExtendedNodeType;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.View;
import org.jahia.services.sites.JahiaSitesService;
import org.jahia.services.usermanager.*;
import org.jahia.utils.i18n.Messages;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.execution.RequestContext;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.io.Serializable;
import java.security.Principal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kevan
 * Date: 23/12/13
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class PortalFactoryHandler implements Serializable {
    private static final long serialVersionUID = 978219001163542883L;

    private static final Logger logger = LoggerFactory.getLogger(PortalFactoryHandler.class);
    private static final String BUNDLE = "resources.portal-factory_core";

    @Autowired
    private transient PortalService portalService;

    @Autowired
    private transient JahiaGroupManagerService groupManagerService;

    public PortalModelTable initPortalModelsTable(RequestContext ctx) throws RepositoryException {
        JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        final int siteId = getRenderContext(ctx).getSite().getID();
        PortalModelTable result = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<PortalModelTable>() {
            @Override
            public PortalModelTable doInJCR(JCRSessionWrapper session) throws RepositoryException {
                List<JCRNodeWrapper> portalModelNodes = portalService.getSitePortalModels(JahiaSitesService.getInstance().getSite(siteId, session), null, false, session);

                PortalModelTable portalModelTable = new PortalModelTable();
                List<PortalModelTableRow> portalModelTableRows = new ArrayList<PortalModelTableRow>();

                for (JCRNodeWrapper portalModelNode : portalModelNodes){
                    PortalModelTableRow portalModelTableRow = new PortalModelTableRow();
                    portalModelTableRow.setName(portalModelNode.getDisplayableName());
                    portalModelTableRow.setPath(portalModelNode.getPath());
                    portalModelTableRow.setUuid(portalModelNode.getIdentifier());
                    portalModelTableRow.setEnabled(portalModelNode.hasProperty(PortalConstants.J_ENABLED) && portalModelNode.getProperty(PortalConstants.J_ENABLED).getBoolean());
                    portalModelTableRow.setRestrictedGroups(portalService.getRestrictedGroups(portalModelNode));
                    portalModelTableRow.setUserPortals(portalService.getUserPortalsByModel(portalModelNode).size());

                    portalModelTableRows.add(portalModelTableRow);
                }

                portalModelTable.setPortalModelTableRows(portalModelTableRows);
                return portalModelTable;
            }
        });

        return result;
    }

    public void grantWriteOnModel(RequestContext ctx, final String portalModelIdentifier) throws RepositoryException {
        JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        final String principalKey = "u:" + sessionWrapper.getUser().getUsername();
        JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<Object>() {
            @Override
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                JCRNodeWrapper portalNode = (JCRNodeWrapper) session.getNodeByIdentifier(portalModelIdentifier);
                portalNode.grantRoles(principalKey, Collections.singleton("owner"));
                session.save();
                return null;
            }
        });
    }

    public boolean createPortalModel(final RequestContext ctx, final PortalModelForm form){
        try {
            JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
            final int siteId = getRenderContext(ctx).getSite().getID();
            JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<Object>() {
                @Override
                public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    portalService.createPortalModel(form, JahiaSitesService.getInstance().getSite(siteId, session), session);
                    return null;
                }
            });

        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }
    
    public boolean updatePortalModel(RequestContext ctx, PortalForm form, String portalModelIdentifier){
        try {
            JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
            form.setPortalModelIdentifier(portalModelIdentifier);
            final PortalForm formToUpdate = form;
            JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<Object>() {
                @Override
                public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    portalService.updatePortalModel(formToUpdate, session);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    public void deletePortalModel(RequestContext ctx, final String portalModelIdentidier) throws RepositoryException {
        JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<Object>() {
            @Override
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                JCRNodeWrapper portalNode = session.getNodeByIdentifier(portalModelIdentidier);
                portalNode.remove();
                session.save();
                return null;
            }
        });
    }

    public void initPortalForm(RequestContext ctx){
        RenderContext renderContext = getRenderContext(ctx);
        JCRSiteNode site = renderContext.getSite();

        ctx.getRequestScope().put("templatesPath", site.getTemplatePackage().getRootFolderPath() + "/" + site.getTemplatePackage().getVersion() + "/templates");

        List<PortalKeyNameObject> widgetTypes = new ArrayList<PortalKeyNameObject>();
        for (ExtendedNodeType nodeType : portalService.getWidgetNodeTypes(site)){
            PortalKeyNameObject widgetType = new PortalKeyNameObject();
            widgetType.setName(portalService.getI18NodeTypeName(nodeType, renderContext.getMainResourceLocale()));
            widgetType.setKey(nodeType.getName());
            widgetTypes.add(widgetType);
        }
        ctx.getRequestScope().put("widgetTypes", widgetTypes);

        List<PortalKeyNameObject> skins = new ArrayList<PortalKeyNameObject>();
        for (View widgetView : portalService.getViewSet(PortalConstants.JMIX_PORTAL_WIDGET, site)) {
            if (widgetView.getKey().startsWith("box")) {
                PortalKeyNameObject portalTabSkin = new PortalKeyNameObject();
                try {
                    portalTabSkin.setName(Messages.get(widgetView.getModule(), widgetView.getKey(), renderContext.getMainResourceLocale()));
                } catch (MissingResourceException e) {
                    // no resourceBundle for skin
                    portalTabSkin.setName(widgetView.getKey());
                }

                portalTabSkin.setKey(widgetView.getKey());
                skins.add(portalTabSkin);
            }
        }
        ctx.getRequestScope().put("allowedWidgetsSkin", skins);
    }

    public PortalForm initEditPortalForm(final RequestContext ctx, final String identifier) throws RepositoryException {
        JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        RenderContext renderContext = getRenderContext(ctx);
        JCRSiteNode site = renderContext.getSite();

        List<PortalKeyNameObject> widgetTypes = new ArrayList<PortalKeyNameObject>();
        for (ExtendedNodeType nodeType : portalService.getWidgetNodeTypes(site)){
            PortalKeyNameObject widgetType = new PortalKeyNameObject();
            widgetType.setName(portalService.getI18NodeTypeName(nodeType, renderContext.getMainResourceLocale()));
            widgetType.setKey(nodeType.getName());
            widgetTypes.add(widgetType);
        }
        ctx.getRequestScope().put("widgetTypes", widgetTypes);

        PortalForm formToUpdate = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<PortalForm>() {
            @Override
            public PortalForm doInJCR(JCRSessionWrapper session) throws RepositoryException {
                if(StringUtils.isNotEmpty(identifier)){
                    try {
                        JCRNodeWrapper portalNode = session.getNodeByUUID(identifier);

                        PortalForm form = new PortalForm();
                        form.setName(portalNode.getDisplayableName());
                        form.setAllowCustomization(portalNode.getProperty(PortalConstants.J_ALLOW_CUSTOMIZATION).getBoolean());

                        List<String> allowedWidgetTypes = new ArrayList<String>();
                        JCRPropertyWrapper allowedWidgetTypesProp = portalNode.getProperty(PortalConstants.J_ALLOWED_WIDGET_TYPES);
                        for(JCRValueWrapper allowedWidgetType : allowedWidgetTypesProp.getValues()){
                            allowedWidgetTypes.add(allowedWidgetType.getString());
                        }
                        form.setAllowedWidgetTypes(allowedWidgetTypes.toArray(new String[allowedWidgetTypes.size()]));
                        form.setTemplateFull(portalNode.getPropertyAsString(PortalConstants.J_FULL_TEMPLATE));
                        return form;
                    } catch (RepositoryException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                return null;
            }
        });

        return formToUpdate;
    }

    public UserPortalsTable initUserPortalsManager(RequestContext ctx) {
        return initUserPortalsManager(ctx, null);
    }

    public UserPortalsTable initUserPortalsManager(RequestContext ctx, UserPortalsTable userPortalsTable) {
        JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        final int siteId = getRenderContext(ctx).getSite().getID();
        if(userPortalsTable == null){
            userPortalsTable = new UserPortalsTable();
        }
        UserPortalsPager pager = new UserPortalsPager();
        UserPortalsSearchCriteria searchCriteria = new UserPortalsSearchCriteria();
        userPortalsTable.setPager(pager);
        userPortalsTable.setSearchCriteria(searchCriteria);
        userPortalsTable.setRows(new LinkedHashMap<String, UserPortalsTableRow>());

        try {
            final UserPortalsTable userPortalsTableToQuery = userPortalsTable;
            long maxResults = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<Long>() {
                @Override
                public Long doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    return getUserPortalsQuery(siteId, userPortalsTableToQuery, session).execute().getNodes().getSize();
                }
            });
            pager.setMaxResults(maxResults);
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
            pager.setMaxResults(0);
        }
        return userPortalsTable;
    }

    private Query getUserPortalsQuery(int siteId, UserPortalsTable userPortalsTable, JCRSessionWrapper sessionWrapper){
        Query query = null;
        try {
            QueryManager queryManager = sessionWrapper.getWorkspace().getQueryManager();
            StringBuilder builder = new StringBuilder("select * from [" + PortalConstants.JNT_PORTAL_USER + "] as p where");
            boolean first = true;
            if(userPortalsTable.getSearchCriteria() != null && StringUtils.isNotEmpty(userPortalsTable.getSearchCriteria().getSearchString())){
                Set<Principal> searchResult = PrincipalViewHelper.getSearchResult("allProps",
                        userPortalsTable.getSearchCriteria().getSearchString(), null, "providers",
                        null);

                Iterator<Principal> principals = searchResult.iterator();
                while (principals.hasNext()){
                    if(first){
                        builder.append("(");
                        first = false;
                    }
                    JahiaUser user = (JahiaUser) principals.next();
                    builder.append(" isdescendantnode(p, ['").append(user.getLocalPath()).append("'])");
                    if(principals.hasNext()){
                        builder.append(" or");
                    }else {
                        builder.append(")");
                    }
                }
            }
            if(!first){
                builder.append(" and");
            }
            builder.append(" p.['").append(PortalConstants.J_SITEID).append("'] = '").append(siteId).append("'");
            if(userPortalsTable.getPager() != null && StringUtils.isNotEmpty(userPortalsTable.getPager().getSortBy())){
                builder.append(" order by '").append(userPortalsTable.getPager().getSortBy()).append("' ").append(userPortalsTable.getPager().isSortAsc() ? "ASC" : "DESC");
            }
            query = queryManager.createQuery(builder.toString(), Query.JCR_SQL2);
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        return query;
    }

    public void searchUserPortals(RequestContext ctx, UserPortalsTable userPortalsTable) {
        try {
            JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
            final int siteId = getRenderContext(ctx).getSite().getID();

            UserPortalsSearchCriteria searchCriteria = userPortalsTable.getSearchCriteria();
            initUserPortalsManager(ctx, userPortalsTable);
            userPortalsTable.setSearchCriteria(searchCriteria);

            final UserPortalsTable userPortalsTableToQuery = userPortalsTable;
            long maxResults = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<Long>() {
                @Override
                public Long doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    Query query = getUserPortalsQuery(siteId, userPortalsTableToQuery, session);
                    if (!query.getStatement().contains("isdescendantnode") && userPortalsTableToQuery.getSearchCriteria() != null &&
                            StringUtils.isNotEmpty(userPortalsTableToQuery.getSearchCriteria().getSearchString())) {
                        return 0l;
                    } else {
                        return query.execute().getNodes().getSize();
                    }
                }
            });
            userPortalsTable.getPager().setMaxResults(maxResults);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            doUserPortalsQuery(ctx, userPortalsTable);
        }
    }

    public void doUserPortalsQuery(RequestContext ctx, UserPortalsTable userPortalsTable){
        try {
            JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
            final int siteId = getRenderContext(ctx).getSite().getID();
            final UserPortalsTable finalTable = userPortalsTable;
            LinkedHashMap<String, UserPortalsTableRow> tableRows = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<LinkedHashMap<String, UserPortalsTableRow>>() {
                @Override
                public LinkedHashMap<String, UserPortalsTableRow> doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    Query query = getUserPortalsQuery(siteId, finalTable, session);
                    query.setLimit(finalTable.getPager().getItemsPerPage());
                    query.setOffset(finalTable.getPager().getItemsPerPage() * (finalTable.getPager().getPage() - 1));

                    LinkedHashMap<String, UserPortalsTableRow> tableRowsToReturn = new LinkedHashMap<String, UserPortalsTableRow>();
                    if (query.getStatement().contains("isdescendantnode") || finalTable.getSearchCriteria() == null ||
                            !StringUtils.isNotEmpty(finalTable.getSearchCriteria().getSearchString())) {
                                NodeIterator nodeIterator = query.execute().getNodes();
                                while (nodeIterator.hasNext()) {
                                    JCRNodeWrapper portalNode = (JCRNodeWrapper) nodeIterator.next();
                                    UserPortalsTableRow row = new UserPortalsTableRow();
                                    row.setUserNodeIdentifier(JCRContentUtils.getParentOfType(portalNode, "jnt:user").getIdentifier());
                                    try {
                                        row.setModelName(((JCRNodeWrapper) portalNode.getProperty("j:model").getNode()).getDisplayableName());
                                    } catch (Exception e) {
                                        // model deleted
                                        row.setModelName("No model found");
                                    }
                                    DateTime sinceDate = ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(portalNode.getPropertyAsString("j:lastViewed"));
                                    row.setLastUsed(Days.daysBetween(new LocalDate(sinceDate), new LocalDate()).getDays());
                                    row.setCreated(portalNode.getProperty("jcr:created").getDate().getTime());
                                    tableRowsToReturn.put(portalNode.getPath(), row);
                                }
                            }
                    return tableRowsToReturn;
                }
            });
            userPortalsTable.setRows(tableRows);
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public PortalModelGroups initManageGroups(RequestContext ctx, SearchCriteria searchCriteria, String portalModelIdentifier) throws RepositoryException {
        JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        PortalModelGroups portalModelGroups = new PortalModelGroups();
        portalModelGroups.setPortalIdentifier(portalModelIdentifier);

        if(StringUtils.isEmpty(searchCriteria.getStoredOn())){
            searchCriteria.setStoredOn("everywhere");
        }
        if(StringUtils.isEmpty(searchCriteria.getSearchIn())){
            searchCriteria.setSearchIn("allProps");
        }
        if(StringUtils.isEmpty(searchCriteria.getSearchString())){
            searchCriteria.setSearchString("");
        }
        
        portalModelGroups.setSearchCriteria(searchCriteria);

        final String portalIdentifier = portalModelGroups.getPortalIdentifier();
        PortalModelGroups systemModel = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<PortalModelGroups>() {
            @Override
            public PortalModelGroups doInJCR(JCRSessionWrapper session) throws RepositoryException {
                JCRNodeWrapper portalNode = session.getNodeByUUID(portalIdentifier);
                PortalModelGroups systemModel = new PortalModelGroups();
                systemModel.setGroupsKey(portalService.getRestrictedGroupNames(portalNode));
                systemModel.setPortalDisplayableName(portalNode.getDisplayableName());
                return systemModel;
            }
        });

        portalModelGroups.setGroupsKey(systemModel.getGroupsKey());
        portalModelGroups.setPortalDisplayableName(systemModel.getPortalDisplayableName());

        return portalModelGroups;
    }

    /**
     * Returns an empty (newly initialized) search criteria bean.
     *
     * @return an empty (newly initialized) search criteria bean
     */
    public SearchCriteria initCriteria(RequestContext ctx) {
        return new SearchCriteria(((RenderContext) ctx.getExternalContext().getRequestMap().get("renderContext")).getSite().getID());
    }

    public Set<Principal> search(RequestContext ctx, PortalModelGroups portalModelGroups) {
        int displayLimit = Integer.parseInt(((Map<String, String>) ctx.getFlowScope().get("siteSettingsProperties")).get("groupDisplayLimit"));
        SearchCriteria searchCriteria = portalModelGroups.getSearchCriteria();
        long timer = System.currentTimeMillis();
        Set<Principal> searchResult = PrincipalViewHelper.getGroupSearchResult(searchCriteria.getSearchIn(),
                searchCriteria.getSiteId(), searchCriteria.getSearchString(), searchCriteria.getProperties(),
                searchCriteria.getStoredOn(), searchCriteria.getProviders());
        logger.info("Found {} groups in {} ms", searchResult.size(), System.currentTimeMillis() - timer);
        portalModelGroups.setCurrentRestrictions(new HashMap<String, Boolean>());
        List<Principal> groups = new ArrayList<Principal>(searchResult);
        for (int i = 0; i < groups.size(); i ++){
            if(i < (displayLimit - 1)){
                String grpKey = ((JahiaGroup) groups.get(i)).getGroupKey();
                portalModelGroups.getCurrentRestrictions().put(grpKey, portalModelGroups.getGroupsKey().contains(grpKey));
            }else {
                portalModelGroups.setDisplayLimited(true);
                portalModelGroups.setDisplayLimit(displayLimit);
                break;
            }
        }
        return searchResult;
    }

    public Map<String, ? extends JahiaGroupManagerProvider> getProviders() {
        Map<String, JahiaGroupManagerProvider> providers = new LinkedHashMap<String, JahiaGroupManagerProvider>();
        for (JahiaGroupManagerProvider p : groupManagerService.getProviderList()) {
            providers.put(p.getKey(), p);
        }
        return providers;
    }

    public void saveRestrictions(RequestContext ctx, PortalModelGroups portalModelGroups) throws RepositoryException {
        final PortalModelGroups copy = portalModelGroups;
        JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        List<String> newRestrictions = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<List<String>>() {
            @Override
            public List<String> doInJCR(JCRSessionWrapper session) throws RepositoryException {
                List<String> groupKeysToAdd = new ArrayList<String>();
                List<String> groupKeysToRemove = new ArrayList<String>();

                for (String groupKey : copy.getCurrentRestrictions().keySet()){
                    if(copy.getCurrentRestrictions().get(groupKey) && (copy.getGroupsKey() == null || !copy.getGroupsKey().contains(groupKey))){
                        groupKeysToRemove.add(groupKey);
                        copy.getCurrentRestrictions().put(groupKey, false);
                    }else if(!copy.getCurrentRestrictions().get(groupKey) && (copy.getGroupsKey() != null && copy.getGroupsKey().contains(groupKey))){
                        groupKeysToAdd.add(groupKey);
                        copy.getCurrentRestrictions().put(groupKey, true);
                    }
                }

                JCRNodeWrapper portalNode = session.getNodeByUUID(copy.getPortalIdentifier());
                portalService.addRestrictedGroupsToModel(portalNode, groupKeysToAdd);
                portalService.removeRestrictedGroupsFromModel(portalNode, groupKeysToRemove);
                return portalService.getRestrictedGroupNames(portalNode);
            }
        });

        portalModelGroups.setGroupsKey(newRestrictions);
    }

    public boolean enablePortalModel(final RequestContext ctx, final String selectedPortalModelIdentifier) throws RepositoryException {
        final JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<Object>() {
            @Override
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                portalService.switchPortalModelActivation(session, selectedPortalModelIdentifier, true);
                return null;
            }
        });

        return true;
    }

    public boolean disablePortalModel(RequestContext ctx, final String selectedPortalModelIdentifier) throws RepositoryException {
        final JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), "live", sessionWrapper.getLocale(), new JCRCallback<Object>() {
            @Override
            public Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
                portalService.switchPortalModelActivation(session, selectedPortalModelIdentifier, false);
                return null;
            }
        });

        return true;
    }

    public String getTemplatesPath(RequestContext ctx){
        JCRSiteNode currentSite = getRenderContext(ctx).getSite();
        return currentSite.getTemplatePackage().getRootFolderPath() + "/" + currentSite.getTemplatePackage().getVersion() + "/templates";
    }

    public void deleteUserPortal(RequestContext ctx, MessageContext messageContext, final String selectedPortal, final UserPortalsTable userPortalsTable){
        JCRSessionWrapper sessionWrapper = getCurrentUserSession(ctx, "live");
        try {
            String portalPath = JCRTemplate.getInstance().doExecuteWithSystemSession(sessionWrapper.getUser().getUsername(), sessionWrapper.getWorkspace().getName(), sessionWrapper.getLocale(), new JCRCallback<String>() {
                @Override
                public String doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    JCRNodeWrapper portalNode = session.getNode(selectedPortal);
                    String path = portalNode.getPath();

                    //perform delete
                    String name = portalNode.getDisplayableName();
                    portalNode.remove();
                    session.save();
                    return path;
                }
            });

            //update user portals table
            userPortalsTable.getRows().remove(portalPath);
            userPortalsTable.getPager().setMaxResults(userPortalsTable.getPager().getMaxResults() - 1);

            setActionMessage(messageContext, true, "manageUserPortals", ".deleted", null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            setActionMessage(messageContext, false, "manageUserPortals", ".deleted", null);
        }
    }

    private void setActionMessage(MessageContext msgCtx, boolean success, String panel, String action, Object name){
        Locale locale = LocaleContextHolder.getLocale();

        String successFlag = success ? ".successfully" : ".failed";

        String message = Messages.get(BUNDLE, panel + successFlag + action, locale);
        if(name != null){
            message = Messages.format(message, name);
        }

        MessageBuilder messageBuilder = new MessageBuilder();
        if(success){
            messageBuilder.info();
        } else {
            messageBuilder.error();
        }
        messageBuilder.defaultText(message);
        msgCtx.addMessage(messageBuilder.build());
    }

    private JCRSessionWrapper getCurrentUserSession(RequestContext ctx) {
        try {
            RenderContext renderContext = getRenderContext(ctx);
            return JCRSessionFactory.getInstance().getCurrentUserSession(renderContext.getMainResource().getWorkspace(), renderContext.getMainResourceLocale());
        } catch (RepositoryException e) {
            logger.error("Error retrieving current user session", e);
        }
        return null;
    }

    private JCRSessionWrapper getCurrentUserSession(RequestContext ctx, String workspace) {
        try {
            return JCRSessionFactory.getInstance().getCurrentUserSession(workspace, getRenderContext(ctx).getMainResourceLocale());
        } catch (RepositoryException e) {
            logger.error("Error retrieving current user session", e);
        }
        return null;
    }

    private JCRNodeWrapper getNodeByUUID(String identifier, JCRSessionWrapper session) {
        try {
            return session.getNodeByUUID(identifier);
        } catch (RepositoryException e) {
            logger.error("Error retrieving node with UUID " + identifier, e);
        }
        return null;
    }

    private RenderContext getRenderContext(RequestContext ctx) {
        return (RenderContext) ctx.getExternalContext().getRequestMap().get("renderContext");
    }

    public PortalService getPortalService() {
        return portalService;
    }

    public void setPortalService(PortalService portalService) {
        this.portalService = portalService;
    }
}
