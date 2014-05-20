<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="user" uri="http://www.jahia.org/tags/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="portal" uri="http://www.jahia.org/tags/portalLib" %>
<%--@elvariable id="jahiaLDAPGroup" type="jah"--%>
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
<%--@elvariable id="portalModelGroups" type="org.jahia.modules.portal.sitesettings.form.PortalModelGroups"--%>

<template:addResources type="javascript" resources="jquery.min.js,jquery-ui.min.js,jquery.blockUI.js,workInProgress.js,admin-bootstrap.js"/>
<template:addResources type="css" resources="admin-bootstrap.css"/>
<template:addResources type="css" resources="jquery-ui.smoothness.css,jquery-ui.smoothness-jahia.css"/>
<fmt:message key="label.workInProgressTitle" var="i18nWaiting"/><c:set var="i18nWaiting" value="${functions:escapeJavaScript(i18nWaiting)}"/>

<template:addResources>
<script type="text/javascript">
    function submitSelectGroupsForm(event){
        $("#selectGroupEvent").val(event);
        $("#selectGroups").submit();
    }
</script>
</template:addResources>

<c:set var="site" value="${renderContext.mainResource.node.resolveSite}"/>

<h2><fmt:message key="manageRestrictedGroups.title"/> - ${portalModelGroups.portalDisplayableName}</h2>

<c:set var="multipleProvidersAvailable" value="${fn:length(providers) > 1}"/>

<div class="box-1">
    <form:form cssClass="form-inline" action="${flowExecutionUrl}" modelAttribute="portalModelGroups" method="post" onsubmit="workInProgress('${i18nWaiting}');">
        <fieldset>
            <h2><fmt:message key="label.search"/></h2>
            <div class="input-append">
                <label style="display: none;"  for="searchString"><fmt:message key="label.search"/></label>
                <form:input path="searchCriteria.searchString" cssClass="span6"/>
                <button class="btn btn-primary" type="submit" name="_eventId_search">
                    <i class="icon-search icon-white"></i>
                    &nbsp;<fmt:message key='label.search'/>
                </button>
            </div>
            <c:if test="${multipleProvidersAvailable}">
                <br/>
                <label for="storedOn"><span class="badge badge-info"><fmt:message key="label.on"/></span></label>
                <form:radiobutton path="searchCriteria.storedOn" value="everywhere" onclick="$('.provCheck').attr('disabled',true);"/>&nbsp;
                <fmt:message key="label.everyWhere"/>

                <form:radiobutton path="searchCriteria.storedOn" value="providers" onclick="$('.provCheck').removeAttr('disabled');"/>&nbsp;
                <fmt:message key="label.providers"/>

                <c:forEach items="${providers}" var="curProvider">
                    <form:checkbox cssClass="provCheck" path="searchCriteria.providers" value="${curProvider.key}" disabled="${searchCriteria.storedOn != 'providers'}"/>
                    <fmt:message var="i18nProviderLabel" key="providers.${curProvider.key}.label"/>
                    ${fn:escapeXml(fn:contains(i18nProviderLabel, '???') ? curProvider.key : i18nProviderLabel)}
                </c:forEach>
            </c:if>
        </fieldset>
    </form:form>
</div>


<div>
    <form:form cssClass="form-inline" action="${flowExecutionUrl}" modelAttribute="portalModelGroups" method="post" id="selectGroups" onsubmit="workInProgress('${i18nWaiting}');">
        <input type="hidden" name="_eventId" id="selectGroupEvent"/>
        <div>
                <button class="btn btn-primary" type="button" onclick="submitSelectGroupsForm('saveRestrictions')">
                    &nbsp;<fmt:message key="label.save"/>
                </button>

            <button class="btn" type="button" onclick="submitSelectGroupsForm('cancel')">
                &nbsp;<fmt:message key="label.cancel"/>
            </button>
        </div>

        <p>
            <c:forEach items="${flowRequestContext.messageContext.allMessages}" var="message">
            <c:if test="${message.severity eq 'INFO'}">
        <div class="alert alert-success">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
                ${message.text}
        </div>
        </c:if>
        <c:if test="${message.severity eq 'ERROR'}">
            <div class="alert alert-error">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                    ${message.text}
            </div>
        </c:if>
        </c:forEach>
        </p>

        <div>
            <c:set var="groupCount" value="${fn:length(portalModelGroups.currentRestrictions)}"/>
            <c:set var="groupsFound" value="${groupCount > 0}"/>

            <div class="alert alert-info">
                <fmt:message key="siteSettings.groups.found">
                    <fmt:param value="${groupCount}"/>
                </fmt:message><c:if test="${portalModelGroups.displayLimited}">&nbsp;<fmt:message key="siteSettings.groups.found.limit">
                <fmt:param value="${portalModelGroups.displayLimit}"/>
            </fmt:message>
            </c:if>
            </div>

            <c:if test="${groupsFound}">
                <form action="${flowExecutionUrl}" method="post" style="display: inline;" id="groupForm">
                    <input type="hidden" name="selectedGroup" id="groupFormSelected"/>
                    <input type="hidden" id="groupFormAction" name="_eventId" value="" />
                </form>
            </c:if>
            <table class="table table-bordered table-striped table-hover">
                <thead>
                <tr>
                    <th width="4%">#</th>
                    <th><fmt:message key="label.name"/></th>
                    <c:if test="${multipleProvidersAvailable}">
                        <th width="20%"><fmt:message key="column.provider.label"/></th>
                    </c:if>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <%--@elvariable id="groups" type="java.util.List"--%>
                    <c:when test="${!groupsFound}">
                        <tr>
                            <td colspan="${multipleProvidersAvailable ? '3' : '2'}"><fmt:message key="label.noItemFound"/></td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${groups}" var="grp" varStatus="loopStatus">
                            <tr>
                                <td>
                                    <form:checkbox path="groupsKey" value="${grp.groupKey}"/>
                                </td>
                                <td>
                                   ${fn:escapeXml(user:displayName(grp))}
                                </td>
                                <c:if test="${multipleProvidersAvailable}">
                                    <fmt:message var="i18nProviderLabel" key="providers.${grp.providerName}.label"/>
                                    <td>${fn:escapeXml(fn:contains(i18nProviderLabel, '???') ? grp.providerName : i18nProviderLabel)}</td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </form:form>
</div>
