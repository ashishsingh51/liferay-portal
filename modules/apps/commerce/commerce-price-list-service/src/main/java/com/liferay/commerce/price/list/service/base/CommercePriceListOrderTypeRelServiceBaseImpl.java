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

package com.liferay.commerce.price.list.service.base;

import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelService;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelServiceUtil;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListOrderTypeRelPersistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.service.BaseServiceImpl;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.lang.reflect.Field;

import javax.sql.DataSource;

/**
 * Provides the base implementation for the commerce price list order type rel remote service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.commerce.price.list.service.impl.CommercePriceListOrderTypeRelServiceImpl}.
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see com.liferay.commerce.price.list.service.impl.CommercePriceListOrderTypeRelServiceImpl
 * @generated
 */
public abstract class CommercePriceListOrderTypeRelServiceBaseImpl
	extends BaseServiceImpl
	implements CommercePriceListOrderTypeRelService, IdentifiableOSGiService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>CommercePriceListOrderTypeRelService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>CommercePriceListOrderTypeRelServiceUtil</code>.
	 */

	/**
	 * Returns the commerce price list order type rel local service.
	 *
	 * @return the commerce price list order type rel local service
	 */
	public com.liferay.commerce.price.list.service.
		CommercePriceListOrderTypeRelLocalService
			getCommercePriceListOrderTypeRelLocalService() {

		return commercePriceListOrderTypeRelLocalService;
	}

	/**
	 * Sets the commerce price list order type rel local service.
	 *
	 * @param commercePriceListOrderTypeRelLocalService the commerce price list order type rel local service
	 */
	public void setCommercePriceListOrderTypeRelLocalService(
		com.liferay.commerce.price.list.service.
			CommercePriceListOrderTypeRelLocalService
				commercePriceListOrderTypeRelLocalService) {

		this.commercePriceListOrderTypeRelLocalService =
			commercePriceListOrderTypeRelLocalService;
	}

	/**
	 * Returns the commerce price list order type rel remote service.
	 *
	 * @return the commerce price list order type rel remote service
	 */
	public CommercePriceListOrderTypeRelService
		getCommercePriceListOrderTypeRelService() {

		return commercePriceListOrderTypeRelService;
	}

	/**
	 * Sets the commerce price list order type rel remote service.
	 *
	 * @param commercePriceListOrderTypeRelService the commerce price list order type rel remote service
	 */
	public void setCommercePriceListOrderTypeRelService(
		CommercePriceListOrderTypeRelService
			commercePriceListOrderTypeRelService) {

		this.commercePriceListOrderTypeRelService =
			commercePriceListOrderTypeRelService;
	}

	/**
	 * Returns the commerce price list order type rel persistence.
	 *
	 * @return the commerce price list order type rel persistence
	 */
	public CommercePriceListOrderTypeRelPersistence
		getCommercePriceListOrderTypeRelPersistence() {

		return commercePriceListOrderTypeRelPersistence;
	}

	/**
	 * Sets the commerce price list order type rel persistence.
	 *
	 * @param commercePriceListOrderTypeRelPersistence the commerce price list order type rel persistence
	 */
	public void setCommercePriceListOrderTypeRelPersistence(
		CommercePriceListOrderTypeRelPersistence
			commercePriceListOrderTypeRelPersistence) {

		this.commercePriceListOrderTypeRelPersistence =
			commercePriceListOrderTypeRelPersistence;
	}

	/**
	 * Returns the counter local service.
	 *
	 * @return the counter local service
	 */
	public com.liferay.counter.kernel.service.CounterLocalService
		getCounterLocalService() {

		return counterLocalService;
	}

	/**
	 * Sets the counter local service.
	 *
	 * @param counterLocalService the counter local service
	 */
	public void setCounterLocalService(
		com.liferay.counter.kernel.service.CounterLocalService
			counterLocalService) {

		this.counterLocalService = counterLocalService;
	}

	public void afterPropertiesSet() {
		_setServiceUtilService(commercePriceListOrderTypeRelService);
	}

	public void destroy() {
		_setServiceUtilService(null);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return CommercePriceListOrderTypeRelService.class.getName();
	}

	protected Class<?> getModelClass() {
		return CommercePriceListOrderTypeRel.class;
	}

	protected String getModelClassName() {
		return CommercePriceListOrderTypeRel.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource =
				commercePriceListOrderTypeRelPersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private void _setServiceUtilService(
		CommercePriceListOrderTypeRelService
			commercePriceListOrderTypeRelService) {

		try {
			Field field =
				CommercePriceListOrderTypeRelServiceUtil.class.getDeclaredField(
					"_service");

			field.setAccessible(true);

			field.set(null, commercePriceListOrderTypeRelService);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	@BeanReference(
		type = com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelLocalService.class
	)
	protected com.liferay.commerce.price.list.service.
		CommercePriceListOrderTypeRelLocalService
			commercePriceListOrderTypeRelLocalService;

	@BeanReference(type = CommercePriceListOrderTypeRelService.class)
	protected CommercePriceListOrderTypeRelService
		commercePriceListOrderTypeRelService;

	@BeanReference(type = CommercePriceListOrderTypeRelPersistence.class)
	protected CommercePriceListOrderTypeRelPersistence
		commercePriceListOrderTypeRelPersistence;

	@ServiceReference(
		type = com.liferay.counter.kernel.service.CounterLocalService.class
	)
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

}