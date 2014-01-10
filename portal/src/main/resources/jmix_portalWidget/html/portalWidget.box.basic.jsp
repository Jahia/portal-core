<%@ taglib uri="http://www.jahia.org/tags/jcr" prefix="jcr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<template:addResources type="css" resources="portal.css"/>

<div class="widget">
    <div class="widget-header">
        <h3>${currentNode.properties["jcr:title"].string}</h3>
    </div>
    <div class="widget-content">
        ${wrappedContent}
    </div>
</div>