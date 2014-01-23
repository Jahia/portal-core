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
<c:set target="${renderContext}" property="contentType" value="application/json;charset=UTF-8"/>
<c:set var="portalWidgetMixin" value="<%= PortalConstants.JMIX_PORTAL_WIDGET %>"/>

<json:array>
    <c:forEach items="${portal:getNodeTypes()}" var="nodetype">
        <c:forEach items="${nodetype.supertypeSet}" var="supertype">
            <c:if test="${supertype.name eq portalWidgetMixin}">
                <c:set var="name_i18n_key" value="${fn:replace(nodetype.name, ':', '_')}"/>
                <json:object>
                    <json:property name="name" value="${nodetype.name}"/>
                    <json:property name="displayableName" value="${portal:getNodeTypeDisplayableName(nodetype.templatePackage, name_i18n_key, renderContext.mainResourceLocale)}"/>
                </json:object>
            </c:if>
        </c:forEach>
    </c:forEach>
</json:array>