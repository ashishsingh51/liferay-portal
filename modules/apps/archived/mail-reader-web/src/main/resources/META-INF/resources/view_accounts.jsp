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
long accountId = ParamUtil.getLong(request, "accountId");

MailManager mailManager = MailManager.getInstance(request);
%>

<c:if test="<%= mailManager != null %>">
	<aui:button icon="icon-plus" onClick="Liferay.Mail.addAccount();" value="add-mail-account" />

	<%
	List<Account> mailAccounts = mailManager.getAccounts();
	%>

	<c:if test="<%= !mailAccounts.isEmpty() %>">
		<ul class="nav nav-pills">

			<%
			for (Account mailAccount : mailAccounts) {
			%>

				<li class="nav-item tab <%= (mailAccount.getAccountId() == accountId) ? "active" : "" %>">
					<aui:a cssClass="folders-link nav-link" data-accountId="<%= mailAccount.getAccountId() %>" data-inboxFolderId="<%= mailAccount.getInboxFolderId() %>" href="javascript:void(0);" label="<%= mailAccount.getAddress() %>" />
				</li>

			<%
			}
			%>

		</ul>
	</c:if>
</c:if>