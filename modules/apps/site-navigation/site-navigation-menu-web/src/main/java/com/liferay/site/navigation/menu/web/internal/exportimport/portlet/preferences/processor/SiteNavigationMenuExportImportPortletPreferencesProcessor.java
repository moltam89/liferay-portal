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

package com.liferay.site.navigation.menu.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.navigation.menu.web.internal.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
	service = ExportImportPortletPreferencesProcessor.class
)
public class SiteNavigationMenuExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.toList(new Capability[] {_exportCapability});
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.toList(new Capability[] {_importCapability});
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		String portletId = portletDataContext.getPortletId();

		String navmenuid = portletPreferences.getValue(
			"siteNavigationMenuId", null);
		long scopeGroupId = portletDataContext.getScopeGroupId();

		try {
			if (navmenuid != null) {
				SiteNavigationMenu navmenu =
					_siteNavigationMenuLocalService.getSiteNavigationMenu(
						GetterUtil.getLong(navmenuid));

				if (navmenu != null) {
					String navmenuuuid = navmenu.getUuid();

					SiteNavigationMenu navmenuToExport =
						_siteNavigationMenuLocalService.
							getSiteNavigationMenuByUuidAndGroupId(
								navmenuuuid, scopeGroupId);

					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletId, navmenuToExport);
				}

				return portletPreferences;
			}
		}
		catch (PortalException pe) {
			PortletDataException pde = new PortletDataException(pe);

			throw pde;
		}

		return null;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		String navmenuid = portletPreferences.getValue(
			"siteNavigationMenuId", null);

		try {
			if (navmenuid != null) {
				Map<Long, Long> navMenuIds =
					(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
						SiteNavigationMenu.class);

				Long properId = navMenuIds.get(navmenuid);

				portletPreferences.setValue(
					"siteNavigationMenuId", String.valueOf(properId));

				return portletPreferences;
			}
		}
		catch (ReadOnlyException roe) {
			PortletDataException pde = new PortletDataException(roe);

			throw pde;
		}

		return null;
	}

	@Reference(target = "(name=PortletDisplayTemplateExporter)")
	private Capability _exportCapability;

	@Reference(target = "(name=PortletDisplayTemplateImporter)")
	private Capability _importCapability;

	@Reference(unbind = "-")
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}