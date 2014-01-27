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
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="nodetype" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>

<c:choose>
    <c:when test="${renderContext.editModeConfigName eq 'studiomode'}">
        portal Area
    </c:when>
    <c:when test="${renderContext.liveMode}">
        <c:set var="portalMixin" value="<%= PortalConstants.JMIX_PORTAL %>"/>
        <c:set var="portalNode" value="${jcr:getParentOfType(renderContext.mainResource.node, portalMixin)}"/>
        <c:set var="widgetIdentifier" value="${not empty renderContext.request.parameterMap['w'] ? renderContext.request.parameterMap['w'][0] : null}"/>
        <c:set var="widgetState" value="${not empty renderContext.request.parameterMap['w_state'] ? renderContext.request.parameterMap['w_state'][0] : ''}"/>
        <c:set var="widgetView" value="${not empty renderContext.request.parameterMap['w_view'] ? renderContext.request.parameterMap['w_view'][0] : ''}"/>

        <div id="portal_area_${currentNode.identifier}" class="portal_area">

        </div>

        <script type="text/javascript">
            <c:choose>
                <c:when test="${widgetIdentifier != null}">
                    <c:set var="widgetNode" value="${portal:getWidget(widgetIdentifier, portalNode)}"/>
                    portal.registerArea("portal_area_${currentNode.identifier}", "${widgetNode.path}", "${widgetState}", "${widgetView}");
                </c:when>
                <c:otherwise>
                    portal.registerArea("portal_area_${currentNode.identifier}");
                </c:otherwise>
            </c:choose>
        </script>
    </c:when>
</c:choose>
