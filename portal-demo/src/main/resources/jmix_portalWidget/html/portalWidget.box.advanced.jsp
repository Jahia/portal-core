<%@ taglib uri="http://www.jahia.org/tags/jcr" prefix="jcr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="portal" uri="http://www.jahia.org/tags/portalLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="nodetype" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>

<c:set var="widgetEditable" value="${portal:getSpecificView(currentNode.primaryNodeTypeName, 'edit', renderContext.site) != null}"/>

<template:addResources type="javascript" resources="jquery.min.js" />
<template:addResources type="javascript" resources="angular.min.js" />
<template:addResources type="javascript" resources="app/portalWidgetWrapper.js" />
<template:addResources type="css" resources="box.advanced.red.css"/>


<div class="widget" ng-init="init('w${currentNode.identifier}')" ng-controller="widgetCtrl" ng-app="widgetApp" id="w${currentNode.identifier}">
    <div class="widget-header">
        <h4 class="panel-title">${currentNode.properties["jcr:title"].string}</h4>
        <div class="widget-tools">
            <c:if test="${widgetEditable}">
                <i class="icon-cog" ng-click="switchEdit()"></i>
            </c:if>
            <i ng-class="_minimize ? 'icon-minus' : 'icon-plus'" ng-click="minimize()"></i>
            <i class="icon-remove" ng-click="delete()"></i>
        </div>
    </div>
    <div class="widget-content">
        ${wrappedContent}
    </div>
</div>

<script type="text/javascript">
    // Boostrap widget app
    angular.bootstrap(document.getElementById("w${currentNode.identifier}"),['widgetApp']);
</script>
