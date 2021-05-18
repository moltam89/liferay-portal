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

package com.liferay.exportimport.data.handler.base;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;

/**
 * @author Mauro Mariuzzo
 */
public abstract class BasePortletDataHandler extends
	com.liferay.exportimport.kernel.lar.BasePortletDataHandler {

	protected void doLoggedImportData(
			PortletDataContext portletDataContext, String title,
			List<Element> elements)
		throws Exception {

		doLoggedImportData(portletDataContext, title, elements, 0);
	}

	protected void doLoggedImportData(
			PortletDataContext portletDataContext, String title,
			List<Element> elements, int maxDelta)
		throws Exception {

		int total = elements.size();
		int delta = total / 50;

		if (delta < 10) {
			delta = 10;
		}

		if (maxDelta > 0 && delta > maxDelta) {
			delta = maxDelta;
		}

		getLog().info("Import " + title);

		int index = 0;

		for (Element element : elements) {
			index++;

			if (index % delta == 0) {
				getLog().info("	" + index + "/" + total);
			}

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, element);
		}

		getLog().info("Imported " + title + " total=" + total);
	}

	protected abstract Log getLog();

}
