/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.model.LayoutSetPrototype;

/**
 * @author Akos Thurzo
 */
public class LayoutLocalServiceHelperUtil {

	public static String getBeanIdentifier() {
		return getLayoutLocalServiceHelper().getBeanIdentifier();
	}

	public static String getFriendlyURL(
			long groupId, boolean privateLayout, long layoutId, String name,
			String friendlyURL)
		throws PortalException, SystemException {

		return getLayoutLocalServiceHelper().getFriendlyURL(
			groupId, privateLayout, layoutId, name, friendlyURL);
	}

	public static String getFriendlyURL(String friendlyURL) {
		return getLayoutLocalServiceHelper().getFriendlyURL(friendlyURL);
	}

	public static LayoutLocalServiceHelper getLayoutLocalServiceHelper() {
		PortalRuntimePermission.checkGetBeanProperty(
			LayoutLocalServiceHelperUtil.class);

		return _layoutLocalServiceHelper;
	}

	public static int getNextPriority(
			long groupId, boolean privateLayout, long parentLayoutId,
			String sourcePrototypeLayoutUuid, int defaultPriority)
		throws SystemException {

		return getLayoutLocalServiceHelper().getNextPriority(
			groupId, privateLayout, parentLayoutId, sourcePrototypeLayoutUuid,
			defaultPriority);
	}

	public static long getParentLayoutId(
			long groupId, boolean privateLayout, long parentLayoutId)
		throws SystemException {

		return getLayoutLocalServiceHelper().getParentLayoutId(
			groupId, privateLayout, parentLayoutId);
	}

	public static boolean hasLayoutSetPrototypeLayout(
			LayoutSetPrototype layoutSetPrototype, String layoutUuid)
		throws PortalException, SystemException {

		return getLayoutLocalServiceHelper().hasLayoutSetPrototypeLayout(
			layoutSetPrototype, layoutUuid);
	}

	public static void setBeanIdentifier(String beanIdentifier) {
		getLayoutLocalServiceHelper().setBeanIdentifier(beanIdentifier);
	}

	public static void validate(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId, String name, String type, boolean hidden,
			String friendlyURL)
		throws PortalException, SystemException {

		getLayoutLocalServiceHelper().validate(
			groupId, privateLayout, layoutId, parentLayoutId, name, type,
			hidden, friendlyURL);
	}

	public static void validateFirstLayout(String type) throws PortalException {
		getLayoutLocalServiceHelper().validateFirstLayout(type);
	}

	public static void validateFriendlyURL(
			long groupId, boolean privateLayout, long layoutId,
			String friendlyURL)
		throws PortalException, SystemException {

		getLayoutLocalServiceHelper().validateFriendlyURL(
			groupId, privateLayout, layoutId, friendlyURL);
	}

	public static void validateName(String name) throws PortalException {
		getLayoutLocalServiceHelper().validateName(name);
	}

	public static void validateName(String name, String languageId)
		throws PortalException {

		getLayoutLocalServiceHelper().validateName(name, languageId);
	}

	public static void validateParentLayoutId(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId)
		throws PortalException, SystemException {

		getLayoutLocalServiceHelper().validateParentLayoutId(
			groupId, privateLayout, layoutId, parentLayoutId);
	}

	public void setLayoutLocalServiceHelper(
		LayoutLocalServiceHelper layoutLocalServiceHelper) {

		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_layoutLocalServiceHelper = layoutLocalServiceHelper;
	}

	private static LayoutLocalServiceHelper _layoutLocalServiceHelper;

}