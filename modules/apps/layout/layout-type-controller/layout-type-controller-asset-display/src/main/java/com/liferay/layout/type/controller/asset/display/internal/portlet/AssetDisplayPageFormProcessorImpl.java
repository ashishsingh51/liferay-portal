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

package com.liferay.layout.type.controller.asset.display.internal.portlet;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Date;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = AssetDisplayPageEntryFormProcessor.class)
public class AssetDisplayPageFormProcessorImpl
	implements AssetDisplayPageEntryFormProcessor {

	@Override
	public void process(String className, long classPK, PortletRequest request)
		throws PortalException {

		long classNameId = _portal.getClassNameId(className);

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				themeDisplay.getScopeGroupId(), classNameId, classPK);

		long assetDisplayPageId = ParamUtil.getLong(
			request, "assetDisplayPageId");

		int displayPageType = ParamUtil.getInteger(request, "displayPageType");

		if (displayPageType == AssetDisplayPageConstants.TYPE_NONE) {
			if (assetDisplayPageEntry != null) {
				_assetDisplayPageEntryLocalService.deleteAssetDisplayPageEntry(
					themeDisplay.getScopeGroupId(), classNameId, classPK);
			}
		}
		else if (assetDisplayPageEntry == null) {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				className, request);

			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
				classNameId, classPK, assetDisplayPageId, displayPageType,
				serviceContext);
		}
		else {
			_assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
				assetDisplayPageEntry.getAssetDisplayPageEntryId(),
				assetDisplayPageId, displayPageType);
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				assetDisplayPageId);

		if (layoutPageTemplateEntry != null) {
			layoutPageTemplateEntry.setModifiedDate(new Date());

			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry);
		}
	}

	@Reference
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Portal _portal;

}