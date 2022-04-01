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

package com.liferay.cookies.consent.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.CookieKeys;

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Tamas Molnar
 */
@RunWith(Arquillian.class)
public class CookiesConsentImplTest {

	@Before
	public void setUp() throws Exception {
		_mockHttpServletRequest = new MockHttpServletRequest();
		_mockHttpServletResponse = new MockHttpServletResponse();
	}

	@Test
	public void testCookiesConsent() throws Exception {
		testCookiesConsentType(CookieKeys.TYPE_FUNCTIONAL);
		testCookiesConsentType(CookieKeys.TYPE_NECESSARY);
		testCookiesConsentType(CookieKeys.TYPE_PERFORMANCE);
		testCookiesConsentType(CookieKeys.TYPE_PERSONALIZATION);
	}

	protected void setCookiesConsent(String type, boolean accepted) {
		Cookie cookiesConsent = new Cookie(
			CookieKeys.COOKIES_CONSENT + CharPool.UNDERLINE + type,
			String.valueOf(accepted));

		CookieKeys.addCookie(
			_mockHttpServletRequest, _mockHttpServletResponse, cookiesConsent);
	}

	protected void testCookiesConsentType(String type) {
		setCookiesConsent(type, false);

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookieKeys.addCookie(
			_mockHttpServletRequest, _mockHttpServletResponse, cookie, type);

		if (type.equals(CookieKeys.TYPE_NECESSARY)) {
			Assert.assertNotNull(
				CookieKeys.getCookie(
					_mockHttpServletRequest, cookie.getName()));
		}
		else {
			Assert.assertNull(
				CookieKeys.getCookie(
					_mockHttpServletRequest, cookie.getName()));
		}

		setCookiesConsent(type, true);

		CookieKeys.addCookie(
			_mockHttpServletRequest, _mockHttpServletResponse, cookie, type);

		Assert.assertNotNull(
			CookieKeys.getCookie(_mockHttpServletRequest, cookie.getName()));
	}

	private MockHttpServletRequest _mockHttpServletRequest;
	private MockHttpServletResponse _mockHttpServletResponse;

}