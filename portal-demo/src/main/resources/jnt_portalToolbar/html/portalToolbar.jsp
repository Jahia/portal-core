<%@ page import="org.jahia.modules.portal.PortalConstants" %>
<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="user" uri="http://www.jahia.org/tags/user" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="uiComponents" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="portal" uri="http://www.jahia.org/tags/portalLib" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="nodetype" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>
<c:set var="portalMixin" value="<%= PortalConstants.JMIX_PORTAL %>"/>
<c:set var="portalModelNT" value="<%= PortalConstants.JNT_PORTAL_MODEL %>"/>
<c:set var="portalNode" value="${jcr:getParentOfType(renderContext.mainResource.node, portalMixin)}" />
<c:set var="portalIsEditable" value="${jcr:hasPermission(renderContext.mainResource.node, 'jcr:write_live')}"/>

<template:addResources type="javascript" resources="app/portalToolbar.js" />
<template:addResources type="css" resources="portal-toolbar.css"/>

<div id="portal_toolbar" class="portal_toolbar" ng-app="portalToolbar">
    <div ng-controller="navCtrl">
        <ul class="nav nav-tabs" ng-init="loadTabs()">
            <li ng-class="isCurrentTab(tab.url) ? 'active' : ''" ng-repeat="tab in tabs">
                <a href="{{tab.url}}">{{tab.name}}</a>
            </li>

            <c:if test="${jcr:isNodeType(portalNode, portalModelNT) and !portal:userPortalExist(portalNode)}">
                <li class="right">
                    <button type="button" class="customize-btn btn btn-inverse" ng-click="copyModel()">Customize</button>
                </li>
            </c:if>
            <c:if test="${portalIsEditable}">
                <li><a href="#newTabModal" data-toggle="modal"><i class="icon-folder-open"></i></a></li>
                <li class="right" ng-show="canBeDeleted"><a href="#" ng-click="deleteTab()"><i class="icon-remove"></i></a></li>
                <li class="right"><a href="#editTabModal" data-toggle="modal"><i class="icon-wrench"></i></a></li>
                <li class="right"><a href="#widgetsModal" data-toggle="modal"><i class="icon-plus"></i></a></li>
            </c:if>
        </ul>
    </div>

    <c:if test="${portalIsEditable}">
        <script type="text/ng-template" id="tabFormTemplate">
            <form>
                <div class="row-fluid">
                    name: <input type="text" ng-model="form.name" required>
                </div>
                <div class="row-fluid">
                    template:
                    <select ng-model='form.template.key' required ng-options='option.key as option.name for option in form.allowedTemplates'></select>
                </div>
                <div class="row-fluid">
                    widgets skin:
                    <select ng-model='form.widgetsSkin.key' required ng-options='option.key as option.name for option in form.allowedWidgetsSkins'></select>
                </div>
            </form>
        </script>

        <div id="widgetsModal" class="modal hide fade" tabindex="-1" role="dialog"
             aria-labelledby="widgetModalLabel" ng-controller="widgetsCtrl"
             ng-init="init('widgetsModal')">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" ng-click="cancel()">×</button>
                <h3 id="widgetModalLabel">Add new widget</h3>
            </div>
            <div class="modal-body">
                <div>
                    <label for="widget_desiredName">Name:</label>
                    <input id="widget_desiredName" ng-model="desiredName" type="text">

                    <ul class="nav nav-pills nav-stacked">
                        <li ng-repeat="widget in widgets" ng-class="widget.name == desiredWidget ? 'active' : ''">
                            <a ng-click="selectWidget(widget.name)" href="#">{{widget.displayableName}}</a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn" data-dismiss="modal" ng-click="cancel()">Close</button>
                <button class="btn btn-primary" ng-click="addWidget()">Add</button>
            </div>
        </div>

        <div id="editTabModal" class="modal hide fade" tabindex="-1" role="dialog"
             aria-labelledby="editTabModalLabel" ng-controller="tabCtrl" ng-init="init('edit', 'editTabModal')">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" ng-click="cancel()">×</button>
                <h3 id="editTabModalLabel">Edit tab {{form.name}}</h3>
            </div>
            <div class="modal-body">
                <div>
                    <div ng-include src="'tabFormTemplate'">

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn" data-dismiss="modal" ng-click="cancel()">Close</button>
                <button class="btn btn-primary" ng-click="submit(false)">Save</button>
            </div>
        </div>

        <div id="newTabModal" class="modal hide fade" tabindex="-1" role="dialog"
             aria-labelledby="newTabModalLabel" ng-controller="tabCtrl" ng-init="init('new', 'newTabModal')">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" ng-click="cancel()">×</button>
                <h3 id="newTabModalLabel">Add new tab</h3>
            </div>
            <div class="modal-body">
                <div>
                    <div ng-include src="'tabFormTemplate'">

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn" data-dismiss="modal" ng-click="cancel()">Close</button>
                <button class="btn btn-primary" ng-click="submit(true)">Add</button>
            </div>
        </div>
    </c:if>
</div>