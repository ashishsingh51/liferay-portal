/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.headless.commerce.machine.learning.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IdEntityField;
import com.liferay.portal.odata.entity.IntegerEntityField;

import java.util.Map;

/**
 * @author Riccardo Ferrari
 */
public class ProductChannelEntityModel implements EntityModel {

	public ProductChannelEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new IdEntityField(
				Field.ENTRY_CLASS_PK, locale -> Field.ENTRY_CLASS_PK,
				String::valueOf),
			new DateTimeEntityField(
				"modified",
				locale -> Field.getSortableFieldName(Field.MODIFIED_DATE),
				locale -> Field.MODIFIED_DATE),
			new IntegerEntityField(
				Field.getSortableFieldName(Field.MODIFIED_DATE),
				locale -> Field.getSortableFieldName(Field.MODIFIED_DATE)));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}