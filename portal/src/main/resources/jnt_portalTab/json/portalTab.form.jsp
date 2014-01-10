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
<%--@elvariable id="skin" type="org.jahia.services.render.View"--%>
<c:set target="${renderContext}" property="contentType" value="application/json;charset=UTF-8"/>
<c:set var="portalMixin" value="<%= PortalConstants.JMIX_PORTAL %>"/>
<c:set var="portalNode" value="${jcr:getParentOfType(currentNode, portalMixin)}"/>

<json:object>
    <json:property name="name" value="${currentNode.properties['jcr:title'].string}"/>
    <json:object name="widgetsSkin">
        <c:set var="skinKey" value="${currentNode.properties['j:widgetsSkin'].string}"/>
        <json:property name="name"
                       value="${functions:escapeJavaScript(portal:getPortalWidgetSkin(skinKey, renderContext.site).displayName)}"/>
        <json:property name="key" value="${skinKey}"/>
    </json:object>
    <json:object name="template">
        <c:set var="templateKey" value="${currentNode.properties['j:templateName'].string}"/>
        <json:property name="name"
                       value="${functions:escapeJavaScript(portal:getPortalTabTemplate(templateKey, portalNode).displayableName)}"/>
        <json:property name="key" value="${templateKey}"/>
    </json:object>

    <json:array items="${portal:getPortalTabTemplates(portalNode)}" var="template" name="allowedTemplates">
        <json:object>
            <json:property name="name" value="${functions:escapeJavaScript(template.displayableName)}"/>
            <json:property name="key" value="${template.name}"/>
        </json:object>
    </json:array>

    <json:array items="${portal:getPortalWidgetSkins(renderContext.site)}" var="skin" name="allowedWidgetsSkins">
        <fmt:message var="i18Name" key="${skin.displayName}"/>
        <json:object>
            <json:property name="name" value="${functions:escapeJavaScript(i18Name)}"/>
            <json:property name="key" value="${skin.key}"/>
        </json:object>
    </json:array>
</json:object>