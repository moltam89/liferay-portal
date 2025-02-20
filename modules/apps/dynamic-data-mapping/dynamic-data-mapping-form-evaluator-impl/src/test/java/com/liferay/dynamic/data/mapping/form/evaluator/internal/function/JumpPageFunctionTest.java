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

import com.liferay.dynamic.data.mapping.expression.ExecuteActionRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Inácio Nery
 * @author Leonardo Barros
 */
public class JumpPageFunctionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testExecuteAction() {
		DefaultDDMExpressionActionHandler spyDefaultDDMExpressionActionHandler =
			Mockito.spy(new DefaultDDMExpressionActionHandler());

		JumpPageFunction jumpPageFunction = new JumpPageFunction();

		jumpPageFunction.setDDMExpressionActionHandler(
			spyDefaultDDMExpressionActionHandler);

		Boolean result = jumpPageFunction.apply(1, 3);

		ArgumentCaptor<ExecuteActionRequest> argumentCaptor =
			ArgumentCaptor.forClass(ExecuteActionRequest.class);

		Mockito.verify(
			spyDefaultDDMExpressionActionHandler, Mockito.times(1)
		).executeAction(
			argumentCaptor.capture()
		);

		ExecuteActionRequest executeActionRequest = argumentCaptor.getValue();

		Assert.assertEquals("jumpPage", executeActionRequest.getAction());
		Assert.assertEquals(
			1, (Object)executeActionRequest.getParameter("from"));
		Assert.assertEquals(3, (Object)executeActionRequest.getParameter("to"));

		Assert.assertTrue(result);
	}

	@Test
	public void testNullActionHandler() {
		JumpPageFunction jumpPageFunction = new JumpPageFunction();

		Boolean result = jumpPageFunction.apply(1, 3);

		Assert.assertFalse(result);
	}

}