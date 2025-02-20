/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.action;

import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gavin Wan
 * @author Peter Shin
 */
@Component(
	property = "javax.portlet.name=" + ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
	service = ConfigurationAction.class
)
public class ReportsAdminConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/admin/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String tabs2 = ParamUtil.getString(actionRequest, "tabs2");

		if (tabs2.equals("delivery-email")) {
			_validateEmailDelivery(actionRequest);
		}
		else if (tabs2.equals("email-from")) {
			validateEmailFrom(actionRequest);
		}
		else if (tabs2.equals("notifications-email")) {
			_validateEmailNotifications(actionRequest);
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private void _validateEmailDelivery(ActionRequest actionRequest)
		throws Exception {

		String emailDeliverySubject = getParameter(
			actionRequest, "emailDeliverySubject");
		String emailDeliveryBody = getParameter(
			actionRequest, "emailDeliveryBody");

		if (Validator.isNull(emailDeliverySubject)) {
			SessionErrors.add(actionRequest, "emailDeliverySubject");
		}
		else if (Validator.isNull(emailDeliveryBody)) {
			SessionErrors.add(actionRequest, "emailDeliveryBody");
		}
	}

	private void _validateEmailNotifications(ActionRequest actionRequest)
		throws Exception {

		String emailNotificationsSubject = getParameter(
			actionRequest, "emailNotificationsSubject");
		String emailNotificationsBody = getParameter(
			actionRequest, "emailNotificationsBody");

		if (Validator.isNull(emailNotificationsSubject)) {
			SessionErrors.add(actionRequest, "emailNotificationsSubject");
		}
		else if (Validator.isNull(emailNotificationsBody)) {
			SessionErrors.add(actionRequest, "emailNotificationsBody");
		}
	}

}