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

<template:addResources type="javascript" resources="jquery.min.js" />
<template:addResources type="javascript" resources="angular.min.js" />
<template:addResources type="javascript" resources="app/googleFeedWidget.js" />

<div id="googleFeed-${currentNode.identifier}" ng-controller="google-feed-edit-ctrl">
    <form>
        <div class="row-fluid">
            URL: <input type="text" name="url" required>
        </div>
        <input class="btn" type="button" value="cancel">
        <input class="btn btn-primary" type="button" value="save">
    </form>
</div>

<script type="text/javascript">
    // Boostrap app
    angular.bootstrap(document.getElementById("google-feed-${currentNode.identifier}"),['widgetApp']);
</script>
