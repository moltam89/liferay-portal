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

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.petra.string.StringBundler;

/**
 * @author Leonardo Barros
 */
public class ConcatFunction
	implements DDMExpressionFunction.Function1<Object[], String> {

	public static final String NAME = "concat";

	@Override
	public String apply(Object[] values) {
		StringBundler sb = new StringBundler(values.length);

		for (Object value : values) {
			if (value == null) {
				continue;
			}

			sb.append(value.toString());
		}

		return sb.toString();
	}

	@Override
	public String getName() {
		return NAME;
	}

}