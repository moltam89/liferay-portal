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

package com.liferay.cookie.impl;

import com.liferay.portal.kernel.util.CookieConsent;

import javax.servlet.http.Cookie;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tamas Molnar
 */
@Component(immediate = true, service = CookieConsent.class)
public class CookieConsentImpl implements CookieConsent {

	@Override
	public boolean isCookieAllowed(Cookie cookie, String type) {

		// TODO
		// Get consent Cookie and check if actual type was allowed

		// If consent Cookie is missing, get Site Config using the Cookie domain

		return true;
	}

}