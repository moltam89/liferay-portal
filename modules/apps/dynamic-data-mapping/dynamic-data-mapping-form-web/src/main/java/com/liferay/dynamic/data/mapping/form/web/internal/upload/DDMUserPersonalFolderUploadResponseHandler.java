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

package com.liferay.dynamic.data.mapping.form.web.internal.upload;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadResponseHandler;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = DDMUserPersonalFolderUploadResponseHandler.class)
public class DDMUserPersonalFolderUploadResponseHandler
	implements UploadResponseHandler {

	@Override
	public JSONObject onFailure(
			PortletRequest portletRequest, PortalException portalException)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug(portalException);
		}

		if (portalException instanceof
				ObjectEntryValuesException.InvalidFileExtension) {

			return JSONUtil.put(
				"error",
				() -> {
					ObjectFieldSetting objectFieldSetting =
						_objectFieldSettingLocalService.fetchObjectFieldSetting(
							ParamUtil.getLong(portletRequest, "objectFieldId"),
							"acceptedFileExtensions");

					return JSONUtil.put(
						"errorType",
						ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION
					).put(
						"message", objectFieldSetting.getValue()
					);
				}
			).put(
				"success", Boolean.FALSE
			);
		}

		return _itemSelectorUploadResponseHandler.onFailure(
			portletRequest, portalException);
	}

	@Override
	public JSONObject onSuccess(
			UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
		throws PortalException {

		JSONObject jsonObject = _itemSelectorUploadResponseHandler.onSuccess(
			uploadPortletRequest, fileEntry);

		JSONObject fileJSONObject = jsonObject.getJSONObject("file");

		fileJSONObject.put("url", _getURL(uploadPortletRequest, fileEntry));

		if (SessionMessages.contains(
				uploadPortletRequest,
				_portal.getPortletId(uploadPortletRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE)) {

			SessionMessages.clear(uploadPortletRequest);
		}

		return jsonObject;
	}

	private String _getURL(
		UploadPortletRequest uploadPortletRequest, FileEntry fileEntry) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return _dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getLatestFileVersion(), themeDisplay,
				StringPool.BLANK);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get URL for file entry " +
						fileEntry.getFileEntryId(),
					portalException);
			}
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMUserPersonalFolderUploadResponseHandler.class);

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private Portal _portal;

}