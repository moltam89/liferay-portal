/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.LayoutSetPrototype;

/**
 * @author Akos Thurzo
 */
public interface LayoutLocalServiceHelper {

	public String getFriendlyURL(
			long groupId, boolean privateLayout, long layoutId, String name,
			String friendlyURL)
		throws PortalException, SystemException;

	public String getFriendlyURL(String friendlyURL);

	public int getNextPriority(
			long groupId, boolean privateLayout, long parentLayoutId,
			String sourcePrototypeLayoutUuid, int defaultPriority)
		throws SystemException;

	public long getParentLayoutId(
			long groupId, boolean privateLayout, long parentLayoutId)
		throws SystemException;

	public boolean hasLayoutSetPrototypeLayout(
			LayoutSetPrototype layoutSetPrototype, String layoutUuid)
		throws PortalException, SystemException;

	public void validate(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId, String name, String type, boolean hidden,
			String friendlyURL)
		throws PortalException, SystemException;

	public void validateFirstLayout(String type) throws PortalException;

	public void validateFriendlyURL(
			long groupId, boolean privateLayout, long layoutId,
			String friendlyURL)
		throws PortalException, SystemException;

	public void validateName(String name) throws PortalException;

	public void validateName(String name, String languageId)
		throws PortalException;

	public void validateParentLayoutId(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId)
		throws PortalException, SystemException;

}