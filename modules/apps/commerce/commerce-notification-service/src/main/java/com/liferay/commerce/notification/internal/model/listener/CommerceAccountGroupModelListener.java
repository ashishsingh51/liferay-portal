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

package com.liferay.commerce.notification.internal.model.listener;

import com.liferay.commerce.account.model.CommerceAccountGroup;
import com.liferay.commerce.notification.service.CommerceNotificationTemplateCommerceAccountGroupRelLocalService;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = ModelListener.class)
public class CommerceAccountGroupModelListener
	extends BaseModelListener<CommerceAccountGroup> {

	@Override
	public void onBeforeRemove(CommerceAccountGroup commerceAccountGroup) {
		_commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			deleteCNTemplateCommerceAccountGroupRelsByCommerceNotificationTemplateId(
				commerceAccountGroup.getCommerceAccountGroupId());
	}

	@Reference
	private CommerceNotificationTemplateCommerceAccountGroupRelLocalService
		_commerceNotificationTemplateCommerceAccountGroupRelLocalService;

}