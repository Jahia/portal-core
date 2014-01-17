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
<template:addResources type="javascript" resources="angular.min.js" />
<template:addResources type="javascript" resources="app/googleMapWidget.js" />
<template:addResources type="css" resources="twitterWidget.css" />

<style>
    .google-map {
        height: ${currentNode.properties["j:height"].string}px;
        margin: 0;
        padding: 0;
    }
</style>

<div id="google-map-${currentNode.identifier}" ng-controller="google-map-view-ctrl"
     ng-init="init('google-map-${currentNode.identifier}', 'canvas-${currentNode.identifier}')">
    <div id="canvas-${currentNode.identifier}" class="google-map">
        <p>Map loading ...</p>
    </div>
</div>

<script type="text/javascript">
    // Boostrap app
    angular.bootstrap(document.getElementById("google-map-${currentNode.identifier}"),['googleMapWidgetApp']);
</script>
