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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tamas Molnar
 */
public class CookiesConsentUtil {

	public static boolean hasCookiesConsentType(
		HttpServletRequest httpServletRequest, String type) {

		try {
			if (_cookiesConsent == null) {
				return true;
			}

			return _cookiesConsent.hasCookiesConsentType(
				httpServletRequest, type);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesConsentUtil.class);

	private static volatile CookiesConsent _cookiesConsent =
		ServiceProxyFactory.newServiceTrackedInstance(
			CookiesConsent.class, CookiesConsentUtil.class, "_cookiesConsent",
			false, true);

}