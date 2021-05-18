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

package com.liferay.exportimport.internal.lar;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mauro Mariuzzo
 * @author Tamas Molnar
 */
public class ImportDataElementCache {

	public static ImportDataElementCache getImportDataElementCache() {
		ImportDataElementCache importDataElementCache =
			_importDataElementCacheThreadLocal.get();

		if (importDataElementCache == null) {
			importDataElementCache = new ImportDataElementCache();

			_importDataElementCacheThreadLocal.set(importDataElementCache);
		}

		return importDataElementCache;
	}

	public static String getKey(Element rootElement, String name) {
		if (rootElement == null) {
			return null;
		}

		String selfPath = rootElement.attributeValue("self-path");

		if (Validator.isNull(selfPath)) {
			return null;
		}

		return StringBundler.concat(selfPath, StringPool.POUND, name);
	}

	public static String getKey(
		Element rootElement, String name, String attribute, String value) {

		if (!Objects.equals(attribute, "path")) {
			return null;
		}

		String base = getKey(rootElement, name);

		if (Validator.isNull(base)) {
			return null;
		}

		return StringBundler.concat(
			base, StringPool.POUND, name, StringPool.POUND, attribute,
			StringPool.POUND, value);
	}

	public void clear() {
		_elementMap.clear();
	}

	public Element getElement(String key) {
		if (Validator.isNull(key)) {
			return null;
		}

		return _elementMap.get(key);
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public void put(String key, Element value) {
		if (Validator.isNotNull(key)) {
			_elementMap.put(key, value);
		}
	}

	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	private static final ThreadLocal<ImportDataElementCache>
		_importDataElementCacheThreadLocal = new CentralizedThreadLocal<>(
			ImportDataElementCache.class +
				"._importDataElementCacheThreadLocal");

	private final Map<String, Element> _elementMap = new HashMap<>();
	private boolean _enabled;

}