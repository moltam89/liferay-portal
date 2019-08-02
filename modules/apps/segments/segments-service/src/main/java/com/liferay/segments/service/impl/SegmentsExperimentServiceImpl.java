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

package com.liferay.segments.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.segments.constants.SegmentsActionKeys;
import com.liferay.segments.constants.SegmentsConstants;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.base.SegmentsExperimentServiceBaseImpl;

import java.util.List;

/**
 * @author Eduardo García
 */
public class SegmentsExperimentServiceImpl
	extends SegmentsExperimentServiceBaseImpl {

	@Override
	public SegmentsExperiment addSegmentsExperiment(
			long segmentsExperienceId, long classNameId, long classPK,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			SegmentsActionKeys.MANAGE_SEGMENTS_ENTRIES);

		return segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperienceId, classNameId, classPK, name, description,
			serviceContext);
	}

	@Override
	public List<SegmentsExperiment> getSegmentsExperienceSegmentsExperiments(
			long segmentsExperienceId, long classNameId, long classPK,
			int status)
		throws PortalException {

		LayoutPermissionUtil.check(
			getPermissionChecker(), classPK, ActionKeys.UPDATE);

		return segmentsExperimentLocalService.
			getSegmentsExperienceSegmentsExperiments(
				segmentsExperienceId, classNameId, classPK, status);
	}

	@Override
	public SegmentsExperiment getSegmentsExperiment(long segmentsExperimentId)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentId);

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(), segmentsExperiment, ActionKeys.VIEW);

		return segmentsExperiment;
	}

	@Override
	public List<SegmentsExperiment> getSegmentsExperiments(
		long groupId, long classNameId, long classPK) {

		return segmentsExperimentPersistence.filterFindByG_C_C(
			groupId, classNameId, _getPublishedLayoutClassPK(classPK));
	}

	@Override
	public SegmentsExperiment updateSegmentsExperiment(
			long segmentsExperimentId, String name, String description)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentId),
			ActionKeys.UPDATE);

		return segmentsExperimentLocalService.updateSegmentsExperiment(
			segmentsExperimentId, name, description);
	}

	@Override
	public SegmentsExperiment updateSegmentsExperiment(
			String segmentsExperimentKey, int status)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentKey),
			ActionKeys.UPDATE);

		return segmentsExperimentLocalService.updateSegmentsExperiment(
			segmentsExperimentKey, status);
	}

	private long _getPublishedLayoutClassPK(long classPK) {
		Layout layout = layoutLocalService.fetchLayout(classPK);

		if ((layout != null) &&
			(layout.getClassNameId() == classNameLocalService.getClassNameId(
				Layout.class)) &&
			(layout.getClassPK() != 0)) {

			return layout.getClassPK();
		}

		return classPK;
	}

	private static volatile PortletResourcePermission
		_portletResourcePermission =
			PortletResourcePermissionFactory.getInstance(
				SegmentsExperimentServiceImpl.class,
				"_portletResourcePermission", SegmentsConstants.RESOURCE_NAME);
	private static volatile ModelResourcePermission<SegmentsExperiment>
		_segmentsExperimentResourcePermission =
			ModelResourcePermissionFactory.getInstance(
				SegmentsExperimentServiceImpl.class,
				"_segmentsExperimentResourcePermission",
				SegmentsExperiment.class);

}