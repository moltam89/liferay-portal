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

package com.liferay.portal.kernel.cookies.util;

import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.exception.CookieNotSupportedException;
import com.liferay.portal.kernel.util.ServiceProxyFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Tamas Molnar
 */
public class CookiesManagerUtil {

	public static void addCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Cookie cookie) {

		_cookiesManager.addCookie(
			httpServletRequest, httpServletResponse, cookie);
	}

	public static void addCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Cookie cookie,
		boolean secure) {

		_cookiesManager.addCookie(
			httpServletRequest, httpServletResponse, cookie, secure);
	}

	public static void addSupportCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		_cookiesManager.addSupportCookie(
			httpServletRequest, httpServletResponse);
	}

	public static void deleteCookies(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String domain,
		String... cookieNames) {

		_cookiesManager.deleteCookies(
			httpServletRequest, httpServletResponse, domain, cookieNames);
	}

	public static String getCookie(
		HttpServletRequest httpServletRequest, String name) {

		return _cookiesManager.getCookie(httpServletRequest, name);
	}

	public static String getCookie(
		HttpServletRequest httpServletRequest, String name,
		boolean toUpperCase) {

		return _cookiesManager.getCookie(httpServletRequest, name, toUpperCase);
	}

	public static String getDomain(HttpServletRequest httpServletRequest) {
		return _cookiesManager.getDomain(httpServletRequest);
	}

	public static String getDomain(String host) {
		return _cookiesManager.getDomain(host);
	}

	public static boolean hasSessionId(HttpServletRequest httpServletRequest) {
		return _cookiesManager.hasSessionId(httpServletRequest);
	}

	public static boolean isEncodedCookie(String name) {
		return _cookiesManager.isEncodedCookie(name);
	}

	public static void validateSupportCookie(
			HttpServletRequest httpServletRequest)
		throws CookieNotSupportedException {

		_cookiesManager.validateSupportCookie(httpServletRequest);
	}

	private static volatile CookiesManager _cookiesManager =
		ServiceProxyFactory.newServiceTrackedInstance(
			CookiesManager.class, CookiesManagerUtil.class, "_cookiesManager",
			false);

}