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

package com.liferay.bookmarks.internal.security.permission.resource;

import com.liferay.bookmarks.constants.BookmarksConstants;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
import com.liferay.portal.util.PropsValues;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.bookmarks.model.BookmarksFolder",
	service = ModelResourcePermission.class
)
public class BookmarksFolderModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<BookmarksFolder> {

	@Override
	protected ModelResourcePermission<BookmarksFolder>
		doGetModelResourcePermission() {

		return ModelResourcePermissionFactory.create(
			BookmarksFolder.class, BookmarksFolder::getFolderId,
			_bookmarksFolderLocalService::getFolder, _portletResourcePermission,
			(modelResourcePermission, consumer) -> {
				consumer.accept(
					new StagedModelPermissionLogic<>(
						_stagingPermission, BookmarksPortletKeys.BOOKMARKS,
						BookmarksFolder::getFolderId));

				if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
					consumer.accept(
						new DynamicInheritancePermissionLogic<>(
							modelResourcePermission, _getFetchParentFunction(),
							true));
				}
			},
			actionId -> {
				if (ActionKeys.ADD_FOLDER.equals(actionId)) {
					return ActionKeys.ADD_SUBFOLDER;
				}

				return actionId;
			});
	}

	private UnsafeFunction<BookmarksFolder, BookmarksFolder, PortalException>
		_getFetchParentFunction() {

		return folder -> {
			long folderId = folder.getParentFolderId();

			if (BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID == folderId) {
				return null;
			}

			if (folder.isInTrash()) {
				return _bookmarksFolderLocalService.fetchBookmarksFolder(
					folderId);
			}

			return _bookmarksFolderLocalService.getFolder(folderId);
		};
	}

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

	@Reference(
		target = "(resource.name=" + BookmarksConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private StagingPermission _stagingPermission;

}