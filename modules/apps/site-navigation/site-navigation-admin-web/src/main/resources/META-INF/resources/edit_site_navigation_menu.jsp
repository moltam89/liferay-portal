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

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(siteNavigationAdminDisplayContext.getSiteNavigationMenuName());
%>

<liferay-ui:success key="siteNavigationMenuItemsAdded" message='<%= GetterUtil.getString(SessionMessages.get(renderRequest, "siteNavigationMenuItemsAdded")) %>' />

<c:if test="<%= siteNavigationAdminDisplayContext.hasUpdatePermission() %>">
	<react:component
		componentId="siteNavigationMenuEditor"
		module="site_navigation_menu_editor/index"
		props="<%= siteNavigationAdminDisplayContext.getSiteNavigationContext() %>"
	/>
</c:if>