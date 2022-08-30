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

package com.liferay.address.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Region;
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
public class AddressCTDisplayRenderer extends BaseCTDisplayRenderer<Address> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, Address address)
		throws PortalException {

		Organization organization = _organizationLocalService.getOrganization(
			address.getClassPK());

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
		).setMVCRenderCommandName(
			"/users_admin/edit_organization"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"organizationId", organization.getOrganizationId()
		).buildString();
	}

	@Override
	public Class<Address> getModelClass() {
		return Address.class;
	}

	@Override
	public String getTitle(Locale locale, Address address) {
		return address.getCity();
	}

	@Override
	protected void buildDisplay(DisplayBuilder<Address> displayBuilder)
		throws PortalException {

		Address address = displayBuilder.getModel();

		displayBuilder.display(
			"name",
			() -> {
				if (Validator.isNotNull(address.getName())) {
					return address.getName();
				}

				return address.getCity();
			}
		).display(
			"created-by",
			() -> {
				String userName = address.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", address.getCreateDate()
		).display(
			"last-modified", address.getModifiedDate()
		).display(
			"primary", address.isPrimary()
		).display(
			"type",
			() -> {
				ListType listType = address.getType();

				if (listType != null) {
					return _language.get(
						displayBuilder.getLocale(), listType.getName());
				}

				return null;
			}
		).display(
			"street1",
			() -> {
				String street1 = address.getStreet1();

				if (Validator.isNotNull(street1)) {
					return street1;
				}

				return null;
			}
		).display(
			"street2",
			() -> {
				String street2 = address.getStreet2();

				if (Validator.isNotNull(street2)) {
					return street2;
				}

				return null;
			}
		).display(
			"street3",
			() -> {
				String street3 = address.getStreet3();

				if (Validator.isNotNull(street3)) {
					return street3;
				}

				return null;
			}
		).display(
			"city", address.getCity()
		).display(
			"country",
			() -> {
				Country country = address.getCountry();

				if (country != null) {
					return country.getTitle(displayBuilder.getLocale());
				}

				return null;
			}
		).display(
			"region",
			() -> {
				Region region = address.getRegion();

				if (region != null) {
					return region.getTitle(
						_language.getLanguageId(displayBuilder.getLocale()));
				}

				return null;
			}
		).display(
			"postal-code", address.getZip()
		).display(
			"mailing", address.isMailing()
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