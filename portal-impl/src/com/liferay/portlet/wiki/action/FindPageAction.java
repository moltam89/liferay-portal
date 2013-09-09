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

package com.liferay.portlet.wiki.action;

import com.liferay.portal.NoSuchLayoutException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.permission.LayoutPermissionUtil;
import com.liferay.portal.struts.FindAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPageResource;
import com.liferay.portlet.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.portlet.wiki.service.WikiPageResourceLocalServiceUtil;
import com.liferay.portlet.wiki.util.WikiPlidPortletMatcher;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Samuel Kong
 */
public class FindPageAction extends FindAction {

	@Override
	protected long getGroupId(long primaryKey) throws Exception {
		WikiPageResource pageResource =
			WikiPageResourceLocalServiceUtil.getPageResource(primaryKey);

		WikiNode node = WikiNodeLocalServiceUtil.getNode(
			pageResource.getNodeId());

		return node.getGroupId();
	}

	@Override
	protected Object[] getPlidAndPortletId(
			HttpServletRequest request, long plid, long primaryKey)
		throws Exception {

		String[] _portletIds = initPortletIds();

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		long groupId = ParamUtil.getLong(
			request, "groupId", themeDisplay.getScopeGroupId());

		if (primaryKey > 0) {
			try {
				groupId = getGroupId(primaryKey);
			}
			catch (Exception e) {
				if (_log.isDebugEnabled()) {
					_log.debug(e, e);
				}
			}
		}

		if ((plid != LayoutConstants.DEFAULT_PLID) &&
			(groupId == themeDisplay.getScopeGroupId())) {

			try {
				Layout layout = LayoutLocalServiceUtil.getLayout(plid);

				LayoutTypePortlet layoutTypePortlet =
					(LayoutTypePortlet)layout.getLayoutType();

				for (String portletId : _portletIds) {
					if (!layoutTypePortlet.hasPortletId(portletId) ||
						!LayoutPermissionUtil.contains(
							permissionChecker, layout, ActionKeys.VIEW)) {

						continue;
					}

					portletId = getPortletId(layoutTypePortlet, portletId);

					return new Object[] {plid, portletId};
				}
			}
			catch (NoSuchLayoutException nsle) {
			}
		}

		long pageResourcePrimKey = ParamUtil.getLong(
			request, getPrimaryKeyParameterName());

		_pageResource = WikiPageResourceLocalServiceUtil.getPageResource(
			pageResourcePrimKey);

		_wikiNode = WikiNodeLocalServiceUtil.getNode(_pageResource.getNodeId());

		WikiPlidPortletMatcher wikiPlidPortletMatcher =
			new WikiPlidPortletMatcher(_wikiNode);

		for (String portletId : _portletIds) {
			plid = PortalUtil.getPlidFromPortletId(
				groupId, portletId, wikiPlidPortletMatcher);

			if (plid == LayoutConstants.DEFAULT_PLID) {
				continue;
			}

			Layout layout = LayoutLocalServiceUtil.getLayout(plid);

			if (!LayoutPermissionUtil.contains(
					permissionChecker, layout, ActionKeys.VIEW)) {

				continue;
			}

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			portletId = getPortletId(layoutTypePortlet, portletId);

			return new Object[] {plid, portletId};
		}

		throw new NoSuchLayoutException();
	}

	@Override
	protected String getPrimaryKeyParameterName() {
		return "pageResourcePrimKey";
	}

	@Override
	protected String getStrutsAction(
		HttpServletRequest request, String portletId) {

		if (portletId.equals(PortletKeys.WIKI_ADMIN)) {
			return "/wiki_admin/view";
		}
		else if (portletId.equals(PortletKeys.WIKI)) {
			return "/wiki/view";
		}
		else {
			return "/wiki_display/view";
		}
	}

	@Override
	protected String[] initPortletIds() {

		// Order is important. See LPS-23770.

		return new String[] {
			PortletKeys.WIKI_ADMIN, PortletKeys.WIKI, PortletKeys.WIKI_DISPLAY
		};
	}

	@Override
	protected PortletURL processPortletURL(
			HttpServletRequest request, PortletURL portletURL)
		throws Exception {

		portletURL.setParameter("nodeName", _wikiNode.getName());
		portletURL.setParameter("title", _pageResource.getTitle());

		return portletURL;
	}

	private static Log _log = LogFactoryUtil.getLog(FindPageAction.class);

	private WikiPageResource _pageResource;
	private WikiNode _wikiNode;

}