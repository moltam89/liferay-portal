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

package com.liferay.portal.upgrade.test.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

/**
 * @author Adam Brandizzi
 * @author Alberto Chaparro
 */
public class UpgradeTestUtil {

	public static UpgradeProcess getUpgradeStep(
		UpgradeStepRegistrator upgradeStepRegistrator,
		String upgradeStepClassName) {

		SearchRegistry searchRegistry = new SearchRegistry(
			upgradeStepClassName);

		upgradeStepRegistrator.register(searchRegistry);

		return searchRegistry.getUpgradeStep();
	}

	private static class SearchRegistry
		implements UpgradeStepRegistrator.Registry {

		public SearchRegistry(String upgradeStepClassName) {
			_upgradeStepClassName = upgradeStepClassName;
		}

		public UpgradeProcess getUpgradeStep() {
			return _upgradeStep;
		}

		@Override
		public void register(
			String fromSchemaVersionString, String toSchemaVersionString,
			UpgradeStep... upgradeSteps) {

			for (UpgradeStep upgradeStep : upgradeSteps) {
				Class<?> clazz = upgradeStep.getClass();

				String className = clazz.getName();

				if (className.contains(_upgradeStepClassName)) {
					_upgradeStep = (UpgradeProcess)upgradeStep;
				}
			}
		}

		private UpgradeProcess _upgradeStep;
		private final String _upgradeStepClassName;

	}

}