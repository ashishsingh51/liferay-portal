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

package com.liferay.account.internal.search.contributor.sort;

import com.liferay.account.model.AccountGroup;
import com.liferay.portal.search.contributor.sort.SortFieldNameTranslator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 */
@Component(service = SortFieldNameTranslator.class)
public class AccountGroupSortFieldNameTranslator
	implements SortFieldNameTranslator {

	@Override
	public Class<?> getEntityClass() {
		return AccountGroup.class;
	}

	@Override
	public String getSortFieldName(String orderByCol) {
		return orderByCol;
	}

}