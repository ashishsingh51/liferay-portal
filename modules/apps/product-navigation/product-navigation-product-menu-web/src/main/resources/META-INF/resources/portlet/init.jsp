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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/application-list" prefix="liferay-application-list" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/staging" prefix="liferay-staging" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.application.list.PanelCategory" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.SessionClicks" %><%@
page import="com.liferay.portal.kernel.webserver.WebServerServletTokenUtil" %><%@
page import="com.liferay.product.navigation.applications.menu.configuration.ApplicationsMenuInstanceConfiguration" %><%@
page import="com.liferay.product.navigation.product.menu.display.context.ProductMenuDisplayContext" %><%@
page import="com.liferay.product.navigation.product.menu.web.internal.constants.ProductNavigationProductMenuWebKeys" %><%@
page import="com.liferay.product.navigation.product.menu.web.internal.display.context.LayoutsTreeDisplayContext" %><%@
page import="com.liferay.taglib.aui.AUIUtil" %><%@
page import="com.liferay.taglib.servlet.PipingServletResponseFactory" %>

<%@ page import="java.util.List" %><%@
page import="java.util.Objects" %>

<liferay-staging:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
ProductMenuDisplayContext productMenuDisplayContext = new ProductMenuDisplayContext(liferayPortletRequest, liferayPortletResponse);
%>