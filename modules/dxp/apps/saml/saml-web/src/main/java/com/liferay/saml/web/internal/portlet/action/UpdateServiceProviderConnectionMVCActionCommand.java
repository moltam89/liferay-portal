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

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;

import java.io.InputStream;

import java.util.Objects;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"javax.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/update_service_provider_connection"
	},
	service = MVCActionCommand.class
)
public class UpdateServiceProviderConnectionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		long samlIdpSpConnectionId = ParamUtil.getLong(
			uploadPortletRequest, "samlIdpSpConnectionId");

		int assertionLifetime = ParamUtil.getInteger(
			uploadPortletRequest, "assertionLifetime");
		String attributeNames = ParamUtil.getString(
			uploadPortletRequest, "attributeNames");
		boolean attributesEnabled = ParamUtil.getBoolean(
			uploadPortletRequest, "attributesEnabled");
		boolean attributesNamespaceEnabled = ParamUtil.getBoolean(
			uploadPortletRequest, "attributesNamespaceEnabled");
		boolean enabled = ParamUtil.getBoolean(uploadPortletRequest, "enabled");
		boolean encryptionForced = ParamUtil.getBoolean(
			uploadPortletRequest, "encryptionForced");

		String metadataUrl = null;
		InputStream metadataXmlInputStream = null;

		if (Objects.equals(
				ParamUtil.getString(uploadPortletRequest, "metadataDelivery"),
				"metadataXml")) {

			metadataUrl = null;
			metadataXmlInputStream = uploadPortletRequest.getFileAsStream(
				"metadataXml");
		}
		else {
			metadataUrl = ParamUtil.getString(
				uploadPortletRequest, "metadataUrl");
			metadataXmlInputStream = null;
		}

		String name = ParamUtil.getString(uploadPortletRequest, "name");
		String nameIdAttribute = ParamUtil.getString(
			uploadPortletRequest, "nameIdAttribute");
		String nameIdFormat = ParamUtil.getString(
			uploadPortletRequest, "nameIdFormat");
		String samlSpEntityId = ParamUtil.getString(
			uploadPortletRequest, "samlSpEntityId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			SamlIdpSpConnection.class.getName(), uploadPortletRequest);

		if (samlIdpSpConnectionId <= 0) {
			_samlIdpSpConnectionLocalService.addSamlIdpSpConnection(
				assertionLifetime, attributeNames, attributesEnabled,
				attributesNamespaceEnabled, enabled, encryptionForced,
				metadataUrl, metadataXmlInputStream, name, nameIdAttribute,
				nameIdFormat, samlSpEntityId, serviceContext);
		}
		else {
			_samlIdpSpConnectionLocalService.updateSamlIdpSpConnection(
				samlIdpSpConnectionId, assertionLifetime, attributeNames,
				attributesEnabled, attributesNamespaceEnabled, enabled,
				encryptionForced, metadataUrl, metadataXmlInputStream, name,
				nameIdAttribute, nameIdFormat, samlSpEntityId, serviceContext);
		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

}