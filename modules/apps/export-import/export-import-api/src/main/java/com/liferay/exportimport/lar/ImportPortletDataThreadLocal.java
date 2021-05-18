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

package com.liferay.exportimport.lar;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mauro Mariuzzo
 */
public class ImportPortletDataThreadLocal {

	public static ImportPortletDataThreadLocal getPortletDataThreadLocal() {
		ImportPortletDataThreadLocal importPortletDataThreadLocal =
			_importPortletDataThreadLocal.get();

		if (importPortletDataThreadLocal == null) {
			importPortletDataThreadLocal = new ImportPortletDataThreadLocal();

			_importPortletDataThreadLocal.set(importPortletDataThreadLocal);
		}

		return importPortletDataThreadLocal;
	}

	public static String buildkey(Element rootElement, String name) {
		String selfPath = rootElement.attributeValue("self-path");

		if (Validator.isNull(selfPath)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(selfPath);
		sb.append("#");
		sb.append(name);

		return sb.toString();
	}

	public static String buildkey(
		Element rootElement, String name, String attribute, String value) {

		String base = buildkey(rootElement, name);

		if (Validator.isNull(base)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(base);
		sb.append("#");
		sb.append(name);
		sb.append("#");
		sb.append(attribute);
		sb.append("#");
		sb.append(value);

		return sb.toString();
	}

	public void clearCache() {
		_elementMap.clear();
	}

	public boolean contains(String key) {
		return _elementMap.containsKey(key);
	}

	public Element getElement(String key) {
		return _elementMap.get(key);
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public void save(String key, Element value) {
		if (_enabled) {
			_elementMap.put(key, value);
		}
	}

	public void setEnabled(boolean enabled, boolean clear) {
		_enabled = enabled;

		if (clear) {
			clearCache();
		}
	}

	private Map<String, Element> _elementMap = new HashMap<>();
	private boolean _enabled;

	private static final ThreadLocal<ImportPortletDataThreadLocal>
		_importPortletDataThreadLocal = new CentralizedThreadLocal<>(
			ImportPortletDataThreadLocal.class +
				"._importPortletDataThreadLocal");

}

