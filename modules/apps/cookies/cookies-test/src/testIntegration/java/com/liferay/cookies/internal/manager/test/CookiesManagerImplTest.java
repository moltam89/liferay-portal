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

package com.liferay.cookies.internal.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
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

	protected void setCookiesConsent(int consentType, boolean accepted) {
		if (consentType == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			return;
		}

		String consentCookieName = StringPool.BLANK;

		if (consentType == CookiesConstants.CONSENT_TYPE_FUNCTIONAL) {
			consentCookieName = CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL;
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERFORMANCE) {
			consentCookieName = CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE;
		}
		else if (consentType == CookiesConstants.CONSENT_TYPE_PERSONALIZATION) {
			consentCookieName =
				CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION;
		}

		Cookie cookiesConsent = new Cookie(
			consentCookieName, String.valueOf(accepted));

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookiesConsent,
			_mockHttpServletRequest, _mockHttpServletResponse);
	}

	protected void testCookiesConsentType(int consentType) {
		setCookiesConsent(consentType, false);

		Cookie cookie = new Cookie(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		CookiesManagerUtil.addCookie(
			consentType, cookie, _mockHttpServletRequest,
			_mockHttpServletResponse);

		if (consentType == CookiesConstants.CONSENT_TYPE_NECESSARY) {
			Assert.assertNotNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));
		}
		else {
			Assert.assertNull(
				CookiesManagerUtil.getCookieValue(
					cookie.getName(), _mockHttpServletRequest));
		}

		setCookiesConsent(consentType, true);

		CookiesManagerUtil.addCookie(
			consentType, cookie, _mockHttpServletRequest,
			_mockHttpServletResponse);

		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				cookie.getName(), _mockHttpServletRequest));
	}

	private MockHttpServletRequest _mockHttpServletRequest;
	private MockHttpServletResponse _mockHttpServletResponse;

}