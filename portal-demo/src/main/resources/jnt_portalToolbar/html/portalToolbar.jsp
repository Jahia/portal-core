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
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="nodetype" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>

<div id="portal_toolbar" ng-app="portalToolbar">
    <div ng-controller="navCtrl">
        <ul class="nav nav-tabs" ng-init="loadTabs()">
            <li ng-class="isCurrentTab(tab.path) ? 'active' : ''" ng-repeat="tab in tabs"><a href="#">{{tab.name}}</a></li>
            <li><a href="#"><i class="icon-folder-open"></i></a></li>
            <li><a href="#"><i class="icon-wrench"></i></a></li>
            <li><a href="#"><i class="icon-plus"></i></a></li>
        </ul>
    </div>

    <div ng-controller="widgetsCtrl" >
        <input type="button" value="add widget" ng-click="toggleWidgetsMenu()"/>

        <div ng-show="showWidgetsMenu">
            <label for="widget_desiredName">Name:</label>
            <input id="widget_desiredName" ng-model="desiredName" type="text">
            <div ng-repeat="widget in widgets">
                <span>{{widget.displayableName}}</span>

                <input type="button" ng-click="addWidget(widget.name)" value="Add">
            </div>
        </div>
    </div>

    <div id="admin1" ng-controller="tabCtrl">
        <input type="button" value="tab admin" ng-click="toggle()"/>

        <div ng-show="showForm">
            <form>
                <div class="row-fluid">
                    name: <input type="text" ng-model="form.name">
                </div>
                <div class="row-fluid">
                    template:
                    <select ng-model="form.template.key">
                        <option ng-repeat="template in form.allowedTemplates" ng-value="template.key">{{template.name}}</option>
                    </select>
                </div>
                <div class="row-fluid">
                    widgets skin:
                    <select ng-model="form.widgetsSkin.key">
                        <option ng-repeat="skin in form.allowedWidgetsSkins" ng-value="skin.key">{{skin.name}}</option>
                    </select>
                </div>

                <input type="button" value="cancel" ng-click="cancel()">
                <input type="button" value="save" ng-click="save()">
            </form>
        </div>
    </div>
</div>