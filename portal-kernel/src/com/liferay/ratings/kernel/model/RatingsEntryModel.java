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

package com.liferay.ratings.kernel.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.model.AttachedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.StagedAuditedModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The base model interface for the RatingsEntry service. Represents a row in the &quot;RatingsEntry&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation <code>com.liferay.portlet.ratings.model.impl.RatingsEntryModelImpl</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in <code>com.liferay.portlet.ratings.model.impl.RatingsEntryImpl</code>.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RatingsEntry
 * @generated
 */
@ProviderType
public interface RatingsEntryModel
	extends AttachedModel, BaseModel<RatingsEntry>, CTModel<RatingsEntry>,
			MVCCModel, ShardedModel, StagedAuditedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a ratings entry model instance should use the {@link RatingsEntry} interface instead.
	 */

	/**
	 * Returns the primary key of this ratings entry.
	 *
	 * @return the primary key of this ratings entry
	 */
	@Override
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this ratings entry.
	 *
	 * @param primaryKey the primary key of this ratings entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the mvcc version of this ratings entry.
	 *
	 * @return the mvcc version of this ratings entry
	 */
	@Override
	public long getMvccVersion();

	/**
	 * Sets the mvcc version of this ratings entry.
	 *
	 * @param mvccVersion the mvcc version of this ratings entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion);

	/**
	 * Returns the ct collection ID of this ratings entry.
	 *
	 * @return the ct collection ID of this ratings entry
	 */
	@Override
	public long getCtCollectionId();

	/**
	 * Sets the ct collection ID of this ratings entry.
	 *
	 * @param ctCollectionId the ct collection ID of this ratings entry
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId);

	/**
	 * Returns the uuid of this ratings entry.
	 *
	 * @return the uuid of this ratings entry
	 */
	@AutoEscape
	@Override
	public String getUuid();

	/**
	 * Sets the uuid of this ratings entry.
	 *
	 * @param uuid the uuid of this ratings entry
	 */
	@Override
	public void setUuid(String uuid);

	/**
	 * Returns the entry ID of this ratings entry.
	 *
	 * @return the entry ID of this ratings entry
	 */
	public long getEntryId();

	/**
	 * Sets the entry ID of this ratings entry.
	 *
	 * @param entryId the entry ID of this ratings entry
	 */
	public void setEntryId(long entryId);

	/**
	 * Returns the company ID of this ratings entry.
	 *
	 * @return the company ID of this ratings entry
	 */
	@Override
	public long getCompanyId();

	/**
	 * Sets the company ID of this ratings entry.
	 *
	 * @param companyId the company ID of this ratings entry
	 */
	@Override
	public void setCompanyId(long companyId);

	/**
	 * Returns the user ID of this ratings entry.
	 *
	 * @return the user ID of this ratings entry
	 */
	@Override
	public long getUserId();

	/**
	 * Sets the user ID of this ratings entry.
	 *
	 * @param userId the user ID of this ratings entry
	 */
	@Override
	public void setUserId(long userId);

	/**
	 * Returns the user uuid of this ratings entry.
	 *
	 * @return the user uuid of this ratings entry
	 */
	@Override
	public String getUserUuid();

	/**
	 * Sets the user uuid of this ratings entry.
	 *
	 * @param userUuid the user uuid of this ratings entry
	 */
	@Override
	public void setUserUuid(String userUuid);

	/**
	 * Returns the user name of this ratings entry.
	 *
	 * @return the user name of this ratings entry
	 */
	@AutoEscape
	@Override
	public String getUserName();

	/**
	 * Sets the user name of this ratings entry.
	 *
	 * @param userName the user name of this ratings entry
	 */
	@Override
	public void setUserName(String userName);

	/**
	 * Returns the create date of this ratings entry.
	 *
	 * @return the create date of this ratings entry
	 */
	@Override
	public Date getCreateDate();

	/**
	 * Sets the create date of this ratings entry.
	 *
	 * @param createDate the create date of this ratings entry
	 */
	@Override
	public void setCreateDate(Date createDate);

	/**
	 * Returns the modified date of this ratings entry.
	 *
	 * @return the modified date of this ratings entry
	 */
	@Override
	public Date getModifiedDate();

	/**
	 * Sets the modified date of this ratings entry.
	 *
	 * @param modifiedDate the modified date of this ratings entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate);

	/**
	 * Returns the fully qualified class name of this ratings entry.
	 *
	 * @return the fully qualified class name of this ratings entry
	 */
	@Override
	public String getClassName();

	public void setClassName(String className);

	/**
	 * Returns the class name ID of this ratings entry.
	 *
	 * @return the class name ID of this ratings entry
	 */
	@Override
	public long getClassNameId();

	/**
	 * Sets the class name ID of this ratings entry.
	 *
	 * @param classNameId the class name ID of this ratings entry
	 */
	@Override
	public void setClassNameId(long classNameId);

	/**
	 * Returns the class pk of this ratings entry.
	 *
	 * @return the class pk of this ratings entry
	 */
	@Override
	public long getClassPK();

	/**
	 * Sets the class pk of this ratings entry.
	 *
	 * @param classPK the class pk of this ratings entry
	 */
	@Override
	public void setClassPK(long classPK);

	/**
	 * Returns the score of this ratings entry.
	 *
	 * @return the score of this ratings entry
	 */
	public double getScore();

	/**
	 * Sets the score of this ratings entry.
	 *
	 * @param score the score of this ratings entry
	 */
	public void setScore(double score);

	@Override
	public RatingsEntry cloneWithOriginalValues();

	public default String toXmlString() {
		return null;
	}

}