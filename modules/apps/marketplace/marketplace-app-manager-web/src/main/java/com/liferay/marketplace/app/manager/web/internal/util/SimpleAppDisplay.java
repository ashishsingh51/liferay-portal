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

package com.liferay.marketplace.app.manager.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.MimeResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.Version;

/**
 * @author Ryan Park
 */
public class SimpleAppDisplay extends BaseAppDisplay {

	public SimpleAppDisplay() {
		_title = APP_TITLE_UNCATEGORIZED;
		_description = StringPool.BLANK;
		_version = null;
	}

	public SimpleAppDisplay(String title, String description, Version version) {
		if (Validator.isNull(title)) {
			_title = APP_TITLE_UNCATEGORIZED;
		}
		else {
			_title = title;
		}

		_description = description;
		_version = version;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public String getDisplayURL(MimeResponse mimeResponse) {
		return PortletURLBuilder.createRenderURL(
			mimeResponse
		).setMVCPath(
			"/view_modules.jsp"
		).setParameter(
			"app", _title
		).buildString();
	}

	@Override
	public String getIconURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getPathThemeSpritemap() + "#apps";
	}

	@Override
	public String getStoreURL(HttpServletRequest httpServletRequest) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public String getVersion() {
		return _version.toString();
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	private final String _description;
	private final String _title;
	private final Version _version;

}