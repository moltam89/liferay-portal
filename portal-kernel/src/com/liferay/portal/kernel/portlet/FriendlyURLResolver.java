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

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eduardo García
 * @author Marco Leo
 */
@ProviderType
public interface FriendlyURLResolver {

	public String getActualURL(
			long companyId, long groupId, boolean privateLayout,
			String mainPath, String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException;

	public LayoutFriendlyURLComposite getLayoutFriendlyURLComposite(
			long companyId, long groupId, boolean privateLayout,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException;

	public default LayoutFriendlyURLSeparatorComposite
			getLayoutFriendlyURLSeparatorComposite(
				long companyId, long groupId, boolean privateLayout,
				String friendlyURL, Map<String, String[]> params,
				Map<String, Object> requestContext)
		throws PortalException {

		LayoutFriendlyURLComposite layoutFriendlyURLComposite =
			getLayoutFriendlyURLComposite(
				companyId, groupId, privateLayout, friendlyURL, params,
				requestContext);

		if (layoutFriendlyURLComposite == null) {
			return null;
		}

		return new LayoutFriendlyURLSeparatorComposite(
			layoutFriendlyURLComposite, getURLSeparator());
	}

	public String getURLSeparator();

}