<%@ taglib uri="http://www.jahia.org/tags/jcr" prefix="jcr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="nodetype" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>

<template:addResources type="css" resources="box.advanced.red.css"/>

<div class="widget" ng-controller="widgetCtrl" ng-app="widgetWrapper" id="w${currentNode.identifier}">
    <div class="widget-header">
        <h4 class="panel-title">${currentNode.properties["jcr:title"].string}</h4>
        <div class="widget-tools">
            <i class="icon-remove" ng-click="delete()"></i>
        </div>
    </div>
    <div class="widget-content">
        ${wrappedContent}
    </div>
</div>

<script type="text/javascript">
    angular.module("widgetWrapper").factory('widget', function($window) {
        // This is a factory function, and is responsible for
        // creating the 'greet' service.
        return portal.getCurrentWidget("w${currentNode.identifier}");
    });
    angular.bootstrap(document.getElementById("w${currentNode.identifier}"),['widgetWrapper']);
</script>
