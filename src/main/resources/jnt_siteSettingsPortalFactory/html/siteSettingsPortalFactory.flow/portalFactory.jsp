<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="user" uri="http://www.jahia.org/tags/user" %>
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
<%--@elvariable id="searchCriteria" type="org.jahia.services.usermanager.SearchCriteria"--%>
<%--@elvariable id="portals" type="java.util.List<org.jahia.services.content.JCRNodeWrapper>"--%>

<template:addResources type="javascript" resources="jquery.min.js,jquery-ui.min.js,jquery.blockUI.js,workInProgress.js,admin-bootstrap.js"/>
<template:addResources type="css" resources="admin-bootstrap.css"/>
<template:addResources type="css" resources="jquery-ui.smoothness.css,jquery-ui.smoothness-jahia.css,tablecloth.css"/>
<template:addResources>
    <script type="text/javascript">
        function submitPortalForm(act, portal) {
            $('#portalFormAction').val(act);
            if(portal){
                $('#portalFormSelected').val(portal);
            }
            $('#portalForm').submit();
        }
    </script>
</template:addResources>

<c:set var="site" value="${renderContext.mainResource.node.resolveSite}"/>

<h2><fmt:message key="portalFactory.title"/> - ${fn:escapeXml(site.displayableName)}</h2>

<form action="${flowExecutionUrl}" method="post" style="display: inline;" id="portalForm">
    <input type="hidden" name="selectedPortal" id="portalFormSelected"/>
    <input type="hidden" name="_eventId" id="portalFormAction"/>
</form>

<div>
    <div>
        <button class="btn" onclick="submitPortalForm('createPortal')">
            <i class="icon-plus"></i>
                <fmt:message key="newPortalModelForm.title"/>
        </button>
        <button class="btn" onclick="submitPortalForm('manageUserPortals')">
            <i class="icon-user"></i>
            <fmt:message key="portalFactory.manageUserPortals"/>
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
        <c:set var="portalsCount" value="${fn:length(portals)}"/>
        <c:set var="portalsFound" value="${portalsCount > 0}"/>

        <table class="table table-bordered table-striped table-hover">
            <thead>
            <tr>
                <th width="3%">#</th>
                <th><fmt:message key="portalFactory.name"/></th>
                <th width="11%"><fmt:message key="portalFactory.userPortalsNbr"/></th>
                <th width="11%"><fmt:message key="portalFactory.groupRestrictions"/></th>
                <th width="15%"><fmt:message key="label.actions"/></th>
                <th width="10%"><fmt:message key="portalFactory.state"/></th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${!portalsFound}">
                    <tr>
                        <td colspan="5"><fmt:message key="label.noItemFound"/></td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${portals}" var="portal" varStatus="loopStatus">
                        <fmt:message var="i18nRemove" key="label.remove"/><c:set var="i18nRemove" value="${functions:escapeJavaScript(i18nRemove)}"/>
                        <fmt:message var="i18nEdit" key="label.edit"/><c:set var="i18nEdit" value="${functions:escapeJavaScript(i18nEdit)}"/>
                        <fmt:message var="i18nManageGroups" key="portalFactory.manageGroups"/><c:set var="i18nManageGroups" value="${functions:escapeJavaScript(i18nManageGroups)}"/>
                        <fmt:message var="i18nLiveMode" key="portalFactory.goToLive"/><c:set var="i18nLiveMode" value="${functions:escapeJavaScript(i18nLiveMode)}"/>
                        <c:url var="portalURL" value="${url.baseLive}${portal.path}"/>
                        <tr>
                            <td align="center" class="center">${loopStatus.count}</td>
                            <td>
                                <a title="${i18nEdit}" href="#details" onclick="submitPortalForm('editPortal', '${portal.identifier}')">${fn:escapeXml(portal.displayableName)}</a>
                            </td>
                            <td align="center" class="center">
                                ${fn:length(portal:userPortalsByModel(portal))}
                            </td>
                            <td>
                                <%--@elvariable id="groups" type="java.util.List<org.jahia.services.usermanager.JahiaGroup>"--%>
                                <c:set var="groups" value="${portal:getRestrictedGroups(portal)}"/>
                                <c:choose>
                                    <c:when test="${not empty groups}">
                                        <ul>
                                            <c:forEach items="${groups}" var="group">
                                                <li>${group.name}</li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:message key="manageRestrictedGroups.noRestrictions"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a style="margin-bottom:0;" class="btn btn-small" title="${i18nLiveMode}" href="${portalURL}">
                                    <i class="icon-globe"></i>
                                </a>

                                <a style="margin-bottom:0;" class="btn btn-small" title="${i18nEdit}" href="#edit" onclick="submitPortalForm('editPortal', '${portal.identifier}')">
                                    <i class="icon-edit"></i>
                                </a>

                                <a style="margin-bottom:0;" class="btn btn-small" title="${i18nManageGroups}" href="#groups" onclick="submitPortalForm('manageGroups', '${portal.identifier}')">
                                    <i class="icon-user"></i>
                                </a>

                                <a style="margin-bottom:0;" class="btn btn-danger btn-small" title="${i18nRemove}" href="#delete">
                                    <i class="icon-remove icon-white"></i>
                                </a>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${portal.properties['j:enabled'].boolean}">
                                        <button class="btn btn-danger" type="button"
                                                onclick="submitPortalForm('disablePortal', '${portal.identifier}')">
                                            <i class=" icon-stop icon-white"></i><fmt:message key="portalFactory.disable"/>
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-success" type="button"
                                                onclick="submitPortalForm('enablePortal', '${portal.identifier}')">
                                            <i class=" icon-play icon-white"></i><fmt:message key="portalFactory.enable"/>
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>