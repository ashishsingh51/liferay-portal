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

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<div class="portlet-configuration-body-content">
		<div class="container-fluid container-fluid-max-xl">
			<div class="sheet">
				<div class="panel-group panel-group-flush">
					<aui:fieldset>
						<div class="display-template">
							<liferay-template:template-selector
								className="<%= CommerceOrderContentPortlet.class.getName() %>"
								displayStyle="<%= commerceOrderContentDisplayContext.getDisplayStyle(CommercePortletKeys.COMMERCE_ORDER_CONTENT) %>"
								displayStyleGroupId="<%= commerceOrderContentDisplayContext.getDisplayStyleGroupId(CommercePortletKeys.COMMERCE_ORDER_CONTENT) %>"
								refreshURL="<%= PortalUtil.getCurrentURL(request) %>"
								showEmptyOption="<%= true %>"
							/>
						</div>
					</aui:fieldset>

					<aui:fieldset collapsible="<%= true %>" label="order-date-display">
						<aui:input checked="<%= commerceOrderContentDisplayContext.isShowCommerceOrderCreateTime() %>" id="showCommerceOrderCreateTime" label="show-commerce-order-create-time" name="preferences--showCommerceOrderCreateTime--" type="toggle-switch" />
					</aui:fieldset>
				</div>
			</div>
		</div>
	</div>

	<aui:button-row>
		<aui:button cssClass="btn-lg" name="submitButton" type="submit" value="save" />
	</aui:button-row>
</aui:form>