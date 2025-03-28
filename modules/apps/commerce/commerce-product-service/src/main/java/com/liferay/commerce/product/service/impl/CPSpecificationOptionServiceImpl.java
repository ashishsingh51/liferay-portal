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

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.base.CPSpecificationOptionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CPSpecificationOption"
	},
	service = AopService.class
)
public class CPSpecificationOptionServiceImpl
	extends CPSpecificationOptionServiceBaseImpl {

	@Override
	public CPSpecificationOption addCPSpecificationOption(
			long cpOptionCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, boolean facetable, String key,
			ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_cpSpecificationOptionModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CPActionKeys.ADD_COMMERCE_PRODUCT_SPECIFICATION_OPTION);

		return cpSpecificationOptionLocalService.addCPSpecificationOption(
			getUserId(), cpOptionCategoryId, titleMap, descriptionMap,
			facetable, key, serviceContext);
	}

	@Override
	public void deleteCPSpecificationOption(long cpSpecificationOptionId)
		throws PortalException {

		_cpSpecificationOptionModelResourcePermission.check(
			getPermissionChecker(), cpSpecificationOptionId, ActionKeys.DELETE);

		cpSpecificationOptionLocalService.deleteCPSpecificationOption(
			cpSpecificationOptionId);
	}

	@Override
	public CPSpecificationOption fetchCPSpecificationOption(
			long companyId, String key)
		throws PortalException {

		CPSpecificationOption cpSpecificationOption =
			cpSpecificationOptionLocalService.fetchCPSpecificationOption(
				companyId, key);

		if (cpSpecificationOption != null) {
			_cpSpecificationOptionModelResourcePermission.check(
				getPermissionChecker(), cpSpecificationOption, ActionKeys.VIEW);
		}

		return cpSpecificationOption;
	}

	@Override
	public CPSpecificationOption getCPSpecificationOption(
			long cpSpecificationOptionId)
		throws PortalException {

		_cpSpecificationOptionModelResourcePermission.check(
			getPermissionChecker(), cpSpecificationOptionId, ActionKeys.VIEW);

		return cpSpecificationOptionLocalService.getCPSpecificationOption(
			cpSpecificationOptionId);
	}

	@Override
	public CPSpecificationOption getCPSpecificationOption(
			long companyId, String key)
		throws PortalException {

		CPSpecificationOption cpSpecificationOption =
			cpSpecificationOptionLocalService.getCPSpecificationOption(
				companyId, key);

		_cpSpecificationOptionModelResourcePermission.check(
			getPermissionChecker(), cpSpecificationOption, ActionKeys.VIEW);

		return cpSpecificationOption;
	}

	@Override
	public BaseModelSearchResult<CPSpecificationOption>
			searchCPSpecificationOptions(
				long companyId, Boolean facetable, String keywords, int start,
				int end, Sort sort)
		throws PortalException {

		return cpSpecificationOptionLocalService.searchCPSpecificationOptions(
			companyId, facetable, keywords, start, end, sort);
	}

	@Override
	public CPSpecificationOption updateCPSpecificationOption(
			long cpSpecificationOptionId, long cpOptionCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			boolean facetable, String key, ServiceContext serviceContext)
		throws PortalException {

		_cpSpecificationOptionModelResourcePermission.check(
			getPermissionChecker(), cpSpecificationOptionId, ActionKeys.UPDATE);

		return cpSpecificationOptionLocalService.updateCPSpecificationOption(
			cpSpecificationOptionId, cpOptionCategoryId, titleMap,
			descriptionMap, facetable, key, serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPSpecificationOption)"
	)
	private ModelResourcePermission<CPSpecificationOption>
		_cpSpecificationOptionModelResourcePermission;

}