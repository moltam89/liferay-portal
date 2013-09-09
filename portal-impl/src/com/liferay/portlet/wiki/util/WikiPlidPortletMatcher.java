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

package com.liferay.portlet.wiki.util;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Layout;
import com.liferay.portal.util.PlidPortletMatcher;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.wiki.model.WikiNode;

import javax.portlet.PortletPreferences;

/**
 * @author Gabor Pusoma
 */
public class WikiPlidPortletMatcher implements PlidPortletMatcher {

	public WikiPlidPortletMatcher(WikiNode wikiNode) {
		_wikiNode = wikiNode;
	}

	@Override
	public String getCacheKey() {
		return _wikiNode.getName();
	}

	@Override
	public boolean valid(Layout layout, String portletId) {
		return !isHiddenWikiNode(layout, portletId);
	}

	private boolean isHiddenWikiNode(Layout layout, String portletId) {
		try {
			PortletPreferences portletSetup =
				PortletPreferencesFactoryUtil.getLayoutPortletSetup(
					layout, portletId);

			String[] hiddenNodesNames = portletSetup.getValues(
				"hiddenNodes", null);

			if (hiddenNodesNames == null) {
				return false;
			}

			String wikiName = _wikiNode.getName();

			for (String hiddenNodeName : hiddenNodesNames) {
				if (hiddenNodeName.equals(wikiName)) {
					return true;
				}
			}
		}
		catch (SystemException e) {
			_log.error(e, e);
		}

		return false;
	}

	private static Log _log = LogFactoryUtil.getLog(
		WikiPlidPortletMatcher.class);

	private WikiNode _wikiNode;

}