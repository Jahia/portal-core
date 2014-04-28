<%@ page import="org.jahia.modules.portal.PortalConstants" %>
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
<%@ taglib prefix="portal" uri="http://www.jahia.org/tags/portalLib" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="mailSettings" type="org.jahia.services.mail.MailSettings"--%>
<%--@elvariable id="flowRequestContext" type="org.springframework.webflow.execution.RequestContext"--%>
<%--@elvariable id="flowExecutionUrl" type="java.lang.String"--%>
<%--@elvariable id="newsletter" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="newslettersRootNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="portalForm" type="org.jahia.modules.portal.sitesettings.form.PortalForm"--%>
<%--@elvariable id="skin" type="org.jahia.services.render.View"--%>
<%--@elvariable id="portalNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="widgetNodeType" type="org.jahia.modules.portal.service.bean.PortalKeyNameObject"--%>
<c:set var="portalWidgetMixin" value="<%= PortalConstants.JMIX_PORTAL_WIDGET %>"/>

<template:addResources type="javascript"
                       resources="jquery.min.js,jquery.form.js,jquery-ui.min.js,jquery.blockUI.js,workInProgress.js,admin-bootstrap.js"/>
<template:addResources type="css" resources="admin-bootstrap.css"/>
<template:addResources type="css" resources="jquery-ui.smoothness.css,jquery-ui.smoothness-jahia.css"/>

<h2><fmt:message key="newPortalModelForm.title"/></h2>

<c:forEach var="msg" items="${flowRequestContext.messageContext.allMessages}">
    <div class="${msg.severity == 'ERROR' ? 'validationError' : ''} alert ${msg.severity == 'ERROR' ? 'alert-error' : 'alert-success'}">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
            ${fn:escapeXml(msg.text)}</div>
</c:forEach>

<div class="box-1">
    <form:form action="${flowExecutionUrl}" modelAttribute="portalForm">

        <div class="row-fluid">
            <div class="span12">
                <h3><fmt:message key="newPortalModelForm.portal.info"/></h3>
            </div>
        </div>
        <div class="row-fluid">
            <div class="span12">
                <form:label path="name"><fmt:message key="newPortalModelForm.portal.form.name"/> <span class="text-error"><strong>*</strong></span></form:label>
                <form:input class="span4" path="name"/>
            </div>
        </div>
        <%-- div class="row-fluid">
            <div class="span12">
                <form:label path="templateFull"><fmt:message key="newPortalModelForm.portal.form.fullTemplate"/></form:label>
                <form:hidden path="templateFull"/>
                <input type="text" id="templateFullDecoy" class="span4" readonly="readonly" value="${portalForm.templateFull}"/>
                <ui:treeItemSelector fieldId="templateFull"
                                     displayFieldId="templateFullDecoy"
                                     root="${templatesPath}"
                                     nodeTypes="jnt:template"
                                     displayIncludeChildren="false"
                                     selectableNodeTypes="jnt:template"
                                     valueType="path"/>
            </div>
        </div --%>
        <div class="row-fluid">
            <div class="span12">
                <form:label path="allowCustomization"><fmt:message key="newPortalModelForm.portal.form.allowCustomization"/></form:label>
                <form:checkbox path="allowCustomization"/>
            </div>
        </div>
        <div class="row-fluid">
            <div class="span12">
                <p><fmt:message key="newPortalModelForm.portal.form.allowedWidgetTypes"/> <span class="text-error"><strong>*</strong></span></p>

                <c:forEach items="${widgetTypes}" var="widgetNodeType">
                    <label for="widgetType_${widgetNodeType.key}">
                        <form:checkbox path="allowedWidgetTypes" value="${widgetNodeType.key}" id="widgetType_${widgetNodeType.key}"/>
                            &nbsp;${widgetNodeType.name}
                    </label>
                </c:forEach>
            </div>
        </div>

        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span12" style="margin-top:15px;">
                    <button class="btn btn-primary" id="submit" type="submit" name="_eventId_submit"><i
                            class="icon-ok icon-white"></i>&nbsp;<fmt:message key="label.submit"/></button>
                    <button class="btn" id="cancel" type="submit" name="_eventId_cancel">
                        <i class="icon-ban-circle"></i>
                        &nbsp;<fmt:message key="label.cancel"/>
                    </button>
                </div>
            </div>
        </div>
    </form:form>
</div>
