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

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.persistence.RoleActionableDynamicQuery;
import com.liferay.portal.util.PortletKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.PortletURL;

/**
 * @author Akos Thurzo
 */
public class RoleIndexer extends BaseIndexer {

	public static final String[] CLASS_NAMES = {Role.class.getName()};

	public static final String PORTLET_ID = PortletKeys.ROLES_ADMIN;

	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	public String getPortletId() {
		return PORTLET_ID;
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		BooleanQuery fieldQuery = BooleanQueryFactoryUtil.create(searchContext);

		addSearchTerm(fieldQuery, searchContext, Field.NAME, false);
		addSearchLocalizedTerm(fieldQuery, searchContext, Field.TITLE, false);
		addSearchLocalizedTerm(
			fieldQuery, searchContext, Field.DESCRIPTION, false);

		if (fieldQuery.hasClauses()) {
			searchQuery.add(fieldQuery, BooleanClauseOccur.MUST);
		}

		BooleanQuery typeQuery = BooleanQueryFactoryUtil.create(searchContext);

		addSearchArrayQuery(typeQuery, searchContext, Field.TYPE, true);

		if (typeQuery.hasClauses()) {
			searchQuery.add(typeQuery, BooleanClauseOccur.MUST);
		}
	}

	@Override
	protected void addSearchLocalizedTerm(
			BooleanQuery searchQuery, SearchContext searchContext, String field,
			boolean like)
		throws Exception {

		if (Validator.isNull(field)) {
			return;
		}

		String value = String.valueOf(searchContext.getAttribute(field));

		if (Validator.isNull(value)) {
			value = searchContext.getKeywords();
		}

		if (Validator.isNull(value)) {
			return;
		}

		field = DocumentImpl.getLocalizedName(searchContext.getLocale(), field);

		if (searchContext.isAndSearch()) {
			searchQuery.addRequiredTerm(field, value, like);
		}
		else {
			searchQuery.addTerm(field, value, like);
		}
	}

	@Override
	protected void doDelete(Object obj) throws Exception {

		Role role = (Role)obj;

		deleteDocument(role.getCompanyId(), role.getRoleId());
	}

	@Override
	protected Document doGetDocument(Object obj) throws Exception {

		Role role = (Role)obj;

		Document document = getBaseModelDocument(PORTLET_ID, role);

		document.addKeyword(Field.COMPANY_ID, role.getCompanyId());
		document.addText(Field.NAME, role.getName());
		document.addLocalizedText(Field.TITLE, role.getTitleMap());
		document.addLocalizedText(Field.DESCRIPTION, role.getDescriptionMap());
		document.addKeyword(Field.TYPE, role.getType());

		return document;
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletURL portletURL)
		throws Exception {

		return null;
	}

	@Override
	protected void doReindex(Object obj) throws Exception {

		Role role = (Role)obj;

		Document document = getDocument(role);

		SearchEngineUtil.updateDocument(
			getSearchEngineId(), role.getCompanyId(), document);
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		Role role = RoleLocalServiceUtil.getRole(classPK);

		doReindex(role);
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		reindexRoles(companyId);
	}

	@Override
	protected String getPortletId(SearchContext searchContext) {
		return PORTLET_ID;
	}

	protected void reindexRoles(long companyId)
		throws PortalException, SystemException {

		final Collection<Document> documents = new ArrayList<Document>();

		ActionableDynamicQuery actionalbleDynamicQuery =
			new RoleActionableDynamicQuery() {

			@Override
			protected void performAction(Object object)
				throws PortalException, SystemException {

				Role role = (Role)object;

				Document document = getDocument(role);
				documents.add(document);
			}
		};

		actionalbleDynamicQuery.setCompanyId(companyId);

		actionalbleDynamicQuery.performActions();

		SearchEngineUtil.updateDocuments(
			getSearchEngineId(), companyId, documents);
	}

}