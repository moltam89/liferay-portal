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

package com.liferay.portlet.rolesadmin.util;

import com.liferay.portal.NoSuchRoleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.service.RoleLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
@DoPrivileged
public class RolesAdminImpl implements RolesAdmin {

	public String getCssClassName(Role role) {
		String cssClassName = StringPool.BLANK;

		String name = role.getName();
		int type = role.getType();

		if (name.equals(RoleConstants.GUEST)) {
			cssClassName = "lfr-role-guest";
		}
		else if (type == RoleConstants.TYPE_ORGANIZATION) {
			cssClassName = "lfr-role-organization";
		}
		else if (type == RoleConstants.TYPE_REGULAR) {
			cssClassName = "lfr-role-regular";
		}
		else if (type == RoleConstants.TYPE_SITE) {
			cssClassName = "lfr-role-site";
		}
		else if (role.isTeam()) {
			cssClassName = "lfr-role-team";
		}

		return "lfr-role " + cssClassName;
	}

	public Tuple getRoles(Hits hits) throws PortalException, SystemException {
		List<Role> roles = new ArrayList<Role>();
		boolean corruptIndex = false;

		List<Document> documents = hits.toList();

		for (Document document : documents) {
			long roleId = GetterUtil.getLong(document.get(Field.ROLE_ID));

			try {
				Role role = RoleLocalServiceUtil.getRole(roleId);

				roles.add(role);
			}
			catch (NoSuchRoleException nsre) {
				corruptIndex = true;

				Indexer indexer = IndexerRegistryUtil.getIndexer(Role.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
		}

		return new Tuple(roles, corruptIndex);
	}

}