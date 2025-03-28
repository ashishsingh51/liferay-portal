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

package com.liferay.knowledge.base.configuration;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Alicia García
 */
@ProviderType
public interface KBServiceConfigurationProvider {

	public int getCheckInterval(long companyId) throws ConfigurationException;

	public int getExpirationDateNotificationDateWeeks(long companyId)
		throws ConfigurationException;

	public void updateExpirationDateConfiguration(
			int checkInterval, long companyId,
			int expirationDateNotificationDateWeeks)
		throws Exception;

}