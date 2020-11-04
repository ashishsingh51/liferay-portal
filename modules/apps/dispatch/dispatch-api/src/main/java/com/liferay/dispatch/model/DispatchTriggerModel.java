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

package com.liferay.dispatch.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.ShardedModel;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The base model interface for the DispatchTrigger service. Represents a row in the &quot;DispatchTrigger&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation <code>com.liferay.dispatch.model.impl.DispatchTriggerModelImpl</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in <code>com.liferay.dispatch.model.impl.DispatchTriggerImpl</code>.
 * </p>
 *
 * @author Matija Petanjek
 * @see DispatchTrigger
 * @generated
 */
@ProviderType
public interface DispatchTriggerModel
	extends AuditedModel, BaseModel<DispatchTrigger>, MVCCModel, ShardedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a dispatch trigger model instance should use the {@link DispatchTrigger} interface instead.
	 */

	/**
	 * Returns the primary key of this dispatch trigger.
	 *
	 * @return the primary key of this dispatch trigger
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this dispatch trigger.
	 *
	 * @param primaryKey the primary key of this dispatch trigger
	 */
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the mvcc version of this dispatch trigger.
	 *
	 * @return the mvcc version of this dispatch trigger
	 */
	@Override
	public long getMvccVersion();

	/**
	 * Sets the mvcc version of this dispatch trigger.
	 *
	 * @param mvccVersion the mvcc version of this dispatch trigger
	 */
	@Override
	public void setMvccVersion(long mvccVersion);

	/**
	 * Returns the dispatch trigger ID of this dispatch trigger.
	 *
	 * @return the dispatch trigger ID of this dispatch trigger
	 */
	public long getDispatchTriggerId();

	/**
	 * Sets the dispatch trigger ID of this dispatch trigger.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID of this dispatch trigger
	 */
	public void setDispatchTriggerId(long dispatchTriggerId);

	/**
	 * Returns the company ID of this dispatch trigger.
	 *
	 * @return the company ID of this dispatch trigger
	 */
	@Override
	public long getCompanyId();

	/**
	 * Sets the company ID of this dispatch trigger.
	 *
	 * @param companyId the company ID of this dispatch trigger
	 */
	@Override
	public void setCompanyId(long companyId);

	/**
	 * Returns the user ID of this dispatch trigger.
	 *
	 * @return the user ID of this dispatch trigger
	 */
	@Override
	public long getUserId();

	/**
	 * Sets the user ID of this dispatch trigger.
	 *
	 * @param userId the user ID of this dispatch trigger
	 */
	@Override
	public void setUserId(long userId);

	/**
	 * Returns the user uuid of this dispatch trigger.
	 *
	 * @return the user uuid of this dispatch trigger
	 */
	@Override
	public String getUserUuid();

	/**
	 * Sets the user uuid of this dispatch trigger.
	 *
	 * @param userUuid the user uuid of this dispatch trigger
	 */
	@Override
	public void setUserUuid(String userUuid);

	/**
	 * Returns the user name of this dispatch trigger.
	 *
	 * @return the user name of this dispatch trigger
	 */
	@AutoEscape
	@Override
	public String getUserName();

	/**
	 * Sets the user name of this dispatch trigger.
	 *
	 * @param userName the user name of this dispatch trigger
	 */
	@Override
	public void setUserName(String userName);

	/**
	 * Returns the create date of this dispatch trigger.
	 *
	 * @return the create date of this dispatch trigger
	 */
	@Override
	public Date getCreateDate();

	/**
	 * Sets the create date of this dispatch trigger.
	 *
	 * @param createDate the create date of this dispatch trigger
	 */
	@Override
	public void setCreateDate(Date createDate);

	/**
	 * Returns the modified date of this dispatch trigger.
	 *
	 * @return the modified date of this dispatch trigger
	 */
	@Override
	public Date getModifiedDate();

	/**
	 * Sets the modified date of this dispatch trigger.
	 *
	 * @param modifiedDate the modified date of this dispatch trigger
	 */
	@Override
	public void setModifiedDate(Date modifiedDate);

	/**
	 * Returns the active of this dispatch trigger.
	 *
	 * @return the active of this dispatch trigger
	 */
	public boolean getActive();

	/**
	 * Returns <code>true</code> if this dispatch trigger is active.
	 *
	 * @return <code>true</code> if this dispatch trigger is active; <code>false</code> otherwise
	 */
	public boolean isActive();

	/**
	 * Sets whether this dispatch trigger is active.
	 *
	 * @param active the active of this dispatch trigger
	 */
	public void setActive(boolean active);

	/**
	 * Returns the cron expression of this dispatch trigger.
	 *
	 * @return the cron expression of this dispatch trigger
	 */
	@AutoEscape
	public String getCronExpression();

	/**
	 * Sets the cron expression of this dispatch trigger.
	 *
	 * @param cronExpression the cron expression of this dispatch trigger
	 */
	public void setCronExpression(String cronExpression);

	/**
	 * Returns the end date of this dispatch trigger.
	 *
	 * @return the end date of this dispatch trigger
	 */
	public Date getEndDate();

	/**
	 * Sets the end date of this dispatch trigger.
	 *
	 * @param endDate the end date of this dispatch trigger
	 */
	public void setEndDate(Date endDate);

	/**
	 * Returns the name of this dispatch trigger.
	 *
	 * @return the name of this dispatch trigger
	 */
	@AutoEscape
	public String getName();

	/**
	 * Sets the name of this dispatch trigger.
	 *
	 * @param name the name of this dispatch trigger
	 */
	public void setName(String name);

	/**
	 * Returns the overlap allowed of this dispatch trigger.
	 *
	 * @return the overlap allowed of this dispatch trigger
	 */
	public boolean getOverlapAllowed();

	/**
	 * Returns <code>true</code> if this dispatch trigger is overlap allowed.
	 *
	 * @return <code>true</code> if this dispatch trigger is overlap allowed; <code>false</code> otherwise
	 */
	public boolean isOverlapAllowed();

	/**
	 * Sets whether this dispatch trigger is overlap allowed.
	 *
	 * @param overlapAllowed the overlap allowed of this dispatch trigger
	 */
	public void setOverlapAllowed(boolean overlapAllowed);

	/**
	 * Returns the single node execution of this dispatch trigger.
	 *
	 * @return the single node execution of this dispatch trigger
	 */
	public boolean getSingleNodeExecution();

	/**
	 * Returns <code>true</code> if this dispatch trigger is single node execution.
	 *
	 * @return <code>true</code> if this dispatch trigger is single node execution; <code>false</code> otherwise
	 */
	public boolean isSingleNodeExecution();

	/**
	 * Sets whether this dispatch trigger is single node execution.
	 *
	 * @param singleNodeExecution the single node execution of this dispatch trigger
	 */
	public void setSingleNodeExecution(boolean singleNodeExecution);

	/**
	 * Returns the start date of this dispatch trigger.
	 *
	 * @return the start date of this dispatch trigger
	 */
	public Date getStartDate();

	/**
	 * Sets the start date of this dispatch trigger.
	 *
	 * @param startDate the start date of this dispatch trigger
	 */
	public void setStartDate(Date startDate);

	/**
	 * Returns the system of this dispatch trigger.
	 *
	 * @return the system of this dispatch trigger
	 */
	public boolean getSystem();

	/**
	 * Returns <code>true</code> if this dispatch trigger is system.
	 *
	 * @return <code>true</code> if this dispatch trigger is system; <code>false</code> otherwise
	 */
	public boolean isSystem();

	/**
	 * Sets whether this dispatch trigger is system.
	 *
	 * @param system the system of this dispatch trigger
	 */
	public void setSystem(boolean system);

	/**
	 * Returns the task executor type of this dispatch trigger.
	 *
	 * @return the task executor type of this dispatch trigger
	 */
	@AutoEscape
	public String getTaskExecutorType();

	/**
	 * Sets the task executor type of this dispatch trigger.
	 *
	 * @param taskExecutorType the task executor type of this dispatch trigger
	 */
	public void setTaskExecutorType(String taskExecutorType);

	/**
	 * Returns the task settings of this dispatch trigger.
	 *
	 * @return the task settings of this dispatch trigger
	 */
	@AutoEscape
	public String getTaskSettings();

	/**
	 * Sets the task settings of this dispatch trigger.
	 *
	 * @param taskSettings the task settings of this dispatch trigger
	 */
	public void setTaskSettings(String taskSettings);

}