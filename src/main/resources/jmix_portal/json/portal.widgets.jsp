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
<%--@elvariable id="widgetNodeType" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>
<%--@elvariable id="view" type="org.jahia.services.render.View"--%>
<c:set target="${renderContext}" property="contentType" value="application/json;charset=UTF-8"/>

<json:array>
    <c:forEach items="${portal:getPortalWidgetNodeTypes(renderContext.site, currentNode)}" var="widgetNodeType">
        <json:object>
            <json:property name="name" value="${widgetNodeType.name}"/>
            <json:property name="displayableName"
                           value="${portal:getNodeTypeDisplayableName(widgetNodeType, renderContext.mainResourceLocale)}"/>
            <json:array name="views">
                <c:forEach items="${portal:getViewsSet(widgetNodeType.name, currentNode)}" var="view">
                    <json:object>
                        <json:property name="path" value="${view.path}"/>
                        <json:property name="key" value="${view.key}"/>
                    </json:object>
                </c:forEach>
            </json:array>
        </json:object>
    </c:forEach>
</json:array>