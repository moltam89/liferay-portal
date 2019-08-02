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

package com.liferay.segments.service.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.service.http.TunnelUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.segments.service.SegmentsExperimentServiceUtil;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the HTTP utility for the
 * <code>SegmentsExperimentServiceUtil</code> service
 * utility. The
 * static methods of this class calls the same methods of the service utility.
 * However, the signatures are different because it requires an additional
 * <code>HttpPrincipal</code> parameter.
 *
 * <p>
 * The benefits of using the HTTP utility is that it is fast and allows for
 * tunneling without the cost of serializing to text. The drawback is that it
 * only works with Java.
 * </p>
 *
 * <p>
 * Set the property <b>tunnel.servlet.hosts.allowed</b> in portal.properties to
 * configure security.
 * </p>
 *
 * <p>
 * The HTTP utility is only generated for remote services.
 * </p>
 *
 * @author Eduardo Garcia
 * @see SegmentsExperimentServiceSoap
 * @generated
 */
@ProviderType
public class SegmentsExperimentServiceHttp {

	public static com.liferay.segments.model.SegmentsExperiment
			addSegmentsExperiment(
				HttpPrincipal httpPrincipal, long segmentsExperienceId,
				long classNameId, long classPK, String name, String description,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsExperimentServiceUtil.class, "addSegmentsExperiment",
				_addSegmentsExperimentParameterTypes0);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsExperienceId, classNameId, classPK, name,
				description, serviceContext);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception e) {
				if (e instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						e;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					e);
			}

			return (com.liferay.segments.model.SegmentsExperiment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException se) {
			_log.error(se, se);

			throw se;
		}
	}

	public static java.util.List<com.liferay.segments.model.SegmentsExperiment>
			getSegmentsExperienceSegmentsExperiments(
				HttpPrincipal httpPrincipal, long segmentsExperienceId,
				long classNameId, long classPK, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsExperimentServiceUtil.class,
				"getSegmentsExperienceSegmentsExperiments",
				_getSegmentsExperienceSegmentsExperimentsParameterTypes1);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsExperienceId, classNameId, classPK, status);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception e) {
				if (e instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						e;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					e);
			}

			return (java.util.List
				<com.liferay.segments.model.SegmentsExperiment>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException se) {
			_log.error(se, se);

			throw se;
		}
	}

	public static com.liferay.segments.model.SegmentsExperiment
			getSegmentsExperiment(
				HttpPrincipal httpPrincipal, long segmentsExperimentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsExperimentServiceUtil.class, "getSegmentsExperiment",
				_getSegmentsExperimentParameterTypes2);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsExperimentId);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception e) {
				if (e instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						e;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					e);
			}

			return (com.liferay.segments.model.SegmentsExperiment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException se) {
			_log.error(se, se);

			throw se;
		}
	}

	public static java.util.List<com.liferay.segments.model.SegmentsExperiment>
		getSegmentsExperiments(
			HttpPrincipal httpPrincipal, long groupId, long classNameId,
			long classPK) {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsExperimentServiceUtil.class, "getSegmentsExperiments",
				_getSegmentsExperimentsParameterTypes3);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, groupId, classNameId, classPK);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception e) {
				throw new com.liferay.portal.kernel.exception.SystemException(
					e);
			}

			return (java.util.List
				<com.liferay.segments.model.SegmentsExperiment>)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException se) {
			_log.error(se, se);

			throw se;
		}
	}

	public static com.liferay.segments.model.SegmentsExperiment
			updateSegmentsExperiment(
				HttpPrincipal httpPrincipal, long segmentsExperimentId,
				String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsExperimentServiceUtil.class, "updateSegmentsExperiment",
				_updateSegmentsExperimentParameterTypes4);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsExperimentId, name, description);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception e) {
				if (e instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						e;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					e);
			}

			return (com.liferay.segments.model.SegmentsExperiment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException se) {
			_log.error(se, se);

			throw se;
		}
	}

	public static com.liferay.segments.model.SegmentsExperiment
			updateSegmentsExperiment(
				HttpPrincipal httpPrincipal, String segmentsExperimentKey,
				int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		try {
			MethodKey methodKey = new MethodKey(
				SegmentsExperimentServiceUtil.class, "updateSegmentsExperiment",
				_updateSegmentsExperimentParameterTypes5);

			MethodHandler methodHandler = new MethodHandler(
				methodKey, segmentsExperimentKey, status);

			Object returnObj = null;

			try {
				returnObj = TunnelUtil.invoke(httpPrincipal, methodHandler);
			}
			catch (Exception e) {
				if (e instanceof
						com.liferay.portal.kernel.exception.PortalException) {

					throw (com.liferay.portal.kernel.exception.PortalException)
						e;
				}

				throw new com.liferay.portal.kernel.exception.SystemException(
					e);
			}

			return (com.liferay.segments.model.SegmentsExperiment)returnObj;
		}
		catch (com.liferay.portal.kernel.exception.SystemException se) {
			_log.error(se, se);

			throw se;
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		SegmentsExperimentServiceHttp.class);

	private static final Class<?>[] _addSegmentsExperimentParameterTypes0 =
		new Class[] {
			long.class, long.class, long.class, String.class, String.class,
			com.liferay.portal.kernel.service.ServiceContext.class
		};
	private static final Class<?>[]
		_getSegmentsExperienceSegmentsExperimentsParameterTypes1 = new Class[] {
			long.class, long.class, long.class, int.class
		};
	private static final Class<?>[] _getSegmentsExperimentParameterTypes2 =
		new Class[] {long.class};
	private static final Class<?>[] _getSegmentsExperimentsParameterTypes3 =
		new Class[] {long.class, long.class, long.class};
	private static final Class<?>[] _updateSegmentsExperimentParameterTypes4 =
		new Class[] {long.class, String.class, String.class};
	private static final Class<?>[] _updateSegmentsExperimentParameterTypes5 =
		new Class[] {String.class, int.class};

}