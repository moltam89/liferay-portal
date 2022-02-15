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

package com.liferay.cookie.consent.internal;

import com.liferay.portal.kernel.util.CookieConsent;
import com.liferay.portal.kernel.util.CookieKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tamas Molnar
 */
@Component(immediate = true, service = CookieConsent.class)
public class CookieConsentImpl implements CookieConsent {

	@Override
	public boolean isCookieAllowed(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Cookie cookie, String type) {

		if (type.equals(CookieKeys.TYPE_NECESSARY)) {
			return true;
		}

		String cookieConsentString = CookieKeys.getCookie(
			httpServletRequest, CookieKeys.COOKIE_CONSENT + "_" + type);

		if (Validator.isNull(cookieConsentString)) {

			// ToDo
			// If consent Cookie is missing, get Site Config using the Cookie's
			// domain

			return true;
		}

		return GetterUtil.getBoolean(cookieConsentString);
	}

}