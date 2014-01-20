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
<template:addResources type="javascript" resources="angular-bootstrap-colorpicker.js"/>
<template:addResources type="javascript" resources="app/twitterWidget.js"/>
<template:addResources type="css" resources="twitterWidget.css"/>
<template:addResources type="css" resources="commonsWidget.css"/>
<template:addResources type="css" resources="colorpicker.css"/>

<c:set var="properties" value="${currentNode.properties}"/>

<div id="twitter-widget-${currentNode.identifier}" ng-controller="twitter-edit-ctrl"
     ng-init="init('twitter-widget-${currentNode.identifier}')" class="widget-edit">

<h2>
    <fmt:message key="jnt_twitterWidget"/>
    <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
       title="<fmt:message key="jnt_twitterWidget_description"/>">
        <i class="icon-info-sign"></i>
    </a>
</h2>

<div class="box-1">
<form name="twitter_form" class="simple-form">
<input type="hidden" name="jcrNodeType" ng-model="twitter.jcrNodeType"
       ng-init="twitter.jcrNodeType = '${currentNode.primaryNodeTypeName}'"/>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.widgetId"/>:</span>

            <input type="text" required name="widgetId" ng-model="twitter.widgetId"
                   ng-init="twitter.widgetId = '${properties.widgetId.string}'"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.widgetId.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>

        <div ng-show="twitter_form.widgetId.$invalid">Invalid:
            <span ng-show="twitter_form.widgetId.$error.required">Tell us your widgetId.</span>
        </div>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.theme"/>:</span>

            <select name="theme" ng-model='twitter.theme' ng-init="twitter.theme = '${properties.theme.string}'">
                <option value="dark">dark</option>
                <option value="light">light</option>
            </select>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.theme.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.height"/>:</span>
            <input type="number" name="height" ng-model="twitter.height"
                   ng-init="twitter.height = ${properties.height.long}"/>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.linkcolor"/>:</span>
            <input colorpicker type="text" name="linkcolor" ng-model="twitter.linkcolor"
                   ng-init="twitter.linkcolor = '${properties.linkcolor.string}'"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.linkcolor.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.bordercolor"/>:</span>
            <input colorpicker type="text" name="bordercolor" ng-model="twitter.bordercolor"
                   ng-init="twitter.bordercolor = '${properties.bordercolor.string}'"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.bordercolor.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.noheader"/>:</span>
            <input type="checkbox" name="noheader" ng-model="twitter.noheader"
                   ng-init="twitter.noheader = ${properties.noheader.boolean}"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.noheader.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.nofooter"/>:</span>
            <input type="checkbox" name="nofooter" ng-model="twitter.nofooter"
                   ng-init="twitter.nofooter = ${properties.nofooter.boolean}"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.nofooter.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.noborders"/>:</span>
            <input type="checkbox" name="noborders" ng-model="twitter.noborders"
                   ng-init="twitter.noborders = ${properties.noborders.boolean}"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.noborders.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.noscrollbar"/>:</span>
            <input type="checkbox" name="noscrollbar" ng-model="twitter.noscrollbar"
                   ng-init="twitter.noscrollbar = ${properties.noscrollbar.boolean}"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.noscrollbar.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.transparant"/>:</span>
            <input type="checkbox" name="transparent" ng-model="twitter.transparent"
                   ng-init="twitter.transparent = ${properties.transparent.boolean}"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.transparant.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.language"/>:</span>
            <input type="text" name="language" ng-model="twitter.language"
                   ng-init="twitter.language = '${properties.language.string}'"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.language.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.related"/>:</span>
            <input type="text" name="related" ng-model="twitter.related"
                   ng-init="twitter.related = '${properties.related.string}'"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.related.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.tweetlimit"/>:</span>
            <input type="number" name="related" ng-model="twitter.tweetlimit"
                   ng-init="twitter.tweetlimit = ${properties.tweetlimit.long}"
                   min="1" max="20"/>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.tweetlimit.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <label>
            <span><fmt:message key="jnt_twitterWidget.ariapolite"/>:</span>
            <select name="theme" ng-model='twitter.ariapolite'
                    ng-init="twitter.ariapolite = '${properties.ariapolite.string}'">
                <option value="assertive">assertive</option>
                <option value="polite">polite</option>
            </select>

            <a href="#" class="twitter-tooltip" data-placement="right" data-toggle="tooltip"
               title="<fmt:message key="jnt_twitterWidget.ariapolite.ui.tooltip"/>">
                <i class="icon-info-sign"></i>
            </a>
        </label>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <button class="btn" ng-click="cancel()"><fmt:message key="cancel"/></button>
        <button class="btn btn-primary" ng-disabled="twitter_form.$invalid" ng-click="update(twitter)">
            <fmt:message key="save"/>
        </button>
    </div>
</div>
</form>
</div>
</div>
<script type="text/javascript">
    // Boostrap app
    $(document).ready(function () {
        angular.bootstrap(document.getElementById("twitter-widget-${currentNode.identifier}"), ['twitterWidgetApp']);
    });
</script>
