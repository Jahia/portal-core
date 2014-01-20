<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<template:addResources type="javascript" resources="jquery.min.js"/>
<template:addResources type="javascript" resources="angular.min.js"/>
<template:addResources type="javascript" resources="app/googleFeedWidget.js"/>
<template:addResources type="css" resources="commonsWidget.css"/>

<div id="google-feed-${currentNode.identifier}" ng-controller="google-feed-edit-ctrl"
     ng-init="init('google-feed-${currentNode.identifier}')" class="widget-edit">
    <h2>
        <fmt:message key="jnt_googleFeedWidget"/> {{test}}
    </h2>

    <div class="box-1">
        <form name="feed_form">
            <input type="hidden" name="jcrNodeType" ng-model="feed.jcrNodeType"
                   ng-init="feed.jcrNodeType = '${currentNode.primaryNodeTypeName}'"/>

            <div class="row-fluid">
                <div class="span12">
                    <label>
                        <span><fmt:message key="jnt_googleFeedWidget.url"/>:</span>

                        <input type="text" name="url" ng-model="feed.url"
                               ng-init="feed.url = '${currentNode.properties['url'].string}'" required/>
                    </label>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span12">
                    <label>
                        <span><fmt:message key="jnt_googleFeedWidget.nbEntries"/>:</span>

                        <input type="number" name="nbEntries" ng-model="feed.nbEntries"
                               ng-init="feed.nbEntries = ${currentNode.properties['nbEntries'].long}"/>
                    </label>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span12">
                    <button class="btn" ng-click="cancel()"><fmt:message key="cancel"/></button>
                    <button class="btn btn-primary" ng-click="update(feed)">
                        <fmt:message key="save"/>
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<script type="text/javascript">
    // Boostrap app
    angular.bootstrap(document.getElementById("google-feed-${currentNode.identifier}"), ['googleFeedWidgetApp']);
</script>
