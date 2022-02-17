package com.liferay.portal.kernel.util;

import javax.servlet.http.Cookie;

public class CookieConsentUtil {

	public static boolean isCookieAllowed(Cookie cookie, String type) {
		return _cookieConsent.isCookieAllowed(cookie, type);
	}

	private static volatile CookieConsent _cookieConsent =
			ServiceProxyFactory.newServiceTrackedInstance(
			CookieConsent.class, CookieUtil.class, "_cookieConsent", false);
}
