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

package com.liferay.object.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the ObjectField service. Represents a row in the &quot;ObjectField&quot; database table, with each column mapped to a property of this class.
 *
 * @author Marco Leo
 * @see ObjectFieldModel
 * @generated
 */
@ImplementationClassName("com.liferay.object.model.impl.ObjectFieldImpl")
@ProviderType
public interface ObjectField extends ObjectFieldModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.object.model.impl.ObjectFieldImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<ObjectField, Long> OBJECT_FIELD_ID_ACCESSOR =
		new Accessor<ObjectField, Long>() {

			@Override
			public Long get(ObjectField objectField) {
				return objectField.getObjectFieldId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<ObjectField> getTypeClass() {
				return ObjectField.class;
			}

		};

	public boolean compareBusinessType(String businessType);

	public java.util.List<ObjectFieldSetting> getObjectFieldSettings();

	public void setObjectFieldSettings(
		java.util.List<ObjectFieldSetting> objectFieldSettings);

}