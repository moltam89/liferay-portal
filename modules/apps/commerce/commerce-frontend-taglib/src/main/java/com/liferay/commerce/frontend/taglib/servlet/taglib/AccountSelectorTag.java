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

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.frontend.taglib.internal.model.CurrentCommerceAccountModel;
import com.liferay.commerce.frontend.taglib.internal.model.CurrentCommerceOrderModel;
import com.liferay.commerce.frontend.taglib.internal.model.WorkflowStatusModel;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.util.IncludeTag;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author Fabio Diego Mastrorilli
 */
public class AccountSelectorTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			_commerceChannelId = commerceContext.getCommerceChannelId();

			if (_commerceChannelId == 0) {
				_accountEntryAllowedTypes = new String[0];
				_addCommerceOrderURL = StringPool.BLANK;
				_editOrderURL = StringPool.BLANK;
				_setCurrentAccountURL = StringPool.BLANK;

				return super.doStartTag();
			}

			_accountEntryAllowedTypes =
				commerceContext.getAccountEntryAllowedTypes();

			_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			_addCommerceOrderURL = _getAddCommerceOrderURL(
				_themeDisplay, httpServletRequest);

			_commerceAccount = commerceContext.getCommerceAccount();
			_commerceOrder = commerceContext.getCommerceOrder();
			_editOrderURL = _getEditOrderURL(_themeDisplay);
			_setCurrentAccountURL =
				PortalUtil.getPortalURL(httpServletRequest) +
					PortalUtil.getPathContext() +
						"/o/commerce-ui/set-current-account";

			if (Validator.isNull(_spritemap)) {
				_spritemap = _themeDisplay.getPathThemeSpritemap();
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			_accountEntryAllowedTypes = null;
			_addCommerceOrderURL = null;
			_commerceAccount = null;
			_commerceChannelId = 0;
			_commerceOrder = null;
			_editOrderURL = null;
			_setCurrentAccountURL = null;
			_spritemap = null;
			_themeDisplay = null;
		}

		return super.doStartTag();
	}

	public String getSpritemap() {
		return _spritemap;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		_commerceOrderTypeLocalService =
			ServletContextUtil.getCommerceOrderTypeLocalService();

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setSpritemap(String spritemap) {
		_spritemap = spritemap;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_accountEntryAllowedTypes = null;
		_addCommerceOrderURL = null;
		_commerceAccount = null;
		_commerceChannelId = 0;
		_commerceOrder = null;
		_commerceOrderTypeLocalService = null;
		_editOrderURL = null;
		_setCurrentAccountURL = null;
		_spritemap = null;
		_themeDisplay = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:accountEntryAllowedTypes",
			_accountEntryAllowedTypes);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:commerceChannelId",
			_commerceChannelId);

		if (_commerceAccount != null) {
			String thumbnailUrl = null;

			if (_commerceAccount.getLogoId() == 0) {
				thumbnailUrl =
					_themeDisplay.getPathImage() +
						"/organization_logo?img_id=0";
			}
			else {
				thumbnailUrl = StringBundler.concat(
					_themeDisplay.getPathImage(), "/organization_logo?img_id=",
					_commerceAccount.getLogoId(), "&t=",
					WebServerServletTokenUtil.getToken(
						_commerceAccount.getLogoId()));
			}

			CurrentCommerceAccountModel currentCommerceAccountModel =
				new CurrentCommerceAccountModel(
					_commerceAccount.getCommerceAccountId(), thumbnailUrl,
					_commerceAccount.getName());

			httpServletRequest.setAttribute(
				"liferay-commerce:account-selector:currentCommerceAccount",
				currentCommerceAccountModel);
		}

		if (_commerceOrder != null) {
			String workflowStatusInfoLabel = WorkflowConstants.getStatusLabel(
				_commerceOrder.getStatus());

			WorkflowStatusModel workflowStatusModel = new WorkflowStatusModel(
				_commerceOrder.getStatus(), workflowStatusInfoLabel,
				LanguageUtil.get(
					_themeDisplay.getLocale(), workflowStatusInfoLabel));

			CurrentCommerceOrderModel currentCommerceOrderModel =
				new CurrentCommerceOrderModel(
					_commerceOrder.getCommerceOrderId(), workflowStatusModel);

			httpServletRequest.setAttribute(
				"liferay-commerce:account-selector:currentCommerceOrder",
				currentCommerceOrderModel);
		}

		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:createNewOrderURL",
			_addCommerceOrderURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:selectOrderURL", _editOrderURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:setCurrentAccountURL",
			_setCurrentAccountURL);
		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:spritemap", _spritemap);
	}

	private String _getAddCommerceOrderURL(
			ThemeDisplay themeDisplay, HttpServletRequest httpServletRequest)
		throws PortalException {

		int commerceOrderTypesCount =
			_commerceOrderTypeLocalService.getCommerceOrderTypesCount(
				themeDisplay.getCompanyId(), true);

		if (commerceOrderTypesCount > 1) {
			httpServletRequest.setAttribute(
				"liferay-commerce:account-selector:showOrderTypeModal",
				Boolean.TRUE);

			return PortletURLBuilder.create(
				_getPortletURL(
					themeDisplay.getRequest(),
					CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT)
			).setMVCRenderCommandName(
				"/commerce_order_content/view_commerce_order_order_type_modal"
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString();
		}

		httpServletRequest.setAttribute(
			"liferay-commerce:account-selector:showOrderTypeModal",
			Boolean.FALSE);

		long plid = PortalUtil.getPlidFromPortletId(
			themeDisplay.getScopeGroupId(),
			CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

		if (plid > 0) {
			return PortletURLBuilder.create(
				_getPortletURL(
					themeDisplay.getRequest(),
					CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT)
			).setActionName(
				"/commerce_open_order_content/edit_commerce_order"
			).setCMD(
				Constants.ADD
			).buildString();
		}

		return StringPool.BLANK;
	}

	private String _getEditOrderURL(ThemeDisplay themeDisplay)
		throws PortalException {

		long plid = PortalUtil.getPlidFromPortletId(
			themeDisplay.getScopeGroupId(),
			CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT);

		if (plid > 0) {
			return PortletURLBuilder.create(
				_getPortletURL(
					themeDisplay.getRequest(),
					CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT)
			).setActionName(
				"/commerce_open_order_content/edit_commerce_order"
			).setCMD(
				"setCurrent"
			).setParameter(
				"commerceOrderId", "{id}"
			).buildString();
		}

		return StringPool.BLANK;
	}

	private PortletURL _getPortletURL(
			HttpServletRequest httpServletRequest, String portletId)
		throws PortalException {

		long groupId = PortalUtil.getScopeGroupId(httpServletRequest);

		long plid = PortalUtil.getPlidFromPortletId(groupId, portletId);

		if (plid > 0) {
			return PortletURLFactoryUtil.create(
				httpServletRequest, portletId, plid,
				PortletRequest.ACTION_PHASE);
		}

		return PortletURLFactoryUtil.create(
			httpServletRequest, portletId, PortletRequest.ACTION_PHASE);
	}

	private static final String _PAGE = "/account_selector/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		AccountSelectorTag.class);

	private String[] _accountEntryAllowedTypes;
	private String _addCommerceOrderURL;
	private CommerceAccount _commerceAccount;
	private long _commerceChannelId;
	private CommerceOrder _commerceOrder;
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;
	private String _editOrderURL;
	private String _setCurrentAccountURL;
	private String _spritemap;
	private ThemeDisplay _themeDisplay;

}