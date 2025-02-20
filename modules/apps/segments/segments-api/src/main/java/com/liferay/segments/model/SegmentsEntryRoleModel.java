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

package com.liferay.segments.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The base model interface for the SegmentsEntryRole service. Represents a row in the &quot;SegmentsEntryRole&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation <code>com.liferay.segments.model.impl.SegmentsEntryRoleModelImpl</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in <code>com.liferay.segments.model.impl.SegmentsEntryRoleImpl</code>.
 * </p>
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryRole
 * @generated
 */
@ProviderType
public interface SegmentsEntryRoleModel
	extends AuditedModel, BaseModel<SegmentsEntryRole>,
			CTModel<SegmentsEntryRole>, MVCCModel, ShardedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a segments entry role model instance should use the {@link SegmentsEntryRole} interface instead.
	 */

	/**
	 * Returns the primary key of this segments entry role.
	 *
	 * @return the primary key of this segments entry role
	 */
	@Override
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this segments entry role.
	 *
	 * @param primaryKey the primary key of this segments entry role
	 */
	@Override
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the mvcc version of this segments entry role.
	 *
	 * @return the mvcc version of this segments entry role
	 */
	@Override
	public long getMvccVersion();

	/**
	 * Sets the mvcc version of this segments entry role.
	 *
	 * @param mvccVersion the mvcc version of this segments entry role
	 */
	@Override
	public void setMvccVersion(long mvccVersion);

	/**
	 * Returns the ct collection ID of this segments entry role.
	 *
	 * @return the ct collection ID of this segments entry role
	 */
	@Override
	public long getCtCollectionId();

	/**
	 * Sets the ct collection ID of this segments entry role.
	 *
	 * @param ctCollectionId the ct collection ID of this segments entry role
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId);

	/**
	 * Returns the segments entry role ID of this segments entry role.
	 *
	 * @return the segments entry role ID of this segments entry role
	 */
	public long getSegmentsEntryRoleId();

	/**
	 * Sets the segments entry role ID of this segments entry role.
	 *
	 * @param segmentsEntryRoleId the segments entry role ID of this segments entry role
	 */
	public void setSegmentsEntryRoleId(long segmentsEntryRoleId);

	/**
	 * Returns the company ID of this segments entry role.
	 *
	 * @return the company ID of this segments entry role
	 */
	@Override
	public long getCompanyId();

	/**
	 * Sets the company ID of this segments entry role.
	 *
	 * @param companyId the company ID of this segments entry role
	 */
	@Override
	public void setCompanyId(long companyId);

	/**
	 * Returns the user ID of this segments entry role.
	 *
	 * @return the user ID of this segments entry role
	 */
	@Override
	public long getUserId();

	/**
	 * Sets the user ID of this segments entry role.
	 *
	 * @param userId the user ID of this segments entry role
	 */
	@Override
	public void setUserId(long userId);

	/**
	 * Returns the user uuid of this segments entry role.
	 *
	 * @return the user uuid of this segments entry role
	 */
	@Override
	public String getUserUuid();

	/**
	 * Sets the user uuid of this segments entry role.
	 *
	 * @param userUuid the user uuid of this segments entry role
	 */
	@Override
	public void setUserUuid(String userUuid);

	/**
	 * Returns the user name of this segments entry role.
	 *
	 * @return the user name of this segments entry role
	 */
	@AutoEscape
	@Override
	public String getUserName();

	/**
	 * Sets the user name of this segments entry role.
	 *
	 * @param userName the user name of this segments entry role
	 */
	@Override
	public void setUserName(String userName);

	/**
	 * Returns the create date of this segments entry role.
	 *
	 * @return the create date of this segments entry role
	 */
	@Override
	public Date getCreateDate();

	/**
	 * Sets the create date of this segments entry role.
	 *
	 * @param createDate the create date of this segments entry role
	 */
	@Override
	public void setCreateDate(Date createDate);

	/**
	 * Returns the modified date of this segments entry role.
	 *
	 * @return the modified date of this segments entry role
	 */
	@Override
	public Date getModifiedDate();

	/**
	 * Sets the modified date of this segments entry role.
	 *
	 * @param modifiedDate the modified date of this segments entry role
	 */
	@Override
	public void setModifiedDate(Date modifiedDate);

	/**
	 * Returns the segments entry ID of this segments entry role.
	 *
	 * @return the segments entry ID of this segments entry role
	 */
	public long getSegmentsEntryId();

	/**
	 * Sets the segments entry ID of this segments entry role.
	 *
	 * @param segmentsEntryId the segments entry ID of this segments entry role
	 */
	public void setSegmentsEntryId(long segmentsEntryId);

	/**
	 * Returns the role ID of this segments entry role.
	 *
	 * @return the role ID of this segments entry role
	 */
	public long getRoleId();

	/**
	 * Sets the role ID of this segments entry role.
	 *
	 * @param roleId the role ID of this segments entry role
	 */
	public void setRoleId(long roleId);

	@Override
	public SegmentsEntryRole cloneWithOriginalValues();

	public default String toXmlString() {
		return null;
	}

}