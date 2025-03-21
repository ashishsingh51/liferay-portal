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

package com.liferay.wiki.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The base model interface for the WikiPageResource service. Represents a row in the &quot;WikiPageResource&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation <code>com.liferay.wiki.model.impl.WikiPageResourceModelImpl</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in <code>com.liferay.wiki.model.impl.WikiPageResourceImpl</code>.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see WikiPageResource
 * @generated
 */
@ProviderType
public interface WikiPageResourceModel
	extends BaseModel<WikiPageResource>, CTModel<WikiPageResource>, MVCCModel,
			ShardedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a wiki page resource model instance should use the {@link WikiPageResource} interface instead.
	 */

	/**
	 * Returns the primary key of this wiki page resource.
	 *
	 * @return the primary key of this wiki page resource
	 */
	@Override
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this wiki page resource.
	 *
	 * @param primaryKey the primary key of this wiki page resource
	 */
	@Override
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the mvcc version of this wiki page resource.
	 *
	 * @return the mvcc version of this wiki page resource
	 */
	@Override
	public long getMvccVersion();

	/**
	 * Sets the mvcc version of this wiki page resource.
	 *
	 * @param mvccVersion the mvcc version of this wiki page resource
	 */
	@Override
	public void setMvccVersion(long mvccVersion);

	/**
	 * Returns the ct collection ID of this wiki page resource.
	 *
	 * @return the ct collection ID of this wiki page resource
	 */
	@Override
	public long getCtCollectionId();

	/**
	 * Sets the ct collection ID of this wiki page resource.
	 *
	 * @param ctCollectionId the ct collection ID of this wiki page resource
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId);

	/**
	 * Returns the uuid of this wiki page resource.
	 *
	 * @return the uuid of this wiki page resource
	 */
	@AutoEscape
	public String getUuid();

	/**
	 * Sets the uuid of this wiki page resource.
	 *
	 * @param uuid the uuid of this wiki page resource
	 */
	public void setUuid(String uuid);

	/**
	 * Returns the resource prim key of this wiki page resource.
	 *
	 * @return the resource prim key of this wiki page resource
	 */
	public long getResourcePrimKey();

	/**
	 * Sets the resource prim key of this wiki page resource.
	 *
	 * @param resourcePrimKey the resource prim key of this wiki page resource
	 */
	public void setResourcePrimKey(long resourcePrimKey);

	/**
	 * Returns the group ID of this wiki page resource.
	 *
	 * @return the group ID of this wiki page resource
	 */
	public long getGroupId();

	/**
	 * Sets the group ID of this wiki page resource.
	 *
	 * @param groupId the group ID of this wiki page resource
	 */
	public void setGroupId(long groupId);

	/**
	 * Returns the company ID of this wiki page resource.
	 *
	 * @return the company ID of this wiki page resource
	 */
	@Override
	public long getCompanyId();

	/**
	 * Sets the company ID of this wiki page resource.
	 *
	 * @param companyId the company ID of this wiki page resource
	 */
	@Override
	public void setCompanyId(long companyId);

	/**
	 * Returns the node ID of this wiki page resource.
	 *
	 * @return the node ID of this wiki page resource
	 */
	public long getNodeId();

	/**
	 * Sets the node ID of this wiki page resource.
	 *
	 * @param nodeId the node ID of this wiki page resource
	 */
	public void setNodeId(long nodeId);

	/**
	 * Returns the title of this wiki page resource.
	 *
	 * @return the title of this wiki page resource
	 */
	@AutoEscape
	public String getTitle();

	/**
	 * Sets the title of this wiki page resource.
	 *
	 * @param title the title of this wiki page resource
	 */
	public void setTitle(String title);

	@Override
	public WikiPageResource cloneWithOriginalValues();

	public default String toXmlString() {
		return null;
	}

}