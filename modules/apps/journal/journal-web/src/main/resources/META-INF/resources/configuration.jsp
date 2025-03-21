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
String emailFromAddress = ParamUtil.getString(request, "preferences--emailFromAddress--", journalGroupServiceConfiguration.emailFromAddress());
String emailFromName = ParamUtil.getString(request, "preferences--emailFromName--", journalGroupServiceConfiguration.emailFromName());
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL">
	<portlet:param name="serviceName" value="<%= JournalConstants.SERVICE_NAME %>" />
	<portlet:param name="settingsScope" value="group" />
</liferay-portlet:actionURL>

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	fluid="<%= true %>"
	method="post"
	name="fm"
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<%
	String tabs1Names = "email-from,web-content-added-email,web-content-moved-from-folder-email,web-content-moved-to-folder-email,web-content-review-email,web-content-updated-email";

	if (JournalUtil.hasWorkflowDefinitionsLinks(themeDisplay)) {
		tabs1Names = tabs1Names.concat(",web-content-approval-denied-email,web-content-approval-granted-email,web-content-approval-requested-email");
	}
	%>

	<liferay-frontend:edit-form-body>
		<liferay-ui:tabs
			names="<%= tabs1Names %>"
			refresh="<%= false %>"
		>
			<liferay-ui:error key="emailFromAddress" message="please-enter-a-valid-email-address" />
			<liferay-ui:error key="emailFromName" message="please-enter-a-valid-name" />
			<liferay-ui:error key="emailArticleAddedBody" message="please-enter-a-valid-body" />
			<liferay-ui:error key="emailArticleAddedSubject" message="please-enter-a-valid-subject" />
			<liferay-ui:error key="emailArticleApprovalDeniedBody" message="please-enter-a-valid-body" />
			<liferay-ui:error key="emailArticleApprovalDeniedSubject" message="please-enter-a-valid-subject" />
			<liferay-ui:error key="emailArticleApprovalGrantedBody" message="please-enter-a-valid-body" />
			<liferay-ui:error key="emailArticleApprovalGrantedSubject" message="please-enter-a-valid-subject" />
			<liferay-ui:error key="emailArticleApprovalRequestedBody" message="please-enter-a-valid-body" />
			<liferay-ui:error key="emailArticleApprovalRequestedSubject" message="please-enter-a-valid-subject" />
			<liferay-ui:error key="emailArticleReviewBody" message="please-enter-a-valid-body" />
			<liferay-ui:error key="emailArticleReviewSubject" message="please-enter-a-valid-subject" />
			<liferay-ui:error key="emailArticleUpdatedBody" message="please-enter-a-valid-body" />
			<liferay-ui:error key="emailArticleUpdatedSubject" message="please-enter-a-valid-subject" />

			<liferay-ui:section>
				<liferay-frontend:fieldset>
					<aui:input cssClass="lfr-input-text-container" label="name" name="preferences--emailFromName--" type="text" value="<%= emailFromName %>" />

					<aui:input cssClass="lfr-input-text-container" label="address" name="preferences--emailFromAddress--" type="text" value="<%= emailFromAddress %>" />
				</liferay-frontend:fieldset>
			</liferay-ui:section>

			<%
			Map<String, String> emailDefinitionTerms = JournalUtil.getEmailDefinitionTerms(renderRequest, emailFromAddress, emailFromName);
			%>

			<liferay-ui:section>
				<liferay-frontend:email-notification-settings
					emailBodyLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleAddedBody() %>"
					emailDefinitionTerms="<%= emailDefinitionTerms %>"
					emailEnabled="<%= journalGroupServiceConfiguration.emailArticleAddedEnabled() %>"
					emailParam="emailArticleAdded"
					emailSubjectLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleAddedSubject() %>"
				/>
			</liferay-ui:section>

			<liferay-ui:section>
				<liferay-frontend:email-notification-settings
					emailBodyLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleMovedFromFolderBody() %>"
					emailDefinitionTerms="<%= emailDefinitionTerms %>"
					emailEnabled="<%= journalGroupServiceConfiguration.emailArticleMovedFromFolderEnabled() %>"
					emailParam="emailArticleMovedFromFolder"
					emailSubjectLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleMovedFromFolderSubject() %>"
				/>
			</liferay-ui:section>

			<liferay-ui:section>
				<liferay-frontend:email-notification-settings
					emailBodyLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleMovedToFolderBody() %>"
					emailDefinitionTerms="<%= emailDefinitionTerms %>"
					emailEnabled="<%= journalGroupServiceConfiguration.emailArticleMovedToFolderEnabled() %>"
					emailParam="emailArticleMovedToFolder"
					emailSubjectLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleMovedToFolderSubject() %>"
				/>
			</liferay-ui:section>

			<liferay-ui:section>
				<liferay-frontend:email-notification-settings
					emailBodyLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleReviewBody() %>"
					emailDefinitionTerms="<%= emailDefinitionTerms %>"
					emailEnabled="<%= journalGroupServiceConfiguration.emailArticleReviewEnabled() %>"
					emailParam="emailArticleReview"
					emailSubjectLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleReviewSubject() %>"
				/>
			</liferay-ui:section>

			<liferay-ui:section>
				<liferay-frontend:email-notification-settings
					emailBodyLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleUpdatedBody() %>"
					emailDefinitionTerms="<%= emailDefinitionTerms %>"
					emailEnabled="<%= journalGroupServiceConfiguration.emailArticleUpdatedEnabled() %>"
					emailParam="emailArticleUpdated"
					emailSubjectLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleUpdatedSubject() %>"
				/>
			</liferay-ui:section>

			<c:if test="<%= WorkflowDefinitionLinkLocalServiceUtil.getWorkflowDefinitionLinksCount(themeDisplay.getCompanyId(), scopeGroupId, JournalFolder.class.getName()) > 0 %>">
				<liferay-ui:section>
					<liferay-frontend:email-notification-settings
						emailBodyLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleApprovalDeniedBody() %>"
						emailDefinitionTerms="<%= emailDefinitionTerms %>"
						emailEnabled="<%= journalGroupServiceConfiguration.emailArticleApprovalDeniedEnabled() %>"
						emailParam="emailArticleApprovalDenied"
						emailSubjectLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleApprovalDeniedSubject() %>"
					/>
				</liferay-ui:section>

				<liferay-ui:section>
					<liferay-frontend:email-notification-settings
						emailBodyLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleApprovalGrantedBody() %>"
						emailDefinitionTerms="<%= emailDefinitionTerms %>"
						emailEnabled="<%= journalGroupServiceConfiguration.emailArticleApprovalGrantedEnabled() %>"
						emailParam="emailArticleApprovalGranted"
						emailSubjectLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleApprovalGrantedSubject() %>"
					/>
				</liferay-ui:section>

				<liferay-ui:section>
					<liferay-frontend:email-notification-settings
						emailBodyLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleApprovalRequestedBody() %>"
						emailDefinitionTerms='<%= JournalUtil.getEmailDefinitionTerms(renderRequest, emailFromAddress, emailFromName, "requested") %>'
						emailEnabled="<%= journalGroupServiceConfiguration.emailArticleApprovalRequestedEnabled() %>"
						emailParam="emailArticleApprovalRequested"
						emailSubjectLocalizedValuesMap="<%= journalGroupServiceConfiguration.emailArticleApprovalRequestedSubject() %>"
					/>
				</liferay-ui:section> </c:if> </liferay-ui:tabs>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>