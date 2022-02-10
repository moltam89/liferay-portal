package com.liferay.exportimport.internal.staging;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.liferay.cookie.util.Cookie;


@Component(immediate = true, service = Cookie.class)
public class CookieImpl implements Cookie {

	@Override
	public void addCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		javax.servlet.http.Cookie cookie) {

		httpServletResponse.addCookie(cookie);
	}
}
