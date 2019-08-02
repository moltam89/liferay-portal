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

package com.liferay.segments.asah.connector.internal.client.model.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.asah.connector.internal.client.model.Experiment;
import com.liferay.segments.asah.connector.internal.client.model.ExperimentStatus;
import com.liferay.segments.constants.SegmentsConstants;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;

/**
 * @author Sarai Díaz
 * @author David Arques
 */
public class ExperimentUtil {

	public static Experiment toExperiment(
			CompanyLocalService companyLocalService, String dataSourceId,
			GroupLocalService groupLocalService,
			LayoutLocalService layoutLocalService, Portal portal,
			SegmentsEntryLocalService segmentsEntryLocalService,
			SegmentsExperienceLocalService segmentsExperienceLocalService,
			SegmentsExperiment segmentsExperiment)
		throws PortalException {

		return toExperiment(
			dataSourceId,
			SegmentsConstants.getDefaultSegmentsEntryName(
				LocaleUtil.getDefault()),
			SegmentsConstants.getDefaultSegmentsExperienceName(
				LocaleUtil.getDefault()),
			layoutLocalService,
			_getLayoutFullURL(
				portal, companyLocalService, groupLocalService,
				layoutLocalService.getLayout(segmentsExperiment.getClassPK())),
			segmentsEntryLocalService, segmentsExperienceLocalService,
			segmentsExperiment);
	}

	protected static Experiment toExperiment(
			String dataSourceId, String defaultSegmentsEntryName,
			String defaultSegmentsExperienceName,
			LayoutLocalService layoutLocalService, String pageURL,
			SegmentsEntryLocalService segmentsEntryLocalService,
			SegmentsExperienceLocalService segmentsExperienceLocalService,
			SegmentsExperiment segmentsExperiment)
		throws PortalException {

		Experiment experiment = new Experiment();

		experiment.setCreateDate(segmentsExperiment.getCreateDate());
		experiment.setModifiedDate(segmentsExperiment.getModifiedDate());
		experiment.setName(segmentsExperiment.getName());
		experiment.setDataSourceId(dataSourceId);
		experiment.setDescription(segmentsExperiment.getDescription());
		experiment.setId(segmentsExperiment.getSegmentsExperimentKey());

		if (segmentsExperiment.getSegmentsExperienceId() ==
				SegmentsConstants.SEGMENTS_EXPERIENCE_ID_DEFAULT) {

			experiment.setDXPExperienceId(
				SegmentsConstants.SEGMENTS_EXPERIENCE_KEY_DEFAULT);
			experiment.setDXPExperienceName(defaultSegmentsExperienceName);
			experiment.setDXPSegmentId(
				SegmentsConstants.SEGMENTS_ENTRY_KEY_DEFAULT);
			experiment.setDXPSegmentName(defaultSegmentsEntryName);
		}
		else {
			SegmentsExperience segmentsExperience =
				segmentsExperienceLocalService.getSegmentsExperience(
					segmentsExperiment.getSegmentsExperienceId());

			experiment.setDXPExperienceId(
				segmentsExperience.getSegmentsExperienceKey());
			experiment.setDXPExperienceName(
				segmentsExperience.getName(LocaleUtil.getDefault()));

			SegmentsEntry segmentsEntry =
				segmentsEntryLocalService.getSegmentsEntry(
					segmentsExperience.getSegmentsEntryId());

			experiment.setDXPSegmentId(segmentsEntry.getSegmentsEntryKey());
			experiment.setDXPSegmentName(
				segmentsEntry.getName(LocaleUtil.getDefault()));
		}

		experiment.setExperimentStatus(
			_toExperimentStatus(segmentsExperiment.getStatus()));

		Layout layout = layoutLocalService.getLayout(
			segmentsExperiment.getClassPK());

		experiment.setDxpLayoutId(layout.getUuid());
		experiment.setPageRelativePath(
			layout.getFriendlyURL(LocaleUtil.getDefault()));
		experiment.setPageTitle(layout.getTitle(LocaleUtil.getDefault()));

		experiment.setPageURL(pageURL);

		return experiment;
	}

	private static String _getLayoutFullURL(
			Portal portal, CompanyLocalService companyLocalService,
			GroupLocalService groupLocalService, Layout layout)
		throws PortalException {

		StringBundler sb = new StringBundler(4);

		Group group = groupLocalService.getGroup(layout.getGroupId());

		if (group.isLayout()) {
			long parentGroupId = group.getParentGroupId();

			if (parentGroupId > 0) {
				group = groupLocalService.getGroup(parentGroupId);
			}
		}

		String virtualHostname = null;

		LayoutSet layoutSet = layout.getLayoutSet();

		if (Validator.isNotNull(layoutSet.getVirtualHostname())) {
			virtualHostname = layoutSet.getVirtualHostname();
		}
		else {
			Company company = companyLocalService.getCompany(
				layout.getCompanyId());

			virtualHostname = company.getVirtualHostname();
		}

		boolean secure = StringUtil.equalsIgnoreCase(
			Http.HTTPS, PropsValues.WEB_SERVER_PROTOCOL);

		String portalURL = portal.getPortalURL(
			virtualHostname, portal.getPortalServerPort(secure), secure);

		sb.append(portalURL);

		sb.append(portal.getPathFriendlyURLPublic());

		sb.append(group.getFriendlyURL());
		sb.append(layout.getFriendlyURL());

		return sb.toString();
	}

	private static ExperimentStatus _toExperimentStatus(int status) {
		if (status == SegmentsExperimentConstants.STATUS_DRAFT) {
			return ExperimentStatus.DRAFT;
		}

		return ExperimentStatus.DRAFT;
	}

}