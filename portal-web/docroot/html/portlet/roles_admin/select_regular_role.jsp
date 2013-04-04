<%--
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
--%>

<%@ include file="/html/portlet/roles_admin/init.jsp" %>

<%
String p_u_i_d = ParamUtil.getString(request, "p_u_i_d");
String callback = ParamUtil.getString(request, "callback", "selectRole");

User selUser = PortalUtil.getSelectedUser(request);

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/roles_admin/select_regular_role");

if (selUser != null) {
	portletURL.setParameter("p_u_i_d", String.valueOf(selUser.getUserId()));
}

portletURL.setParameter("callback", callback);
%>

<aui:form action="<%= portletURL.toString() %>" method="post" name="fm">
	<liferay-ui:header
		title="roles"
	/>

	<liferay-ui:search-container
		headerNames="name"
		searchContainer="<%= new RoleSearch(renderRequest, portletURL) %>"
	>
		<liferay-ui:search-form
			page="/html/portlet/roles_admin/role_search.jsp"
		/>

		<%
		RoleSearchTerms searchTerms = (RoleSearchTerms)searchContainer.getSearchTerms();
		%>

		<liferay-ui:search-container-results>

			<%
			Sort sort = SortFactoryUtil.getSort(Role.class, searchContainer.getOrderByCol(), searchContainer.getOrderByType());

			if (filterManageableRoles) {
				List<Role> roles = null;

				while (true) {
					Hits hits = null;

					hits = RoleLocalServiceUtil.search(company.getCompanyId(), searchTerms.getKeywords(), new Integer[] {RoleConstants.TYPE_REGULAR}, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, sort);

					Tuple tuple = RolesAdminUtil.getRoles(hits);

					boolean corruptIndex = (Boolean)tuple.getObject(1);

					if (!corruptIndex) {
						roles = (List<Role>)tuple.getObject(0);

						break;
					}
				}

				roles = UsersAdminUtil.filterRoles(permissionChecker, roles);

				total = roles.size();
				results = ListUtil.subList(roles, searchContainer.getStart(), searchContainer.getEnd());
			}
			else {
				while (true) {
					Hits hits = null;

					hits = RoleLocalServiceUtil.search(company.getCompanyId(), searchTerms.getKeywords(), new Integer[] {RoleConstants.TYPE_REGULAR}, null, searchContainer.getStart(), searchContainer.getEnd(), sort);

					Tuple tuple = RolesAdminUtil.getRoles(hits);

					boolean corruptIndex = (Boolean)tuple.getObject(1);

					if (!corruptIndex) {
						results = (List<Role>)tuple.getObject(0);
						total = hits.getLength();

						break;
					}
				}
			}

			pageContext.setAttribute("results", results);
			pageContext.setAttribute("total", total);
			%>

		</liferay-ui:search-container-results>

		<liferay-ui:search-container-row
			className="com.liferay.portal.model.Role"
			keyProperty="roleId"
			modelVar="role"
		>
			<liferay-util:param name="className" value="<%= RolesAdminUtil.getCssClassName(role) %>" />
			<liferay-util:param name="classHoverName" value="<%= RolesAdminUtil.getCssClassName(role) %>" />

			<%
			String rowHREF = null;

			if (Validator.isNull(p_u_i_d)|| RoleMembershipPolicyUtil.isRoleAllowed((selUser != null) ? selUser.getUserId() : 0, role.getRoleId())) {
				StringBundler sb = new StringBundler(8);

				sb.append("javascript:opener.");
				sb.append(renderResponse.getNamespace());
				sb.append(callback);
				sb.append("('");
				sb.append(role.getRoleId());
				sb.append("', '");
				sb.append(UnicodeFormatter.toString(role.getTitle(locale)));
				sb.append("', 'roles'); window.close();");

				rowHREF = sb.toString();
			}
			%>

			<liferay-ui:search-container-column-text
				href="<%= rowHREF %>"
				name="title"
				value="<%= HtmlUtil.escape(role.getTitle(locale)) %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator />
	</liferay-ui:search-container>
</aui:form>

<aui:script>
	Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />name);
</aui:script>