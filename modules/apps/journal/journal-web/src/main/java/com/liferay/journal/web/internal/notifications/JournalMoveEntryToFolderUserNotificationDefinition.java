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

package com.liferay.journal.web.internal.notifications;

import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "javax.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = UserNotificationDefinition.class
)
public class JournalMoveEntryToFolderUserNotificationDefinition
	extends BaseJournalUserNotificationDefinition {

	public JournalMoveEntryToFolderUserNotificationDefinition() {
		super(
			JournalArticleConstants.NOTIFICATION_TYPE_MOVE_ENTRY_TO_FOLDER,
			"receive-a-notification-when-someone-moves-to-folder-web-content-" +
				"you-are-subscribed-to");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"email", UserNotificationDeliveryConstants.TYPE_EMAIL, true,
				true));
		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}