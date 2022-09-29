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

package com.liferay.exportimport.internal.background.task;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SystemProperties;

import java.io.File;

/**
 * @author Tamas Molnar
 */
public class LayoutSetPrototypeExportBackgroundTaskExecutor
	extends BaseExportImportBackgroundTaskExecutor {

	public LayoutSetPrototypeExportBackgroundTaskExecutor() {
		setBackgroundTaskStatusMessageTranslator(
			new LayoutExportImportBackgroundTaskStatusMessageTranslator());

		// Isolation level guarantees this will be serial in a group

		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_GROUP);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		LayoutSetPrototypeExportBackgroundTaskExecutor
			layoutSetPrototypeExportBackgroundTaskExecutor =
				new LayoutSetPrototypeExportBackgroundTaskExecutor();

		layoutSetPrototypeExportBackgroundTaskExecutor.
			setBackgroundTaskStatusMessageTranslator(
				getBackgroundTaskStatusMessageTranslator());
		layoutSetPrototypeExportBackgroundTaskExecutor.setIsolationLevel(
			getIsolationLevel());

		return layoutSetPrototypeExportBackgroundTaskExecutor;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		ExportImportConfiguration exportImportConfiguration =
			getExportImportConfiguration(backgroundTask);

		long layoutSetPrototypeId = MapUtil.getLong(
			exportImportConfiguration.getSettingsMap(), "layoutSetPrototypeId");

		LayoutSetPrototype layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.getLayoutSetPrototype(
				layoutSetPrototypeId);

		String cacheFileName = StringBundler.concat(
			_TEMP_DIR, layoutSetPrototype.getUuid(), ".v",
			layoutSetPrototype.getMvccVersion(), ".lar");

		File cacheFile = new File(cacheFileName);

		if (cacheFile.exists()) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Using cached layout set prototype LAR file " +
						cacheFile.getAbsolutePath());
			}

			return BackgroundTaskResult.SUCCESS;
		}

		File larFile = ExportImportLocalServiceUtil.exportLayoutsAsFile(
			exportImportConfiguration);

		try {
			FileUtil.copyFile(larFile, cacheFile);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Copied ", larFile.getAbsolutePath(), " to ",
						cacheFile.getAbsolutePath()));
			}
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to copy file ", larFile.getAbsolutePath(), " to ",
					cacheFile.getAbsolutePath()),
				exception);
		}

		return BackgroundTaskResult.SUCCESS;
	}

	private static final String _TEMP_DIR =
		SystemProperties.get(SystemProperties.TMP_DIR) +
			"/liferay/layout_set_prototype/";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypeExportBackgroundTaskExecutor.class);

}