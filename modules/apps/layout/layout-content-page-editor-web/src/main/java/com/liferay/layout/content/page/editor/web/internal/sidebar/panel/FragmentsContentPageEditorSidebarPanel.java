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

package com.liferay.layout.content.page.editor.web.internal.sidebar.panel;

import com.liferay.layout.content.page.editor.sidebar.panel.ContentPageEditorSidebarPanel;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.LayoutPermission;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "service.ranking:Integer=800",
	service = ContentPageEditorSidebarPanel.class
)
public class FragmentsContentPageEditorSidebarPanel
	implements ContentPageEditorSidebarPanel {

	@Override
	public String getIcon() {
		return "plus";
	}

	@Override
	public String getId() {
		return "fragments_and_widgets";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "fragments-and-widgets");
	}

	@Override
	public boolean isVisible(
		PermissionChecker permissionChecker, long plid, int layoutType) {

		try {
			if (_layoutPermission.containsLayoutRestrictedUpdatePermission(
					permissionChecker, plid)) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentsContentPageEditorSidebarPanel.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutPermission _layoutPermission;

}