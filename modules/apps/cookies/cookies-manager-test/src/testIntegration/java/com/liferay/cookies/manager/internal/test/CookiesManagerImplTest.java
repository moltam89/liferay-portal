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

package com.liferay.cookies.manager.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.cookies.util.CookiesManagerUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

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
public class CookiesManagerImplTest {

	@Before
	public void setUp() throws Exception {
		_mockHttpServletRequest = new MockHttpServletRequest();
		_mockHttpServletResponse = new MockHttpServletResponse();
	}

	@Test
	public void testCookiesConsent() throws Exception {
		testCookiesConsentType(CookiesConstants.CONSENT_TYPE_FUNCTIONAL);
		testCookiesConsentType(CookiesConstants.CONSENT_TYPE_NECESSARY);
		testCookiesConsentType(CookiesConstants.CONSENT_TYPE_PERFORMANCE);
		testCookiesConsentType(CookiesConstants.CONSENT_TYPE_PERSONALIZATION);
	}

	protected void setCookiesConsent(int type, boolean accepted) {
		if (type == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			return;
		}

		String consentCookieName = StringPool.BLANK;

		if (type == CookiesConstants.CONSENT_TYPE_FUNCTIONAL) {
			consentCookieName = CookiesConstants.KEY_CONSENT_TYPE_FUNCTIONAL;
		}
		else if (type == CookiesConstants.CONSENT_TYPE_PERFORMANCE) {
			consentCookieName = CookiesConstants.KEY_CONSENT_TYPE_PERFORMANCE;
		}
		else if (type == CookiesConstants.CONSENT_TYPE_PERSONALIZATION) {
			consentCookieName =
				CookiesConstants.KEY_CONSENT_TYPE_PERSONALIZATION;
		}

		Cookie cookiesConsent = new Cookie(
			consentCookieName, String.valueOf(accepted));

		CookiesManagerUtil.addCookie(
			_mockHttpServletRequest, _mockHttpServletResponse, cookiesConsent,
			CookiesConstants.CONSENT_TYPE_NECESSARY);
	}

	protected void testCookiesConsentType(int type) {
		setCookiesConsent(type, false);

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			_mockHttpServletRequest, _mockHttpServletResponse, cookie, type);

		if (type == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			Assert.assertNotNull(
				CookiesManagerUtil.getCookie(
					_mockHttpServletRequest, cookie.getName()));
		}
		else {
			Assert.assertNull(
				CookiesManagerUtil.getCookie(
					_mockHttpServletRequest, cookie.getName()));
		}

		setCookiesConsent(type, true);

		CookiesManagerUtil.addCookie(
			_mockHttpServletRequest, _mockHttpServletResponse, cookie, type);

		Assert.assertNotNull(
			CookiesManagerUtil.getCookie(
				_mockHttpServletRequest, cookie.getName()));
	}

	private MockHttpServletRequest _mockHttpServletRequest;
	private MockHttpServletResponse _mockHttpServletResponse;

}