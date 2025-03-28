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

package com.liferay.journal.internal.exportimport.content.processor;

import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.journal.model.JournalFeed;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.util.PropsValues;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalFeed",
	service = {
		ExportImportContentProcessor.class,
		JournalFeedExportImportContentProcessor.class
	}
)
public class JournalFeedExportImportContentProcessor
	implements ExportImportContentProcessor<String> {

	@Override
	public String replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content, boolean exportReferencedContent,
			boolean escapeContent)
		throws Exception {

		JournalFeed feed = (JournalFeed)stagedModel;

		Group group = _groupLocalService.getGroup(
			portletDataContext.getScopeGroupId());

		String newGroupFriendlyURL = group.getFriendlyURL();

		newGroupFriendlyURL = newGroupFriendlyURL.substring(1);

		String[] friendlyURLParts = StringUtil.split(
			feed.getTargetLayoutFriendlyUrl(), StringPool.FORWARD_SLASH);

		String oldGroupFriendlyURL = friendlyURLParts[2];

		String oldTargetLayoutFriendlyURL = feed.getTargetLayoutFriendlyUrl();

		if (newGroupFriendlyURL.equals(oldGroupFriendlyURL)) {
			String targetLayoutFriendlyURL = null;

			if (friendlyURLParts.length > 3) {
				targetLayoutFriendlyURL = StringUtil.replaceFirst(
					feed.getTargetLayoutFriendlyUrl(),
					StringPool.SLASH + newGroupFriendlyURL + StringPool.SLASH,
					StringPool.SLASH + _DATA_HANDLER_GROUP_FRIENDLY_URL +
						StringPool.SLASH);
			}
			else {
				targetLayoutFriendlyURL = StringUtil.replaceFirst(
					feed.getTargetLayoutFriendlyUrl(),
					StringPool.SLASH + newGroupFriendlyURL,
					StringPool.SLASH + _DATA_HANDLER_GROUP_FRIENDLY_URL);
			}

			feed.setTargetLayoutFriendlyUrl(targetLayoutFriendlyURL);
		}

		Group targetLayoutGroup = _groupLocalService.fetchFriendlyURLGroup(
			portletDataContext.getCompanyId(),
			StringPool.SLASH + oldGroupFriendlyURL);

		boolean privateLayout = false;

		if (!PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING.equals(
				StringPool.SLASH + friendlyURLParts[1])) {

			privateLayout = true;
		}

		Layout targetLayout = null;
		String targetLayoutFriendlyURL = null;

		if (friendlyURLParts.length > 3) {
			targetLayoutFriendlyURL = StringUtil.merge(
				Arrays.copyOfRange(
					friendlyURLParts, 3, friendlyURLParts.length),
				StringPool.SLASH);

			targetLayoutFriendlyURL =
				StringPool.SLASH + targetLayoutFriendlyURL;

			targetLayout = _layoutLocalService.fetchLayoutByFriendlyURL(
				targetLayoutGroup.getGroupId(), privateLayout,
				targetLayoutFriendlyURL);
		}
		else {
			targetLayoutFriendlyURL = oldTargetLayoutFriendlyURL;

			long plid = _portal.getPlidFromFriendlyURL(
				portletDataContext.getCompanyId(), targetLayoutFriendlyURL);

			targetLayout = _layoutLocalService.fetchLayout(plid);
		}

		if (targetLayout == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get target page friendly URL ",
						targetLayoutFriendlyURL, " for feed: ",
						feed.getFeedId()));
			}

			throw new NoSuchLayoutException();
		}

		Element feedElement = portletDataContext.getExportDataElement(feed);

		portletDataContext.addReferenceElement(
			feed, feedElement, targetLayout,
			PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);

		return content;
	}

	@Override
	public String replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String content)
		throws Exception {

		JournalFeed feed = (JournalFeed)stagedModel;

		Group group = _groupLocalService.getGroup(
			portletDataContext.getScopeGroupId());

		String newGroupFriendlyURL = group.getFriendlyURL();

		newGroupFriendlyURL = newGroupFriendlyURL.substring(1);

		String newTargetLayoutFriendlyURL = StringUtil.replace(
			feed.getTargetLayoutFriendlyUrl(), _DATA_HANDLER_GROUP_FRIENDLY_URL,
			newGroupFriendlyURL);

		long plid = _portal.getPlidFromFriendlyURL(
			portletDataContext.getCompanyId(), newTargetLayoutFriendlyURL);

		if (plid <= 0) {
			Group oldGroup = _groupLocalService.fetchGroup(
				portletDataContext.getSourceGroupId());

			if (oldGroup == null) {
				return content;
			}

			String oldGroupFriendlyURL = oldGroup.getFriendlyURL();

			oldGroupFriendlyURL = oldGroupFriendlyURL.substring(1);

			newTargetLayoutFriendlyURL = StringUtil.replace(
				feed.getTargetLayoutFriendlyUrl(),
				_DATA_HANDLER_GROUP_FRIENDLY_URL, oldGroupFriendlyURL);
		}

		feed.setTargetLayoutFriendlyUrl(newTargetLayoutFriendlyURL);

		return content;
	}

	@Override
	public void validateContentReferences(long groupId, String content)
		throws PortalException {

		_defaultTextExportImportContentProcessor.validateContentReferences(
			groupId, content);
	}

	private static final String _DATA_HANDLER_GROUP_FRIENDLY_URL =
		"@data_handler_group_friendly_url@";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalFeedExportImportContentProcessor.class);

	@Reference(target = "(model.class.name=java.lang.String)")
	private ExportImportContentProcessor<String>
		_defaultTextExportImportContentProcessor;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}