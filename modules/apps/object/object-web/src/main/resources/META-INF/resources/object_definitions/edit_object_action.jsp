<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/init.jsp" %>

<%
ObjectDefinition objectDefinition = (ObjectDefinition)request.getAttribute(ObjectWebKeys.OBJECT_DEFINITION);

ObjectDefinitionsActionsDisplayContext objectDefinitionsActionsDisplayContext = (ObjectDefinitionsActionsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

ObjectAction objectAction = objectDefinitionsActionsDisplayContext.getObjectAction();
%>

<react:component
	module="js/components/ObjectAction/EditObjectAction"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"isApproved", objectDefinition.isApproved()
		).put(
			"objectAction", objectDefinitionsActionsDisplayContext.getObjectActionJSONObject(objectAction)
		).put(
			"objectActionCodeEditorElements", objectDefinitionsActionsDisplayContext.getObjectActionCodeEditorElements()
		).put(
			"objectActionExecutors", objectDefinitionsActionsDisplayContext.getObjectActionExecutorsJSONArray()
		).put(
			"objectActionTriggers", objectDefinitionsActionsDisplayContext.getObjectActionTriggersJSONArray()
		).put(
			"objectDefinitionExternalReferenceCode", objectDefinition.getExternalReferenceCode()
		).put(
			"objectDefinitionsRelationshipsURL", objectDefinitionsActionsDisplayContext.getObjectDefinitionsRelationshipsURL()
		).put(
			"readOnly", !objectDefinitionsActionsDisplayContext.hasUpdateObjectDefinitionPermission()
		).put(
			"systemObject", objectDefinition.isSystem()
		).put(
			"validateExpressionURL", objectDefinitionsActionsDisplayContext.getValidateExpressionURL()
		).build()
	%>'
/>