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

package com.liferay.users.admin.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import java.util.Locale;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tamas Molnar
 */
@Component(immediate = true, service = CTDisplayRenderer.class)
public class PhoneCTDisplayRenderer extends BaseCTDisplayRenderer<Phone> {

	@Override
	public String getEditURL(HttpServletRequest httpServletRequest, Phone phone)
		throws PortalException {

		Organization organization = _organizationLocalService.getOrganization(
			phone.getClassPK());

		Group group = _groupLocalService.getGroup(organization.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, UsersAdminPortletKeys.USERS_ADMIN, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/common/edit_phone_number.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"className", phone.getClassName()
		).setParameter(
			"classPK", phone.getClassPK()
		).setParameter(
			"primaryKey", phone.getPhoneId()
		).buildString();
	}

	@Override
	public Class<Phone> getModelClass() {
		return Phone.class;
	}

	@Override
	public String getTitle(Locale locale, Phone phone) {
		return phone.getNumber();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Phone> displayBuilder)
		throws PortalException {

		Phone phone = displayBuilder.getModel();

		displayBuilder.display(
			"name", phone.getNumber()
		).display(
			"created-by",
			() -> {
				String userName = phone.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", phone.getCreateDate()
		).display(
			"last-modified", phone.getModifiedDate()
		).display(
			"primary", phone.isPrimary()
		).display(
			"type",
			() -> {
				ListType listType = phone.getType();

				if (listType != null) {
					return _language.get(
						displayBuilder.getLocale(), listType.getName());
				}

				return null;
			}
		).display(
			"number", phone.getNumber()
		).display(
			"extension", phone.getExtension()
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}