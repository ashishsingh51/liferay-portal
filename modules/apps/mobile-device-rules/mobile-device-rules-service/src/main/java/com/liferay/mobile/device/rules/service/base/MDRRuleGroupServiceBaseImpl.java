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

package com.liferay.mobile.device.rules.service.base;

import com.liferay.mobile.device.rules.model.MDRRuleGroup;
import com.liferay.mobile.device.rules.service.MDRRuleGroupService;
import com.liferay.mobile.device.rules.service.persistence.MDRRuleGroupFinder;
import com.liferay.mobile.device.rules.service.persistence.MDRRuleGroupPersistence;
import com.liferay.mobile.device.rules.service.persistence.MDRRulePersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.service.BaseServiceImpl;
import com.liferay.portal.kernel.util.PortalUtil;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Reference;

/**
 * Provides the base implementation for the mdr rule group remote service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.mobile.device.rules.service.impl.MDRRuleGroupServiceImpl}.
 * </p>
 *
 * @author Edward C. Han
 * @see com.liferay.mobile.device.rules.service.impl.MDRRuleGroupServiceImpl
 * @generated
 */
public abstract class MDRRuleGroupServiceBaseImpl
	extends BaseServiceImpl
	implements MDRRuleGroupService, AopService, IdentifiableOSGiService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>MDRRuleGroupService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.mobile.device.rules.service.MDRRuleGroupServiceUtil</code>.
	 */
	@Override
	public Class<?>[] getAopInterfaces() {
		return new Class<?>[] {
			MDRRuleGroupService.class, IdentifiableOSGiService.class
		};
	}

	@Override
	public void setAopProxy(Object aopProxy) {
		mdrRuleGroupService = (MDRRuleGroupService)aopProxy;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return MDRRuleGroupService.class.getName();
	}

	protected Class<?> getModelClass() {
		return MDRRuleGroup.class;
	}

	protected String getModelClassName() {
		return MDRRuleGroup.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource = mdrRuleGroupPersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	@Reference
	protected com.liferay.mobile.device.rules.service.MDRRuleGroupLocalService
		mdrRuleGroupLocalService;

	protected MDRRuleGroupService mdrRuleGroupService;

	@Reference
	protected MDRRuleGroupPersistence mdrRuleGroupPersistence;

	@Reference
	protected MDRRuleGroupFinder mdrRuleGroupFinder;

	@Reference
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

	@Reference
	protected MDRRulePersistence mdrRulePersistence;

	@Reference
	protected com.liferay.portal.kernel.service.GroupLocalService
		groupLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.GroupService groupService;

	@Reference
	protected com.liferay.portal.kernel.service.ResourceLocalService
		resourceLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.UserLocalService
		userLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.UserService userService;

}