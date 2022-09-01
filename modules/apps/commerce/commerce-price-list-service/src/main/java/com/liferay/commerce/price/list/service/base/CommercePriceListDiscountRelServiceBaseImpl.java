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

import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.commerce.price.list.service.CommercePriceListDiscountRelService;
import com.liferay.commerce.price.list.service.CommercePriceListDiscountRelServiceUtil;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListDiscountRelPersistence;
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
 * Provides the base implementation for the commerce price list discount rel remote service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.commerce.price.list.service.impl.CommercePriceListDiscountRelServiceImpl}.
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see com.liferay.commerce.price.list.service.impl.CommercePriceListDiscountRelServiceImpl
 * @generated
 */
public abstract class CommercePriceListDiscountRelServiceBaseImpl
	extends BaseServiceImpl
	implements CommercePriceListDiscountRelService, IdentifiableOSGiService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>CommercePriceListDiscountRelService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>CommercePriceListDiscountRelServiceUtil</code>.
	 */

	/**
	 * Returns the commerce price list discount rel local service.
	 *
	 * @return the commerce price list discount rel local service
	 */
	public com.liferay.commerce.price.list.service.
		CommercePriceListDiscountRelLocalService
			getCommercePriceListDiscountRelLocalService() {

		return commercePriceListDiscountRelLocalService;
	}

	/**
	 * Sets the commerce price list discount rel local service.
	 *
	 * @param commercePriceListDiscountRelLocalService the commerce price list discount rel local service
	 */
	public void setCommercePriceListDiscountRelLocalService(
		com.liferay.commerce.price.list.service.
			CommercePriceListDiscountRelLocalService
				commercePriceListDiscountRelLocalService) {

		this.commercePriceListDiscountRelLocalService =
			commercePriceListDiscountRelLocalService;
	}

	/**
	 * Returns the commerce price list discount rel remote service.
	 *
	 * @return the commerce price list discount rel remote service
	 */
	public CommercePriceListDiscountRelService
		getCommercePriceListDiscountRelService() {

		return commercePriceListDiscountRelService;
	}

	/**
	 * Sets the commerce price list discount rel remote service.
	 *
	 * @param commercePriceListDiscountRelService the commerce price list discount rel remote service
	 */
	public void setCommercePriceListDiscountRelService(
		CommercePriceListDiscountRelService
			commercePriceListDiscountRelService) {

		this.commercePriceListDiscountRelService =
			commercePriceListDiscountRelService;
	}

	/**
	 * Returns the commerce price list discount rel persistence.
	 *
	 * @return the commerce price list discount rel persistence
	 */
	public CommercePriceListDiscountRelPersistence
		getCommercePriceListDiscountRelPersistence() {

		return commercePriceListDiscountRelPersistence;
	}

	/**
	 * Sets the commerce price list discount rel persistence.
	 *
	 * @param commercePriceListDiscountRelPersistence the commerce price list discount rel persistence
	 */
	public void setCommercePriceListDiscountRelPersistence(
		CommercePriceListDiscountRelPersistence
			commercePriceListDiscountRelPersistence) {

		this.commercePriceListDiscountRelPersistence =
			commercePriceListDiscountRelPersistence;
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
		_setServiceUtilService(commercePriceListDiscountRelService);
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
		return CommercePriceListDiscountRelService.class.getName();
	}

	protected Class<?> getModelClass() {
		return CommercePriceListDiscountRel.class;
	}

	protected String getModelClassName() {
		return CommercePriceListDiscountRel.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource =
				commercePriceListDiscountRelPersistence.getDataSource();

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
		CommercePriceListDiscountRelService
			commercePriceListDiscountRelService) {

		try {
			Field field =
				CommercePriceListDiscountRelServiceUtil.class.getDeclaredField(
					"_service");

			field.setAccessible(true);

			field.set(null, commercePriceListDiscountRelService);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	@BeanReference(
		type = com.liferay.commerce.price.list.service.CommercePriceListDiscountRelLocalService.class
	)
	protected com.liferay.commerce.price.list.service.
		CommercePriceListDiscountRelLocalService
			commercePriceListDiscountRelLocalService;

	@BeanReference(type = CommercePriceListDiscountRelService.class)
	protected CommercePriceListDiscountRelService
		commercePriceListDiscountRelService;

	@BeanReference(type = CommercePriceListDiscountRelPersistence.class)
	protected CommercePriceListDiscountRelPersistence
		commercePriceListDiscountRelPersistence;

	@ServiceReference(
		type = com.liferay.counter.kernel.service.CounterLocalService.class
	)
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

}