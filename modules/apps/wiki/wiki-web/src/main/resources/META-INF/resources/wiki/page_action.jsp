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

<%@ include file="/wiki/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

WikiPage wikiPage = null;

if (row != null) {
	wikiPage = (WikiPage)row.getObject();
}
else {
	wikiPage = (WikiPage)request.getAttribute("page_info_panel.jsp-wikiPage");
}

WikiListPagesDisplayContext wikiListPagesDisplayContext = new WikiListPagesDisplayContext(request, (TrashHelper)request.getAttribute(TrashWebKeys.TRASH_HELPER), wikiPage.getNode());
%>

<clay:dropdown-actions
	aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
	dropdownItems="<%= wikiListPagesDisplayContext.getActionDropdownItems(wikiPage) %>"
	propsTransformer="wiki/js/WikiPageDropdownPropsTransformer"
/>