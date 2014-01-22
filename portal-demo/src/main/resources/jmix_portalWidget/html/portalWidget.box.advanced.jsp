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

<c:set var="widgetHasEditView"
       value="${portal:getSpecificView(currentNode.primaryNodeTypeName, 'edit', renderContext.site) != null}"/>
<c:set var="widgetIsEditable" value="${jcr:hasPermission(currentNode, 'jcr:write_live')}"/>

<template:addResources type="javascript" resources="app/portalWidgetWrapper.js"/>
<template:addResources type="css" resources="box.advanced.red.css"/>

<script type="text/javascript">
    // skin javascript controller
    $(document).ready(function(){
        new Jahia.Portal.AdvancedWidgetWrapper("w${currentNode.identifier}", ${widgetIsEditable});
    });
</script>

<div class="widget" id="w${currentNode.identifier}">
    <div class="widget-header">
        <h4 class="panel-title">${currentNode.properties["jcr:title"].string}</h4>

        <div class="widget-tools">
            <c:if test="${widgetHasEditView && widgetIsEditable}">
                <i class="icon-cog edit_switch"></i>
            </c:if>
            <i class="icon-minus minimize_action"></i>
            <c:if test="${widgetHasEditView && widgetIsEditable}">
                <i class="icon-remove delete_action"></i>
            </c:if>
        </div>
    </div>
    <div class="widget-content">
        ${wrappedContent}
    </div>
</div>


