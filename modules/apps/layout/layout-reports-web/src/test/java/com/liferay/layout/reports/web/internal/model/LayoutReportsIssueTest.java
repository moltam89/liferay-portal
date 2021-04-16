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

package com.liferay.layout.reports.web.internal.model;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class LayoutReportsIssueTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testEquals() {
		LayoutReportsIssue layoutReportsIssue1 = new LayoutReportsIssue(
			"key", 100);
		LayoutReportsIssue layoutReportsIssue2 = new LayoutReportsIssue(
			"key", 100);

		Assert.assertTrue(layoutReportsIssue1.equals(layoutReportsIssue2));
	}

	@Test
	public void testNewLayoutReportsIssue() {
		LayoutReportsIssue layoutReportsIssue = new LayoutReportsIssue(
			"key", 100);

		Assert.assertEquals("key", layoutReportsIssue.getKey());
		Assert.assertEquals(100, layoutReportsIssue.getTotal());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewLayoutReportsIssueWithNullKey() {
		new LayoutReportsIssue(null, 100);
	}

	@Test
	public void testNotEqualsWithDifferentKey() {
		LayoutReportsIssue layoutReportsIssue1 = new LayoutReportsIssue(
			"key", 100);
		LayoutReportsIssue layoutReportsIssue2 = new LayoutReportsIssue(
			"key1", 100);

		Assert.assertFalse(layoutReportsIssue1.equals(layoutReportsIssue2));
	}

	@Test
	public void testNotEqualsWithDifferentTotal() {
		LayoutReportsIssue layoutReportsIssue1 = new LayoutReportsIssue(
			"key", 100);
		LayoutReportsIssue layoutReportsIssue2 = new LayoutReportsIssue(
			"key", 200);

		Assert.assertFalse(layoutReportsIssue1.equals(layoutReportsIssue2));
	}

	@Test
	public void testToJSONObject() {
		LayoutReportsIssue layoutReportsIssue = new LayoutReportsIssue(
			"key", 100);

		JSONObject jsonObject = layoutReportsIssue.toJSONObject(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE);

		Assert.assertEquals(
			layoutReportsIssue.getKey(), jsonObject.getString("key"));
		Assert.assertEquals(
			layoutReportsIssue.getTotal(), jsonObject.getLong("total"));
	}

	@Test
	public void testToString() {
		LayoutReportsIssue layoutReportsIssue = new LayoutReportsIssue(
			"key", 100);

		Assert.assertEquals(
			"{\"total\":\"100\",\"key\":\"key\"}",
			layoutReportsIssue.toString());
	}

}