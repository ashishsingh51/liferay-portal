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

package com.liferay.commerce.pricing.model.impl;

import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.commerce.pricing.model.CommercePriceModifierRelModel;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.BaseModelImpl;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.sql.Blob;
import java.sql.Types;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The base model implementation for the CommercePriceModifierRel service. Represents a row in the &quot;CommercePriceModifierRel&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface <code>CommercePriceModifierRelModel</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link CommercePriceModifierRelImpl}.
 * </p>
 *
 * @author Riccardo Alberti
 * @see CommercePriceModifierRelImpl
 * @generated
 */
@JSON(strict = true)
public class CommercePriceModifierRelModelImpl
	extends BaseModelImpl<CommercePriceModifierRel>
	implements CommercePriceModifierRelModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a commerce price modifier rel model instance should use the <code>CommercePriceModifierRel</code> interface instead.
	 */
	public static final String TABLE_NAME = "CommercePriceModifierRel";

	public static final Object[][] TABLE_COLUMNS = {
		{"mvccVersion", Types.BIGINT}, {"ctCollectionId", Types.BIGINT},
		{"commercePriceModifierRelId", Types.BIGINT},
		{"companyId", Types.BIGINT}, {"userId", Types.BIGINT},
		{"userName", Types.VARCHAR}, {"createDate", Types.TIMESTAMP},
		{"modifiedDate", Types.TIMESTAMP},
		{"commercePriceModifierId", Types.BIGINT},
		{"classNameId", Types.BIGINT}, {"classPK", Types.BIGINT}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
		new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("mvccVersion", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("ctCollectionId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("commercePriceModifierRelId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("modifiedDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("commercePriceModifierId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("classNameId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("classPK", Types.BIGINT);
	}

	public static final String TABLE_SQL_CREATE =
		"create table CommercePriceModifierRel (mvccVersion LONG default 0 not null,ctCollectionId LONG default 0 not null,commercePriceModifierRelId LONG not null,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,commercePriceModifierId LONG,classNameId LONG,classPK LONG,primary key (commercePriceModifierRelId, ctCollectionId))";

	public static final String TABLE_SQL_DROP =
		"drop table CommercePriceModifierRel";

	public static final String ORDER_BY_JPQL =
		" ORDER BY commercePriceModifierRel.createDate DESC";

	public static final String ORDER_BY_SQL =
		" ORDER BY CommercePriceModifierRel.createDate DESC";

	public static final String DATA_SOURCE = "liferayDataSource";

	public static final String SESSION_FACTORY = "liferaySessionFactory";

	public static final String TX_MANAGER = "liferayTransactionManager";

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long CLASSNAMEID_COLUMN_BITMASK = 1L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long CLASSPK_COLUMN_BITMASK = 2L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long COMMERCEPRICEMODIFIERID_COLUMN_BITMASK = 4L;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *		#getColumnBitmask(String)}
	 */
	@Deprecated
	public static final long CREATEDATE_COLUMN_BITMASK = 8L;

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static void setEntityCacheEnabled(boolean entityCacheEnabled) {
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static void setFinderCacheEnabled(boolean finderCacheEnabled) {
	}

	public CommercePriceModifierRelModelImpl() {
	}

	@Override
	public long getPrimaryKey() {
		return _commercePriceModifierRelId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setCommercePriceModifierRelId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _commercePriceModifierRelId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Class<?> getModelClass() {
		return CommercePriceModifierRel.class;
	}

	@Override
	public String getModelClassName() {
		return CommercePriceModifierRel.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		Map<String, Function<CommercePriceModifierRel, Object>>
			attributeGetterFunctions = getAttributeGetterFunctions();

		for (Map.Entry<String, Function<CommercePriceModifierRel, Object>>
				entry : attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<CommercePriceModifierRel, Object> attributeGetterFunction =
				entry.getValue();

			attributes.put(
				attributeName,
				attributeGetterFunction.apply((CommercePriceModifierRel)this));
		}

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Map<String, BiConsumer<CommercePriceModifierRel, Object>>
			attributeSetterBiConsumers = getAttributeSetterBiConsumers();

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String attributeName = entry.getKey();

			BiConsumer<CommercePriceModifierRel, Object>
				attributeSetterBiConsumer = attributeSetterBiConsumers.get(
					attributeName);

			if (attributeSetterBiConsumer != null) {
				attributeSetterBiConsumer.accept(
					(CommercePriceModifierRel)this, entry.getValue());
			}
		}
	}

	public Map<String, Function<CommercePriceModifierRel, Object>>
		getAttributeGetterFunctions() {

		return _attributeGetterFunctions;
	}

	public Map<String, BiConsumer<CommercePriceModifierRel, Object>>
		getAttributeSetterBiConsumers() {

		return _attributeSetterBiConsumers;
	}

	private static final Map<String, Function<CommercePriceModifierRel, Object>>
		_attributeGetterFunctions;
	private static final Map
		<String, BiConsumer<CommercePriceModifierRel, Object>>
			_attributeSetterBiConsumers;

	static {
		Map<String, Function<CommercePriceModifierRel, Object>>
			attributeGetterFunctions =
				new LinkedHashMap
					<String, Function<CommercePriceModifierRel, Object>>();
		Map<String, BiConsumer<CommercePriceModifierRel, ?>>
			attributeSetterBiConsumers =
				new LinkedHashMap
					<String, BiConsumer<CommercePriceModifierRel, ?>>();

		attributeGetterFunctions.put(
			"mvccVersion", CommercePriceModifierRel::getMvccVersion);
		attributeSetterBiConsumers.put(
			"mvccVersion",
			(BiConsumer<CommercePriceModifierRel, Long>)
				CommercePriceModifierRel::setMvccVersion);
		attributeGetterFunctions.put(
			"ctCollectionId", CommercePriceModifierRel::getCtCollectionId);
		attributeSetterBiConsumers.put(
			"ctCollectionId",
			(BiConsumer<CommercePriceModifierRel, Long>)
				CommercePriceModifierRel::setCtCollectionId);
		attributeGetterFunctions.put(
			"commercePriceModifierRelId",
			CommercePriceModifierRel::getCommercePriceModifierRelId);
		attributeSetterBiConsumers.put(
			"commercePriceModifierRelId",
			(BiConsumer<CommercePriceModifierRel, Long>)
				CommercePriceModifierRel::setCommercePriceModifierRelId);
		attributeGetterFunctions.put(
			"companyId", CommercePriceModifierRel::getCompanyId);
		attributeSetterBiConsumers.put(
			"companyId",
			(BiConsumer<CommercePriceModifierRel, Long>)
				CommercePriceModifierRel::setCompanyId);
		attributeGetterFunctions.put(
			"userId", CommercePriceModifierRel::getUserId);
		attributeSetterBiConsumers.put(
			"userId",
			(BiConsumer<CommercePriceModifierRel, Long>)
				CommercePriceModifierRel::setUserId);
		attributeGetterFunctions.put(
			"userName", CommercePriceModifierRel::getUserName);
		attributeSetterBiConsumers.put(
			"userName",
			(BiConsumer<CommercePriceModifierRel, String>)
				CommercePriceModifierRel::setUserName);
		attributeGetterFunctions.put(
			"createDate", CommercePriceModifierRel::getCreateDate);
		attributeSetterBiConsumers.put(
			"createDate",
			(BiConsumer<CommercePriceModifierRel, Date>)
				CommercePriceModifierRel::setCreateDate);
		attributeGetterFunctions.put(
			"modifiedDate", CommercePriceModifierRel::getModifiedDate);
		attributeSetterBiConsumers.put(
			"modifiedDate",
			(BiConsumer<CommercePriceModifierRel, Date>)
				CommercePriceModifierRel::setModifiedDate);
		attributeGetterFunctions.put(
			"commercePriceModifierId",
			CommercePriceModifierRel::getCommercePriceModifierId);
		attributeSetterBiConsumers.put(
			"commercePriceModifierId",
			(BiConsumer<CommercePriceModifierRel, Long>)
				CommercePriceModifierRel::setCommercePriceModifierId);
		attributeGetterFunctions.put(
			"classNameId", CommercePriceModifierRel::getClassNameId);
		attributeSetterBiConsumers.put(
			"classNameId",
			(BiConsumer<CommercePriceModifierRel, Long>)
				CommercePriceModifierRel::setClassNameId);
		attributeGetterFunctions.put(
			"classPK", CommercePriceModifierRel::getClassPK);
		attributeSetterBiConsumers.put(
			"classPK",
			(BiConsumer<CommercePriceModifierRel, Long>)
				CommercePriceModifierRel::setClassPK);

		_attributeGetterFunctions = Collections.unmodifiableMap(
			attributeGetterFunctions);
		_attributeSetterBiConsumers = Collections.unmodifiableMap(
			(Map)attributeSetterBiConsumers);
	}

	@JSON
	@Override
	public long getMvccVersion() {
		return _mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_mvccVersion = mvccVersion;
	}

	@JSON
	@Override
	public long getCtCollectionId() {
		return _ctCollectionId;
	}

	@Override
	public void setCtCollectionId(long ctCollectionId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_ctCollectionId = ctCollectionId;
	}

	@JSON
	@Override
	public long getCommercePriceModifierRelId() {
		return _commercePriceModifierRelId;
	}

	@Override
	public void setCommercePriceModifierRelId(long commercePriceModifierRelId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_commercePriceModifierRelId = commercePriceModifierRelId;
	}

	@JSON
	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public void setCompanyId(long companyId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_companyId = companyId;
	}

	@JSON
	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setUserId(long userId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_userId = userId;
	}

	@Override
	public String getUserUuid() {
		try {
			User user = UserLocalServiceUtil.getUserById(getUserId());

			return user.getUuid();
		}
		catch (PortalException portalException) {
			return "";
		}
	}

	@Override
	public void setUserUuid(String userUuid) {
	}

	@JSON
	@Override
	public String getUserName() {
		if (_userName == null) {
			return "";
		}
		else {
			return _userName;
		}
	}

	@Override
	public void setUserName(String userName) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_userName = userName;
	}

	@JSON
	@Override
	public Date getCreateDate() {
		return _createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_createDate = createDate;
	}

	@JSON
	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public boolean hasSetModifiedDate() {
		return _setModifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_setModifiedDate = true;

		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_modifiedDate = modifiedDate;
	}

	@JSON
	@Override
	public long getCommercePriceModifierId() {
		return _commercePriceModifierId;
	}

	@Override
	public void setCommercePriceModifierId(long commercePriceModifierId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_commercePriceModifierId = commercePriceModifierId;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalCommercePriceModifierId() {
		return GetterUtil.getLong(
			this.<Long>getColumnOriginalValue("commercePriceModifierId"));
	}

	@Override
	public String getClassName() {
		if (getClassNameId() <= 0) {
			return "";
		}

		return PortalUtil.getClassName(getClassNameId());
	}

	@Override
	public void setClassName(String className) {
		long classNameId = 0;

		if (Validator.isNotNull(className)) {
			classNameId = PortalUtil.getClassNameId(className);
		}

		setClassNameId(classNameId);
	}

	@JSON
	@Override
	public long getClassNameId() {
		return _classNameId;
	}

	@Override
	public void setClassNameId(long classNameId) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_classNameId = classNameId;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalClassNameId() {
		return GetterUtil.getLong(
			this.<Long>getColumnOriginalValue("classNameId"));
	}

	@JSON
	@Override
	public long getClassPK() {
		return _classPK;
	}

	@Override
	public void setClassPK(long classPK) {
		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		_classPK = classPK;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getColumnOriginalValue(String)}
	 */
	@Deprecated
	public long getOriginalClassPK() {
		return GetterUtil.getLong(this.<Long>getColumnOriginalValue("classPK"));
	}

	public long getColumnBitmask() {
		if (_columnBitmask > 0) {
			return _columnBitmask;
		}

		if ((_columnOriginalValues == null) ||
			(_columnOriginalValues == Collections.EMPTY_MAP)) {

			return 0;
		}

		for (Map.Entry<String, Object> entry :
				_columnOriginalValues.entrySet()) {

			if (!Objects.equals(
					entry.getValue(), getColumnValue(entry.getKey()))) {

				_columnBitmask |= _columnBitmasks.get(entry.getKey());
			}
		}

		return _columnBitmask;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(
			getCompanyId(), CommercePriceModifierRel.class.getName(),
			getPrimaryKey());
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		ExpandoBridge expandoBridge = getExpandoBridge();

		expandoBridge.setAttributes(serviceContext);
	}

	@Override
	public CommercePriceModifierRel toEscapedModel() {
		if (_escapedModel == null) {
			Function<InvocationHandler, CommercePriceModifierRel>
				escapedModelProxyProviderFunction =
					EscapedModelProxyProviderFunctionHolder.
						_escapedModelProxyProviderFunction;

			_escapedModel = escapedModelProxyProviderFunction.apply(
				new AutoEscapeBeanHandler(this));
		}

		return _escapedModel;
	}

	@Override
	public Object clone() {
		CommercePriceModifierRelImpl commercePriceModifierRelImpl =
			new CommercePriceModifierRelImpl();

		commercePriceModifierRelImpl.setMvccVersion(getMvccVersion());
		commercePriceModifierRelImpl.setCtCollectionId(getCtCollectionId());
		commercePriceModifierRelImpl.setCommercePriceModifierRelId(
			getCommercePriceModifierRelId());
		commercePriceModifierRelImpl.setCompanyId(getCompanyId());
		commercePriceModifierRelImpl.setUserId(getUserId());
		commercePriceModifierRelImpl.setUserName(getUserName());
		commercePriceModifierRelImpl.setCreateDate(getCreateDate());
		commercePriceModifierRelImpl.setModifiedDate(getModifiedDate());
		commercePriceModifierRelImpl.setCommercePriceModifierId(
			getCommercePriceModifierId());
		commercePriceModifierRelImpl.setClassNameId(getClassNameId());
		commercePriceModifierRelImpl.setClassPK(getClassPK());

		commercePriceModifierRelImpl.resetOriginalValues();

		return commercePriceModifierRelImpl;
	}

	@Override
	public CommercePriceModifierRel cloneWithOriginalValues() {
		CommercePriceModifierRelImpl commercePriceModifierRelImpl =
			new CommercePriceModifierRelImpl();

		commercePriceModifierRelImpl.setMvccVersion(
			this.<Long>getColumnOriginalValue("mvccVersion"));
		commercePriceModifierRelImpl.setCtCollectionId(
			this.<Long>getColumnOriginalValue("ctCollectionId"));
		commercePriceModifierRelImpl.setCommercePriceModifierRelId(
			this.<Long>getColumnOriginalValue("commercePriceModifierRelId"));
		commercePriceModifierRelImpl.setCompanyId(
			this.<Long>getColumnOriginalValue("companyId"));
		commercePriceModifierRelImpl.setUserId(
			this.<Long>getColumnOriginalValue("userId"));
		commercePriceModifierRelImpl.setUserName(
			this.<String>getColumnOriginalValue("userName"));
		commercePriceModifierRelImpl.setCreateDate(
			this.<Date>getColumnOriginalValue("createDate"));
		commercePriceModifierRelImpl.setModifiedDate(
			this.<Date>getColumnOriginalValue("modifiedDate"));
		commercePriceModifierRelImpl.setCommercePriceModifierId(
			this.<Long>getColumnOriginalValue("commercePriceModifierId"));
		commercePriceModifierRelImpl.setClassNameId(
			this.<Long>getColumnOriginalValue("classNameId"));
		commercePriceModifierRelImpl.setClassPK(
			this.<Long>getColumnOriginalValue("classPK"));

		return commercePriceModifierRelImpl;
	}

	@Override
	public int compareTo(CommercePriceModifierRel commercePriceModifierRel) {
		int value = 0;

		value = DateUtil.compareTo(
			getCreateDate(), commercePriceModifierRel.getCreateDate());

		value = value * -1;

		if (value != 0) {
			return value;
		}

		return 0;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CommercePriceModifierRel)) {
			return false;
		}

		CommercePriceModifierRel commercePriceModifierRel =
			(CommercePriceModifierRel)object;

		long primaryKey = commercePriceModifierRel.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isEntityCacheEnabled() {
		return true;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public boolean isFinderCacheEnabled() {
		return true;
	}

	@Override
	public void resetOriginalValues() {
		_columnOriginalValues = Collections.emptyMap();

		_setModifiedDate = false;

		_columnBitmask = 0;
	}

	@Override
	public CacheModel<CommercePriceModifierRel> toCacheModel() {
		CommercePriceModifierRelCacheModel commercePriceModifierRelCacheModel =
			new CommercePriceModifierRelCacheModel();

		commercePriceModifierRelCacheModel.mvccVersion = getMvccVersion();

		commercePriceModifierRelCacheModel.ctCollectionId = getCtCollectionId();

		commercePriceModifierRelCacheModel.commercePriceModifierRelId =
			getCommercePriceModifierRelId();

		commercePriceModifierRelCacheModel.companyId = getCompanyId();

		commercePriceModifierRelCacheModel.userId = getUserId();

		commercePriceModifierRelCacheModel.userName = getUserName();

		String userName = commercePriceModifierRelCacheModel.userName;

		if ((userName != null) && (userName.length() == 0)) {
			commercePriceModifierRelCacheModel.userName = null;
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			commercePriceModifierRelCacheModel.createDate =
				createDate.getTime();
		}
		else {
			commercePriceModifierRelCacheModel.createDate = Long.MIN_VALUE;
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			commercePriceModifierRelCacheModel.modifiedDate =
				modifiedDate.getTime();
		}
		else {
			commercePriceModifierRelCacheModel.modifiedDate = Long.MIN_VALUE;
		}

		commercePriceModifierRelCacheModel.commercePriceModifierId =
			getCommercePriceModifierId();

		commercePriceModifierRelCacheModel.classNameId = getClassNameId();

		commercePriceModifierRelCacheModel.classPK = getClassPK();

		return commercePriceModifierRelCacheModel;
	}

	@Override
	public String toString() {
		Map<String, Function<CommercePriceModifierRel, Object>>
			attributeGetterFunctions = getAttributeGetterFunctions();

		StringBundler sb = new StringBundler(
			(5 * attributeGetterFunctions.size()) + 2);

		sb.append("{");

		for (Map.Entry<String, Function<CommercePriceModifierRel, Object>>
				entry : attributeGetterFunctions.entrySet()) {

			String attributeName = entry.getKey();
			Function<CommercePriceModifierRel, Object> attributeGetterFunction =
				entry.getValue();

			sb.append("\"");
			sb.append(attributeName);
			sb.append("\": ");

			Object value = attributeGetterFunction.apply(
				(CommercePriceModifierRel)this);

			if (value == null) {
				sb.append("null");
			}
			else if (value instanceof Blob || value instanceof Date ||
					 value instanceof Map || value instanceof String) {

				sb.append(
					"\"" + StringUtil.replace(value.toString(), "\"", "'") +
						"\"");
			}
			else {
				sb.append(value);
			}

			sb.append(", ");
		}

		if (sb.index() > 1) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append("}");

		return sb.toString();
	}

	private static class EscapedModelProxyProviderFunctionHolder {

		private static final Function
			<InvocationHandler, CommercePriceModifierRel>
				_escapedModelProxyProviderFunction =
					ProxyUtil.getProxyProviderFunction(
						CommercePriceModifierRel.class, ModelWrapper.class);

	}

	private long _mvccVersion;
	private long _ctCollectionId;
	private long _commercePriceModifierRelId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private boolean _setModifiedDate;
	private long _commercePriceModifierId;
	private long _classNameId;
	private long _classPK;

	public <T> T getColumnValue(String columnName) {
		Function<CommercePriceModifierRel, Object> function =
			_attributeGetterFunctions.get(columnName);

		if (function == null) {
			throw new IllegalArgumentException(
				"No attribute getter function found for " + columnName);
		}

		return (T)function.apply((CommercePriceModifierRel)this);
	}

	public <T> T getColumnOriginalValue(String columnName) {
		if (_columnOriginalValues == null) {
			return null;
		}

		if (_columnOriginalValues == Collections.EMPTY_MAP) {
			_setColumnOriginalValues();
		}

		return (T)_columnOriginalValues.get(columnName);
	}

	private void _setColumnOriginalValues() {
		_columnOriginalValues = new HashMap<String, Object>();

		_columnOriginalValues.put("mvccVersion", _mvccVersion);
		_columnOriginalValues.put("ctCollectionId", _ctCollectionId);
		_columnOriginalValues.put(
			"commercePriceModifierRelId", _commercePriceModifierRelId);
		_columnOriginalValues.put("companyId", _companyId);
		_columnOriginalValues.put("userId", _userId);
		_columnOriginalValues.put("userName", _userName);
		_columnOriginalValues.put("createDate", _createDate);
		_columnOriginalValues.put("modifiedDate", _modifiedDate);
		_columnOriginalValues.put(
			"commercePriceModifierId", _commercePriceModifierId);
		_columnOriginalValues.put("classNameId", _classNameId);
		_columnOriginalValues.put("classPK", _classPK);
	}

	private transient Map<String, Object> _columnOriginalValues;

	public static long getColumnBitmask(String columnName) {
		return _columnBitmasks.get(columnName);
	}

	private static final Map<String, Long> _columnBitmasks;

	static {
		Map<String, Long> columnBitmasks = new HashMap<>();

		columnBitmasks.put("mvccVersion", 1L);

		columnBitmasks.put("ctCollectionId", 2L);

		columnBitmasks.put("commercePriceModifierRelId", 4L);

		columnBitmasks.put("companyId", 8L);

		columnBitmasks.put("userId", 16L);

		columnBitmasks.put("userName", 32L);

		columnBitmasks.put("createDate", 64L);

		columnBitmasks.put("modifiedDate", 128L);

		columnBitmasks.put("commercePriceModifierId", 256L);

		columnBitmasks.put("classNameId", 512L);

		columnBitmasks.put("classPK", 1024L);

		_columnBitmasks = Collections.unmodifiableMap(columnBitmasks);
	}

	private long _columnBitmask;
	private CommercePriceModifierRel _escapedModel;

}