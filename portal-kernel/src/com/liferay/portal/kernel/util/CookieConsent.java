package com.liferay.portal.kernel.util;

import javax.servlet.http.Cookie;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface CookieConsent {

	public boolean isCookieAllowed(Cookie cookie, String type);

}