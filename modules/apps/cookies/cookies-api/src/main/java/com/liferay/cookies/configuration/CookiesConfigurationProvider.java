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

package com.liferay.cookies.configuration;

import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.portal.kernel.theme.ThemeDisplay;

/**
 * @author Daniel Sanz
 */
public interface CookiesConfigurationProvider {

	public CookiesBannerConfiguration getCookiesBannerConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception;

	public CookiesConsentConfiguration getCookiesConsentConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception;

	public CookiesPreferenceHandlingConfiguration
			getCookiesPreferenceHandlingConfiguration(ThemeDisplay themeDisplay)
		throws Exception;

}