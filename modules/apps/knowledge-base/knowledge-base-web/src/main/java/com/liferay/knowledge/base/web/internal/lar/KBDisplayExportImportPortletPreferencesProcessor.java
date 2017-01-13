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

package com.liferay.knowledge.base.web.internal.lar;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.portlet.preferences.processor.capability.ReferencedStagedModelImporterCapability;
import com.liferay.knowledge.base.constants.KBArticleConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sorin Pop
 */
@Component(
	immediate = true,
	property = {"javax.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_DISPLAY},
	service = ExportImportPortletPreferencesProcessor.class
)
public class KBDisplayExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return null;
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.toList(
			new Capability[] {_referencedStagedModelImporterCapability});
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		long resourcePrimKey = GetterUtil.getLong(
			portletPreferences.getValue("resourcePrimKey", StringPool.BLANK));

		long resourceClassNameId = GetterUtil.getLong(
			portletPreferences.getValue(
				"resourceClassNameId", StringPool.BLANK));

		if (resourceClassNameId ==
				PortalUtil.getClassNameId(KBFolderConstants.getClassName())) {

			try {
				portletPreferences.setValue(
					"resourceClassNameId",
					PortalUtil.getClassName(resourceClassNameId));
			}
			catch (ReadOnlyException roe) {
				StringBundler sb = new StringBundler(9);

				sb.append("Unable to save converted portlet preference ");
				sb.append("resourceClassNameId (from ");
				sb.append(resourceClassNameId);
				sb.append(" to ");
				sb.append(PortalUtil.getClassName(resourceClassNameId));
				sb.append(") while exporting KB Display portlet. ");
				sb.append(" (portletId=");
				sb.append(portletDataContext.getPortletId());
				sb.append(")");

				throw new PortletDataException(sb.toString(), roe);
			}

			if (resourcePrimKey != KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				KBFolder rootFolder = _kbFolderLocalService.fetchKBFolder(
					resourcePrimKey);

				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, portletDataContext.getPortletId(),
					rootFolder);
			}
		}

		if (resourceClassNameId ==
				PortalUtil.getClassNameId(KBArticleConstants.getClassName())) {

			try {
				portletPreferences.setValue(
					"resourceClassNameId",
					PortalUtil.getClassName(resourceClassNameId));
			}
			catch (ReadOnlyException roe) {
				StringBundler sb = new StringBundler(9);

				sb.append("Unable to save converted portlet preference ");
				sb.append("resourceClassNameId (from ");
				sb.append(resourceClassNameId);
				sb.append(" to ");
				sb.append(PortalUtil.getClassName(resourceClassNameId));
				sb.append(") while exporting KB Display portlet. ");
				sb.append("(portletId=");
				sb.append(portletDataContext.getPortletId());
				sb.append(")");

				throw new PortletDataException(sb.toString(), roe);
			}

			if (resourcePrimKey !=
					KBArticleConstants.DEFAULT_PARENT_RESOURCE_PRIM_KEY) {

				List<KBArticle> kbArticles =
					_kbArticleLocalService.
						getKBArticleAndAllDescendantKBArticles(
							resourcePrimKey, WorkflowConstants.STATUS_APPROVED,
							null);

				for (KBArticle kbArticle : kbArticles) {
					StagedModelDataHandlerUtil.exportReferenceStagedModel(
						portletDataContext, portletDataContext.getPortletId(),
						kbArticle);
				}
			}
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		String resourceClassName = GetterUtil.getString(
			portletPreferences.getValue(
				"resourceClassNameId", StringPool.BLANK));

		try {
			portletPreferences.setValue(
				"resourceClassNameId",
				String.valueOf(PortalUtil.getClassNameId(resourceClassName)));
		}
		catch (ReadOnlyException roe) {
			StringBundler sb = new StringBundler(9);

			sb.append("Unable to save reconverted portlet preference ");
			sb.append("resourceClassNameId (from ");
			sb.append(resourceClassName);
			sb.append(" to ");
			sb.append(PortalUtil.getClassNameId(resourceClassName));
			sb.append(") while importing KB Display portlet. ");
			sb.append("(portletId=");
			sb.append(portletDataContext.getPortletId());
			sb.append(")");

			throw new PortletDataException(sb.toString(), roe);
		}

		long resourcePrimKey = GetterUtil.getLong(
			portletPreferences.getValue("resourcePrimKey", StringPool.BLANK));

		if (PortalUtil.getClassNameId(resourceClassName) ==
				PortalUtil.getClassNameId(KBFolderConstants.getClassName())) {

			Map<Long, Long> kbFolderIds =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					KBFolder.class);

			resourcePrimKey = MapUtil.getLong(
					kbFolderIds, resourcePrimKey, resourcePrimKey);

			try {
				portletPreferences.setValue(
					"resourcePrimKey", String.valueOf(resourcePrimKey));
			}
			catch (ReadOnlyException roe) {
				StringBundler sb = new StringBundler(8);

				sb.append("Unable to save converted portlet preference ");
				sb.append("resourcePrimKey=");
				sb.append(resourcePrimKey);
				sb.append(" (the root folder)  ");
				sb.append("while importing KB Display portlet. ");
				sb.append("(portletId=");
				sb.append(portletDataContext.getPortletId());
				sb.append(")");

				throw new PortletDataException(sb.toString(), roe);
			}
		}

		if (PortalUtil.getClassNameId(resourceClassName) ==
				PortalUtil.getClassNameId(KBArticleConstants.getClassName())) {

			Map<Long, Long> kbArticleResourcePrimKeys =
				(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
					KBArticle.class);

			resourcePrimKey = MapUtil.getLong(kbArticleResourcePrimKeys,
					resourcePrimKey, resourcePrimKey);

			try {
				portletPreferences.setValue(
					"resourcePrimKey", String.valueOf(resourcePrimKey));
			}
			catch (ReadOnlyException roe) {
				StringBundler sb = new StringBundler(8);

				sb.append("Unable to save converted portlet preference ");
				sb.append("resourcePrimKey=");
				sb.append(resourcePrimKey);
				sb.append(" (the root article)  ");
				sb.append("while importing KB Display portlet. ");
				sb.append("(portletId=");
				sb.append(portletDataContext.getPortletId());
				sb.append(")");

				throw new PortletDataException(sb.toString(), roe);
			}
		}

		return portletPreferences;
	}

	@Reference(unbind = "-")
	protected void seKBArticleLocalService(
		KBArticleLocalService kbArticleLocalService) {

		_kbArticleLocalService = kbArticleLocalService;
	}

	@Reference(unbind = "-")
	protected void seKBFolderLocalService(
		KBFolderLocalService kbFolderLocalService) {

		_kbFolderLocalService = kbFolderLocalService;
	}

	@Reference(unbind = "-")
	protected void setReferencedStagedModelImporterCapability(
		ReferencedStagedModelImporterCapability
			referencedStagedModelImporterCapability) {

		_referencedStagedModelImporterCapability =
			referencedStagedModelImporterCapability;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBDisplayExportImportPortletPreferencesProcessor.class);

	private KBArticleLocalService _kbArticleLocalService;
	private KBFolderLocalService _kbFolderLocalService;
	private ReferencedStagedModelImporterCapability
		_referencedStagedModelImporterCapability;

}