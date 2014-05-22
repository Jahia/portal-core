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
<%--@elvariable id="nodetype" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>

<c:choose>
    <c:when test="${!renderContext.liveMode}">
        portal Area
    </c:when>
    <c:otherwise>
        <div id="portal_area_${currentNode.identifier}" class="portal_area" data-area-name="${currentNode.name}">
            <c:set var="widgetIdentifier" value="${not empty renderContext.request.parameterMap['w'] ? renderContext.request.parameterMap['w'][0] : null}"/>
            <c:set var="widgetSolo" value="${not empty renderContext.request.parameterMap['w_solo'] ? renderContext.request.parameterMap['w_solo'][0] : null}"/>

            <c:choose>
                <c:when test="${widgetSolo != null && widgetIdentifier != null}">
                    <jcr:node var="widgetNode" uuid="${widgetIdentifier}"/>
                    <c:if test="${not empty widgetNode}">
                        <jcr:node var="portalColNode" path="${widgetNode.parent.path}"/>
                        <c:if test="${not empty portalColNode}">
                            <template:area path="${portalColNode.path}"/>
                        </c:if>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <jcr:node var="portalColNode" path="${renderContext.mainResource.node.path}/${currentNode.name}"/>
                    <c:if test="${not empty portalColNode}">
                        <template:area path="${portalColNode.path}"/>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>

        <script type="text/javascript">
            portal.registerArea("portal_area_${currentNode.identifier}");
        </script>
    </c:otherwise>
</c:choose>
