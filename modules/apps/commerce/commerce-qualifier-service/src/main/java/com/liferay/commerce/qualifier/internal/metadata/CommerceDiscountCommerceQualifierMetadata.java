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

package com.liferay.commerce.qualifier.internal.metadata;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountTable;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.qualifier.configuration.CommerceDiscountCommerceQualifierConfiguration;
import com.liferay.commerce.qualifier.metadata.BaseCommerceQualifierMetadata;
import com.liferay.commerce.qualifier.metadata.CommerceQualifierMetadata;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.PersistedModelLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	configurationPid = "com.liferay.commerce.qualifier.configuration.CommerceDiscountCommerceQualifierConfiguration",
	service = {CommerceQualifierMetadata.class, ModelListener.class}
)
public class CommerceDiscountCommerceQualifierMetadata
	extends BaseCommerceQualifierMetadata<CommerceDiscount> {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return CommerceDiscountCommerceQualifierConfiguration.class;
	}

	@Override
	public String getKey() {
		return "discount";
	}

	@Override
	public Column<?, String> getKeywordsColumn() {
		return CommerceDiscountTable.INSTANCE.title;
	}

	@Override
	public Class<CommerceDiscount> getModelClass() {
		return CommerceDiscount.class;
	}

	@Override
	public String getModelClassName() {
		return CommerceDiscount.class.getName();
	}

	@Override
	public ModelResourcePermission<CommerceDiscount>
		getModelResourcePermission() {

		return _commerceDiscountModelResourcePermission;
	}

	@Override
	public PersistedModelLocalService getPersistedModelLocalService() {
		return _commerceDiscountLocalService;
	}

	@Override
	public Column<?, Long> getPrimaryKeyColumn() {
		return CommerceDiscountTable.INSTANCE.commerceDiscountId;
	}

	@Override
	public Table getTable() {
		return CommerceDiscountTable.INSTANCE;
	}

	@Override
	protected OrderByExpression[] getAdditionalOrderByExpressions(
		Map<String, ?> targetAttributes) {

		return new OrderByExpression[] {
			CommerceDiscountTable.INSTANCE.commerceDiscountId.descending()
		};
	}

	@Reference
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.discount.model.CommerceDiscount)"
	)
	private ModelResourcePermission<CommerceDiscount>
		_commerceDiscountModelResourcePermission;

}