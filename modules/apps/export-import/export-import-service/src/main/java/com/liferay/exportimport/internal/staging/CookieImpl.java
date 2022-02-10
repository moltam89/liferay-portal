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

package com.liferay.exportimport.internal.staging;

import com.liferay.cookie.util.Cookie;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.staging.configuration.StagingConfiguration;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tamas Molnar
 */
@Component(
	configurationPid = "com.liferay.staging.configuration.StagingConfiguration",
	immediate = true, service = Cookie.class
)
public class CookieImpl implements Cookie {

	@Override
	public void addCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		javax.servlet.http.Cookie cookie) {

		httpServletResponse.addCookie(cookie);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_stagingConfiguration = ConfigurableUtil.createConfigurable(
			StagingConfiguration.class, properties);
	}

	private volatile StagingConfiguration _stagingConfiguration;

}