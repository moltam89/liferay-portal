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

export default function () {
	const toggleFunctional = document.querySelector(
		'.toggle-switch-check-functional'
	);
	const togglePerformance = document.querySelector(
		'.toggle-switch-check-performance'
	);
	const togglePersonalization = document.querySelector(
		'.toggle-switch-check-personalization'
	);

	if (getCookie('COOKIE_CONSENT_TYPE_FUNCTIONAL') === 'true') {
		toggleFunctional.checked = true;
	}

	if (getCookie('COOKIE_CONSENT_TYPE_PERFORMANCE') === 'true') {
		togglePerformance.checked = true;
	}

	if (getCookie('COOKIE_CONSENT_TYPE_PERSONALIZATION') === 'true') {
		togglePersonalization.checked = true;
	}

	toggleFunctional.addEventListener(
		'click',
		function handleToggleFunctional() {
			if (toggleFunctional.checked) {
				setCookie('COOKIE_CONSENT_TYPE_FUNCTIONAL', 'true');
			}
			else {
				setCookie('COOKIE_CONSENT_TYPE_FUNCTIONAL', 'false');
			}
		}
	);

	togglePerformance.addEventListener(
		'click',
		function handleTogglePerformance() {
			if (togglePerformance.checked) {
				setCookie('COOKIE_CONSENT_TYPE_PERFORMANCE', 'true');
			}
			else {
				setCookie('COOKIE_CONSENT_TYPE_PERFORMANCE', 'false');
			}
		}
	);

	togglePersonalization.addEventListener(
		'click',
		function handleTogglePersonalization() {
			if (togglePersonalization.checked) {
				setCookie('COOKIE_CONSENT_TYPE_PERSONALIZATION', 'true');
			}
			else {
				setCookie('COOKIE_CONSENT_TYPE_PERSONALIZATION', 'false');
			}
		}
	);

	toggleFunctional.removeAttribute('disabled');
	togglePerformance.removeAttribute('disabled');
	togglePersonalization.removeAttribute('disabled');
}

function getCookie(name) {
	const cookieName = name + '=';
	const cookieSet = document.cookie.split(';');

	for (let i = 0; i < cookieSet.length; i++) {
		let c = cookieSet[i];

		while (c.charAt(0) === ' ') {
			c = c.substring(1, c.length);
		}

		if (c.indexOf(cookieName) === 0) {
			return c.substring(cookieName.length, c.length);
		}
	}

	return null;
}

function setCookie(name, value, days = 180) {
	const date = new Date();

	date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);

	const expires = '; expires=' + date.toUTCString();

	document.cookie = name + '=' + (value || '') + expires + '; path=/';
}
