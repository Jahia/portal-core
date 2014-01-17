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

<template:addResources type="javascript" resources="jquery.min.js"/>
<template:addResources type="javascript" resources="angular.min.js"/>
<template:addResources type="javascript" resources="angular-bootstrap-colorpicker.js"/>
<template:addResources type="javascript" resources="app/twitterWidget.js"/>
<template:addResources type="css" resources="colorpicker.css"/>

<c:set var="properties" value="${currentNode.properties}"/>

<div id="twitter-widget-${currentNode.identifier}" ng-controller="twitter-edit-ctrl"
     ng-init="init('twitter-widget-${currentNode.identifier}')">

    <form name="twitter_form" class="simple-form">
        <input type="hidden" name="jcrNodeType" ng-model="twitter.jcrNodeType" ng-init="twitter.jcrNodeType = '${currentNode.primaryNodeTypeName}'"/>

        <label>
            <span>widgetId: </span>
        <input type="text" required name="widgetId" ng-model="twitter.widgetId"
               ng-init="twitter.widgetId = '${properties.widgetId.string}'"/>
        </label><br/>
        <div ng-show="twitter_form.widgetId.$invalid">Invalid:
            <span ng-show="twitter_form.widgetId.$error.required">Tell us your widgetId.</span>
        </div>

        <label>
            <span>Height:</span>
            <input type="number" name="height" ng-model="twitter.height"
                   ng-init="twitter.height = ${properties.height.long}"/>
        </label><br/>
        <div ng-show="twitter_form.height.$invalid">Invalid:
            <span ng-show="twitter_form.height.$error.number">Not a number</span>
        </div>

        <label>
            <span>linkcolor:</span>
            <input colorpicker type="text" name="linkcolor" ng-model="twitter.linkcolor"
                   ng-init="twitter.linkcolor = '${properties.linkcolor.string}'"/>
        </label><br/>

        <label>
            <span>bordercolor:</span>
            <input colorpicker type="text" name="bordercolor" ng-model="twitter.bordercolor"
                   ng-init="twitter.bordercolor = '${properties.bordercolor.string}'"/>
        </label><br/>

        <button class="btn" ng-click="cancel()">cancel</button>
        <button class="btn btn-primary" ng-disabled="twitter_form.$invalid" ng-click="update(twitter)">update</button>
    </form>
</div>

<script type="text/javascript">
    // Boostrap app
    $(document).ready(function(){
        angular.bootstrap(document.getElementById("twitter-widget-${currentNode.identifier}"), ['twitterWidgetApp']);
    });
</script>
