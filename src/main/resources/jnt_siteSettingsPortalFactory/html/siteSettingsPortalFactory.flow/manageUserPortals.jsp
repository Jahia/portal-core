<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="user" uri="http://www.jahia.org/tags/user" %>
<%@ taglib prefix="portal" uri="http://www.jahia.org/tags/portalLib" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
<%--@elvariable id="userPortalsTable" type="org.jahia.modules.portal.sitesettings.table.UserPortalsTable"--%>

<fmt:message key="label.workInProgressTitle" var="i18nWaiting"/><c:set var="i18nWaiting" value="${functions:escapeJavaScript(i18nWaiting)}"/>
<template:addResources type="javascript" resources="jquery.min.js,jquery-ui.min.js,jquery.blockUI.js,workInProgress.js,admin-bootstrap.js"/>
<template:addResources type="css" resources="admin-bootstrap.css"/>
<template:addResources type="css" resources="jquery-ui.smoothness.css,jquery-ui.smoothness-jahia.css,tablecloth.css"/>
<template:addResources type="css" resources="portal-factory.css"/>
<template:addResources>
    <script type="text/javascript">
        function submitPortalForm(act, portal) {
            $('#portalFormAction').val(act);
            if(portal){
                $('#portalFormSelected').val(portal);
            }
            $('#portalForm').submit();
        }

        function paginate(page, itemsPerPage, disabled) {
            var form = $("#manageUserPortalsForm");
            form.find("#event").val("paginateTable");
            if(page){
                form.find("#pager_page").val(page);
            }
            if(itemsPerPage){
                form.find("#pager_itemsPerPage").val(itemsPerPage);
            }
            if(!disabled){
                workInProgress('${i18nWaiting}');
                form.submit();
            }
        }

        function order(sortBy, asc, disabled) {
            var form = $("#manageUserPortalsForm");
            form.find("#event").val("paginateTable");
            if(sortBy){
                form.find("#pager_sortBy").val(sortBy);
            }
            form.find("#pager_sortAsc").val(asc);

            if(!disabled){
                workInProgress('${i18nWaiting}');
                form.submit();
            }
        }

        function search(searchString) {
            var form = $("#manageUserPortalsForm");
            form.find("#searchCriteria_searchString").val(searchString);
            form.find("#event").val("search");
            workInProgress('${i18nWaiting}');
            form.submit();
        }
    </script>
</template:addResources>

<h2><fmt:message key="manageUserPortals.title"/></h2>

<form action="${flowExecutionUrl}" method="post" style="display: inline;" id="portalForm">
    <input type="hidden" name="selectedPortal" id="portalFormSelected"/>
    <input type="hidden" name="_eventId" id="portalFormAction"/>
</form>

