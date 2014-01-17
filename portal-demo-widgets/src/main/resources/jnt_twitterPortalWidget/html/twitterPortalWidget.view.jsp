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
<template:addResources type="javascript" resources="app/twitterWidget.js" />
<template:addResources type="css" resources="twitterWidget.css" />
<%--

List of properties

- widgetId (string) indexed=no
- width (long) indexed=no
- height (long) indexed=no
- linkcolor (color) indexed=no
- theme (string, choicelist) = light indexed=no < dark,light
- noheader (boolean)  indexed=no
- nofooter (boolean)  indexed=no
- noborders (boolean)  indexed=no
- noscrollbar (boolean) indexed=no
- transparant (boolean)  indexed=no
- bordercolor (color)  indexed=no
- language (string)  indexed=no
- tweetlimit (long) indexed=no < '[0-20]*'
- related (string)  indexed=no
- ariapolite (string, choicelist) = "assertive" < "polite", "assertive"

--%>

<c:set var="properties" value="${currentNode.properties}"/>
<div id="twitter-widget-${currentNode.identifier}" ng-controller="twitter-view-ctrl" ng-init="init('twitter-widget-${currentNode.identifier}')">
    <a class="twitter-timeline"
       data-widget-id="423033586246103040"
       href="https://twitter.com/twitterapi"
                width="${properties.width.long}"
                height="600"
            <c:if test="${not empty properties.linkcolor}">
                data-link-color="${properties.linkcolor.string}"
            </c:if>
       data-chrome="<c:if test="${properties.noheader.boolean}">noheader </c:if> <c:if test="${properties.nofooter.boolean}">nofooter </c:if><c:if test="${properties.noborders.boolean}">noborders </c:if><c:if test="${properties.noscrollbar.boolean}">noscrollbar </c:if><c:if test="${properties.transparent.boolean}">transparent </c:if>"
            <c:if test="${not empty properties.bordercolor}">
                border-color="${properties.bordercolor.string}"
            </c:if>
            <c:if test="${not empty properties.language}">
                lang="${properties.language.string}"
            </c:if>
            <c:if test="${not empty properties.tweetlimit}">
                data-tweet-limit="${properties.tweetlimit.long}"
            </c:if>
            <c:if test="${not empty properties.theme}">
                data-theme="${properties.theme.string}"
            </c:if>
            <c:if test="${not empty properties.related}">
                data-related="${properties.related.string}"
            </c:if>
            <c:if test="${not empty properties.ariapolite}">
                data-aria-polite="${properties.ariapolite.string}"
            </c:if>

            >test</a>
</div>

<script type="text/javascript">
    // Boostrap app
    angular.bootstrap(document.getElementById("twitter-widget-${currentNode.identifier}"),['widgetApp']);
</script>

