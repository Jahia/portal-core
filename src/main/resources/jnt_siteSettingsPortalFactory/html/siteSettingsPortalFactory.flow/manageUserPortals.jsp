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
<%--@elvariable id="userPortal" type="org.jahia.services.content.JCRNodeWrapper"--%>

<h2>manage user portals</h2>

<form action="${flowExecutionUrl}" method="post" style="display: inline;" id="portalForm">
    <input type="hidden" name="selectedPortal" id="portalFormSelected"/>
    <input type="hidden" name="_eventId" id="portalFormAction"/>
</form>

<div>
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
        <c:set var="portalsFound" value="${userPortals.size > 0}"/>

        <table class="table table-bordered table-striped table-hover">
            <thead>
            <tr>
                <th width="3%">#</th>
                <th>model</th>
                <th>user</th>
                <th>last used</th>
                <th width="15%"><fmt:message key="label.actions"/></th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${!portalsFound}">
                    <tr>
                        <td colspan="3"><fmt:message key="label.noItemFound"/></td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${userPortals}" var="userPortal" varStatus="loopStatus">
                        <tr>
                            <td align="center" class="center">${loopStatus.count}</td>
                            <td>
                                ${userPortal.properties['j:model'].node.displayableName}
                            </td>
                            <td>
                                <c:set var="user" value="${jcr:getParentOfType(userPortal, 'jnt:user')}"/>
                                <jcr:nodeProperty node="${user}" name="j:firstName" var="firstname"/>
                                <jcr:nodeProperty node="${user}" name="j:lastName" var="lastname"/>

                                <c:choose>
                                    <c:when test="${empty firstname.string && empty lastname.string}">
                                        ${fn:escapeXml(currentNode.name)}
                                    </c:when>
                                    <c:otherwise>
                                        ${fn:escapeXml(firstname.string)}&nbsp;${fn:escapeXml(lastname.string)}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                ${portal:getDaysSinceDate(userPortal.properties['j:lastViewed'].string)}
                            </td>
                            <td>
                                <a style="margin-bottom:0;" class="btn btn-danger btn-small" title="${i18nRemove}" href="#delete">
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