<div>
    <div>
        <button class="btn" onclick="submitPortalForm('cancel')">
            <i class="icon-arrow-left"></i>
            <fmt:message key="manageUserPortals.back"/>
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
        <c:set var="portalsCount" value="${fn:length(userPortalsTable.rows)}" />
        <c:set var="portalsFound" value="${portalsCount > 0}"/>

        <form:form commandName="userPortalsTable" action="${flowExecutionUrl}" method="post" id="manageUserPortalsForm">
            <form:hidden path="pager.itemsPerPage" id="pager_itemsPerPage"/>
            <form:hidden path="pager.page" id="pager_page"/>
            <form:hidden path="pager.sortBy" id="pager_sortBy"/>
            <form:hidden path="pager.sortAsc" id="pager_sortAsc"/>
            <form:hidden path="searchCriteria.searchString" id="searchCriteria_searchString"/>
            <input type="hidden" name="_eventId" id="event"/>
        </form:form>

        <div class="manageUserPortalsSearch">
            <div class="input-append">
                <label for="searchString"><fmt:message key="manageUserPortals.search"/></label>
                <form:input path="userPortalsTable.searchCriteria.searchString" id="searchString" class="span6"
                            onkeydown="if (event.keyCode == 13) search($('#searchString').val());"/>
                <button class="btn btn-primary" type="submit" name="_eventId_search" onclick="search($('#searchString').val())">
                    <i class="icon-search icon-white"></i>
                    &nbsp;<fmt:message key='label.search'/>
                </button>
            </div>
        </div>

        <div class="manageUserPortalsPagination">
            <div class="results span3">
                <span>
                    <fmt:message key="pagination.pageOf.withTotal">
                        <fmt:param value="${userPortalsTable.pager.page}"/>
                        <fmt:param value="${userPortalsTable.pager.pages}"/>
                        <fmt:param value="${userPortalsTable.pager.maxResults}"/>
                    </fmt:message>
                </span>
            </div>

            <div class="pagination span6">
                <c:set var="isStart" value="${userPortalsTable.pager.start}"/>
                <c:set var="isEnd" value="${userPortalsTable.pager.end}"/>
                <ul>
                    <li class="${isStart ? 'disabled' : ''}"><a href="#" onclick="paginate(1, null, ${isStart})"><fmt:message key="pagination.begin"/></a></li>
                    <li class="${isStart ? 'disabled' : ''}"><a href="#" onclick="paginate(${userPortalsTable.pager.page - 1}, null, ${isStart})"><fmt:message key="pagination.previous"/></a></li>

                    <c:forEach var="i" begin="${userPortalsTable.pager.firstEntry}" end="${userPortalsTable.pager.lastEntry}">
                        <li class="${i == userPortalsTable.pager.page ? 'active' : ''}">
                            <a href="#" onclick="paginate(${i}, null, ${i == userPortalsTable.pager.page})">${i}</a>
                        </li>
                    </c:forEach>

                    <li class="${isEnd ? 'disabled' : ''}"><a href="#" onclick="paginate(${userPortalsTable.pager.page + 1}, null, ${isEnd})"><fmt:message key="pagination.next"/></a></li>
                    <li class="${isEnd ? 'disabled' : ''}"><a href="#" onclick="paginate(${userPortalsTable.pager.pages}, null, ${isEnd})"><fmt:message key="pagination.end"/></a></li>
                </ul>
            </div>

            <div class="items span3">
                <div class="btn-group">
                    <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                        <fmt:message key="pagination.itemsPerPage"/>: ${userPortalsTable.pager.itemsPerPage}
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <c:forEach items="${userPortalsTable.pager.itemsPerPageEntries}" var="entry">
                            <li class="${entry == userPortalsTable.pager.itemsPerPage ? 'active' : ''}">
                                <a href="#" onclick="paginate(null, ${entry}, ${entry == userPortalsTable.pager.itemsPerPage})">${entry}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </div>
        <table class="table table-bordered table-striped table-hover">
            <thead>
            <tr>
                <th width="3%">#</th>
                <th class="${userPortalsTable.pager.sortBy == 'j:model' ? (userPortalsTable.pager.sortAsc ? 'headerSortUp' : 'headerSortDown') : ''}">
                    <a href="#" onclick="order('j:model', ${!userPortalsTable.pager.sortAsc})">
                        <fmt:message key="manageUserPortals.table.header.model"/>
                    </a>
                </th>
                <th>
                    <fmt:message key="manageUserPortals.table.header.user"/>
                </th>
                <th class="${userPortalsTable.pager.sortBy == 'j:lastViewed' ? (userPortalsTable.pager.sortAsc ? 'headerSortUp' : 'headerSortDown') : ''}">
                    <a href="#" onclick="order('j:lastViewed', ${!userPortalsTable.pager.sortAsc})">
                        <fmt:message key="manageUserPortals.table.header.lastUsed"/>
                    </a>
                </th>
                <th class="${userPortalsTable.pager.sortBy == 'jcr:created' ? (userPortalsTable.pager.sortAsc ? 'headerSortUp' : 'headerSortDown') : ''}">
                    <a href="#" onclick="order('jcr:created', ${!userPortalsTable.pager.sortAsc})">
                        <fmt:message key="manageUserPortals.table.header.created"/>
                    </a>
                </th>
                <th width="15%"><fmt:message key="label.actions"/></th>
            </tr>
            </thead>
            <tbody>

            <c:choose>
                <c:when test="${!portalsFound}">
                    <tr>
                        <td colspan="6"><fmt:message key="label.noItemFound"/></td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${userPortalsTable.rows}" var="userPortalRow" varStatus="loopStatus">
                        <fmt:message var="i18nRemoveConfirm" key="manageUserPortals.remove"/><c:set var="i18nRemoveConfirm" value="${functions:escapeJavaScript(i18nRemoveConfirm)}"/>
                        <jcr:node var="userNode" uuid="${userPortalRow.value.userNodeIdentifier}"/>
                        <tr>
                            <td align="center" class="center">${loopStatus.count}</td>
                            <td>
                                ${userPortalRow.value.modelName}
                            </td>
                            <td>
                                ${fn:escapeXml(userNode.displayableName)}
                            </td>
                            <td>
                                <c:set var="lastViewed" value="${userPortalRow.value.lastUsed}" />
                                <c:choose>
                                    <c:when test="${lastViewed == 0}">
                                        <fmt:message key="manageUserPortals.today" />
                                    </c:when>
                                    <c:when test="${lastViewed == 1}">
                                        <fmt:message key="manageUserPortals.lastViewed.day" />
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:message key="manageUserPortals.lastViewed.days">
                                            <fmt:param value="${lastViewed}" />
                                        </fmt:message>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <fmt:formatDate value="${userPortalRow.value.created}" dateStyle="short" type="both"/>
                            </td>
                            <td>
                                <a style="margin-bottom:0;" class="btn btn-danger btn-small" title="${i18nRemove}" href="#delete"
                                        onclick="if (confirm('${i18nRemoveConfirm}')) { submitPortalForm('removePortal', '${userPortalRow.key}');} return false; ">
                                    <i class="icon-remove icon-white"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>