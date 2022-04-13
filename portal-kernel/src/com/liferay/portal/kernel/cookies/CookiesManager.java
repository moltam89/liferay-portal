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

package com.liferay.portal.kernel.cookies;

import com.liferay.portal.kernel.exception.CookieNotSupportedException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tamas Molnar
 */
@ProviderType
public interface CookiesManager {

	public void addCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Cookie cookie);

	public void addCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Cookie cookie, boolean secure);

	public void addSupportCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public void deleteCookies(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String domain,
		String... cookieNames);

	public String getCookie(HttpServletRequest httpServletRequest, String name);

	public String getCookie(
		HttpServletRequest httpServletRequest, String name,
		boolean toUpperCase);

	public String getDomain(HttpServletRequest httpServletRequest);

	public String getDomain(String host);

	public boolean hasSessionId(HttpServletRequest httpServletRequest);

	public boolean isEncodedCookie(String name);

	public void validateSupportCookie(HttpServletRequest httpServletRequest)
		throws CookieNotSupportedException;

}