package org.jahia.modules.portal.service.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by kevan on 23/04/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortalContext implements Serializable{
    private static final long serialVersionUID = 3505746969053804448L;

    @JsonProperty("portalPath")
    private String path;
    @JsonProperty("portalIdentifier")
    private String identifier;
    @JsonProperty("portalTabPath")
    private String tabPath;
    @JsonProperty("portalTabIdentifier")
    private String tabIdentifier;
    @JsonProperty("portalModelPath")
    private String modelPath;
    @JsonProperty("portalModelIdentifier")
    private String modelIdentifier;
    @JsonProperty("fullTemplate")
    private String fullTemplate;
    @JsonProperty("baseURL")
    private String baseUrl;
    @JsonProperty("portalTabs")
    private LinkedList<PortalTab> portalTabs;
    @JsonProperty("portalTabSkins")
    private List<PortalKeyNameObject> portalTabSkins;
    @JsonProperty("portalTabTemplates")
    private List<PortalKeyNameObject> portalTabTemplates;
    @JsonProperty("portalWidgetTypes")
    private SortedSet<PortalWidgetType> portalWidgetTypes;
    @JsonProperty("isLocked")
    private boolean isLock;
    @JsonProperty("isModel")
    private boolean isModel;
    @JsonProperty("isCustomizationAllowed")
    private boolean isCustomizable;
    @JsonProperty("isEditable")
    private boolean isEditable;
    @JsonProperty("isEnabled")
    private boolean isEnabled;
    @JsonProperty("debug")
    private boolean isDebug;
    @JsonProperty("siteId")
    private int siteId;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean isLock) {
        this.isLock = isLock;
    }

    public boolean isModel() {
        return isModel;
    }

    public void setModel(boolean isModel) {
        this.isModel = isModel;
    }

    public boolean isCustomizable() {
        return isCustomizable;
    }

    public void setCustomizable(boolean isCustomizable) {
        this.isCustomizable = isCustomizable;
    }

    public String getFullTemplate() {
        return fullTemplate;
    }

    public void setFullTemplate(String fullTemplate) {
        this.fullTemplate = fullTemplate;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public String getTabPath() {
        return tabPath;
    }

    public void setTabPath(String tabPath) {
        this.tabPath = tabPath;
    }

    public String getTabIdentifier() {
        return tabIdentifier;
    }

    public void setTabIdentifier(String tabIdentifier) {
        this.tabIdentifier = tabIdentifier;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getModelIdentifier() {
        return modelIdentifier;
    }

    public void setModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }

    public LinkedList<PortalTab> getPortalTabs() {
        return portalTabs;
    }

    public void setPortalTabs(LinkedList<PortalTab> portalTabs) {
        this.portalTabs = portalTabs;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<PortalKeyNameObject> getPortalTabTemplates() {
        return portalTabTemplates;
    }

    public void setPortalTabTemplates(List<PortalKeyNameObject> portalTabTemplates) {
        this.portalTabTemplates = portalTabTemplates;
    }

    public List<PortalKeyNameObject> getPortalTabSkins() {
        return portalTabSkins;
    }

    public void setPortalTabSkins(List<PortalKeyNameObject> portalTabSkins) {
        this.portalTabSkins = portalTabSkins;
    }

    public SortedSet<PortalWidgetType> getPortalWidgetTypes() {
        return portalWidgetTypes;
    }

    public void setPortalWidgetTypes(SortedSet<PortalWidgetType> portalWidgetTypes) {
        this.portalWidgetTypes = portalWidgetTypes;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
}
