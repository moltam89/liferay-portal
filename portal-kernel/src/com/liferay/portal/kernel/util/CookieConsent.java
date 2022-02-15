package com.liferay.portal.kernel.util;

import javax.servlet.http.Cookie;

public interface CookieConsent {

	public boolean isCookieAllowed(Cookie cookie, String type);

}