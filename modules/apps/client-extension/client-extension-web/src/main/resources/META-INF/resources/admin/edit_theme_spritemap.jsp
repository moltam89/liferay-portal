<%--
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
--%>

<%@ include file="/admin/init.jsp" %>

<%
EditClientExtensionEntryDisplayContext<ThemeSpritemapCET> editClientExtensionEntryDisplayContext = (EditClientExtensionEntryDisplayContext)renderRequest.getAttribute(ClientExtensionAdminWebKeys.EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT);

ThemeSpritemapCET themeSpritemapCET = editClientExtensionEntryDisplayContext.getCET();
%>

<aui:input label="url" name="url" required="<%= true %>" type="text" value="<%= themeSpritemapCET.getURL() %>" />

<aui:input helpMessage="enable-this-for-resources-that-are-hosted-on-a-different-domain" label="enable-cross-domain-support" name="enableSVG4Everybody" type="checkbox" value="<%= themeSpritemapCET.isEnableSVG4Everybody() %>" />