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

package com.liferay.content.dashboard.item.action;

import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;

import java.util.List;

/**
 * @author Cristina González
 */
public interface ContentDashboardItemActionProviderRegistry {

	public ContentDashboardItemActionProvider
		getContentDashboardItemActionProvider(
			String className, ContentDashboardItemAction.Type type);

	public List<ContentDashboardItemActionProvider>
		getContentDashboardItemActionProviders(
			String className, ContentDashboardItemAction.Type... types);

}