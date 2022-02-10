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

package com.liferay.cookie.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.util.ServiceProxyFactory;

/**
 * @author Tamas Molnar
 */
public class CookieUtil {

	public static void addCookie(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			javax.servlet.http.Cookie cookie) {

		_cookie.addCookie(httpServletRequest, httpServletResponse, cookie);
	}
	
	private static volatile Cookie _cookie =
		ServiceProxyFactory.newServiceTrackedInstance(
			Cookie.class, CookieUtil.class,
			"_cookie", true);

}
