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

package com.liferay.contacts.uad.display;

import com.liferay.user.associated.data.display.UADDisplay;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = UADDisplay.class)
public class EntryUADDisplay extends BaseEntryUADDisplay {

	@Override
	public boolean isSiteScoped() {
		return false;
	}

}