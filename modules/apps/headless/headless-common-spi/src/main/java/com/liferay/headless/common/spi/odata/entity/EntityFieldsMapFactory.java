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

package com.liferay.headless.common.spi.odata.entity;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.IdEntityField;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Javier de Arcos
 */
public class EntityFieldsMapFactory {

	public static Map<String, EntityField> create(EntityField... entityFields) {
		Map<String, EntityField> entityFieldsMap = new HashMap<>();

		for (EntityField entityField : entityFields) {
			entityFieldsMap.put(entityField.getName(), entityField);
		}

		entityFieldsMap.put(
			"id",
			new IdEntityField(
				"id", locale -> Field.ENTRY_CLASS_PK, String::valueOf));

		return entityFieldsMap;
	}

}