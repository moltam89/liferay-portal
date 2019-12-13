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

package com.liferay.staging.bar.web.internal.display.context;

import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.servlet.SessionErrors;

import java.net.ConnectException;

import javax.portlet.RenderRequest;

/**
 * @author Kimberly Chau
 */
public class StagingBarPortletDisplayContext {

	public StagingBarPortletDisplayContext(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public String getLiveGroupURL(Group group, Group liveGroup, Layout layout) {
		String remoteSiteURL = StringPool.BLANK;

		if ((liveGroup != null) && group.isStagedRemotely()) {
			try {
				remoteSiteURL = StagingUtil.getRemoteSiteURL(
					group, layout.isPrivateLayout());
			}
			catch (AuthException ae) {
				_log.error(ae.getMessage());

				SessionErrors.add(_renderRequest, AuthException.class);
			}
			catch (SystemException se) {
				Throwable cause = se.getCause();

				if (!(cause instanceof ConnectException)) {
					throw se;
				}

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to connect to remote live: " +
							cause.getMessage());
				}

				SessionErrors.add(_renderRequest, RemoteExportException.class);
			}
			catch (Exception e) {
				_log.error(e, e);

				SessionErrors.add(_renderRequest, Exception.class);
			}
		}

		return remoteSiteURL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingBarPortletDisplayContext.class);

	private final RenderRequest _renderRequest;

}