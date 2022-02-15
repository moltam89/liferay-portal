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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Tamas Molnar
 */
public class CookieConsentUtil {

	public static boolean isCookieAllowed(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Cookie cookie, String type) {

		return _cookieConsent.isCookieAllowed(
			httpServletRequest, httpServletResponse, cookie, type);
	}

	private static volatile CookieConsent _cookieConsent =
		ServiceProxyFactory.newServiceTrackedInstance(
			CookieConsent.class, CookieConsentUtil.class, "_cookieConsent",
			true);

}