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

package com.liferay.segments.service;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the remote service utility for SegmentsExperiment. This utility wraps
 * <code>com.liferay.segments.service.impl.SegmentsExperimentServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Eduardo Garcia
 * @see SegmentsExperimentService
 * @generated
 */
@ProviderType
public class SegmentsExperimentServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsExperimentServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.segments.model.SegmentsExperiment
			addSegmentsExperiment(
				long segmentsExperienceId, long classNameId, long classPK,
				String name, String description,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addSegmentsExperiment(
			segmentsExperienceId, classNameId, classPK, name, description,
			serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static java.util.List<com.liferay.segments.model.SegmentsExperiment>
			getSegmentsExperienceSegmentsExperiments(
				long segmentsExperienceId, long classNameId, long classPK,
				int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getSegmentsExperienceSegmentsExperiments(
			segmentsExperienceId, classNameId, classPK, status);
	}

	public static com.liferay.segments.model.SegmentsExperiment
			getSegmentsExperiment(long segmentsExperimentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getSegmentsExperiment(segmentsExperimentId);
	}

	public static java.util.List<com.liferay.segments.model.SegmentsExperiment>
		getSegmentsExperiments(long groupId, long classNameId, long classPK) {

		return getService().getSegmentsExperiments(
			groupId, classNameId, classPK);
	}

	public static com.liferay.segments.model.SegmentsExperiment
			updateSegmentsExperiment(
				long segmentsExperimentId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateSegmentsExperiment(
			segmentsExperimentId, name, description);
	}

	public static com.liferay.segments.model.SegmentsExperiment
			updateSegmentsExperiment(String segmentsExperimentKey, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateSegmentsExperiment(
			segmentsExperimentKey, status);
	}

	public static SegmentsExperimentService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<SegmentsExperimentService, SegmentsExperimentService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			SegmentsExperimentService.class);

		ServiceTracker<SegmentsExperimentService, SegmentsExperimentService>
			serviceTracker =
				new ServiceTracker
					<SegmentsExperimentService, SegmentsExperimentService>(
						bundle.getBundleContext(),
						SegmentsExperimentService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}