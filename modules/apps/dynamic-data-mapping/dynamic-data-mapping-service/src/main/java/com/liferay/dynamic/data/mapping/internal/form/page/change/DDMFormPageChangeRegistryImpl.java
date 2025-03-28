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

package com.liferay.dynamic.data.mapping.internal.form.page.change;

import com.liferay.dynamic.data.mapping.form.page.change.DDMFormPageChange;
import com.liferay.dynamic.data.mapping.form.page.change.DDMFormPageChangeRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Bruno Oliveira
 * @author Carolina Barbosa
 */
@Component(service = DDMFormPageChangeRegistry.class)
public class DDMFormPageChangeRegistryImpl
	implements DDMFormPageChangeRegistry {

	@Override
	public DDMFormPageChange getDDMFormPageChangeByDDMFormInstanceId(
		String ddmFormInstanceId) {

		return _serviceTrackerMap.getService(ddmFormInstanceId);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DDMFormPageChange.class, "ddm.form.instance.id");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private ServiceTrackerMap<String, DDMFormPageChange> _serviceTrackerMap;

}