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

<%@ include file="/item_selector/init.jsp" %>

<%
DDMUserPersonalFolderItemSelectorViewDisplayContext ddmUserPersonalFolderItemSelectorViewDisplayContext = (DDMUserPersonalFolderItemSelectorViewDisplayContext)request.getAttribute(DDMUserPersonalFolderItemSelectorViewDisplayContext.class.getName());
%>

<liferay-item-selector:repository-entry-browser
	editImageURL="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getEditImageURL(liferayPortletResponse) %>"
	emptyResultsMessage='<%= LanguageUtil.get(request, "there-are-no-documents-or-media-files-in-this-folder") %>'
	folderId="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getFolderId() %>"
	itemSelectedEventName="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getItemSelectedEventName() %>"
	itemSelectorReturnTypeResolver="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getItemSelectorReturnTypeResolver() %>"
	maxFileSize="<%= DLValidatorUtil.getMaxAllowableSize(themeDisplay.getScopeGroupId(), null) %>"
	portletURL="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getPortletURL() %>"
	repositoryEntries="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getPortletFileEntries() %>"
	repositoryEntriesCount="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getPortletFileEntriesCount() %>"
	showSearch="<%= false %>"
	tabName="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getTitle(locale) %>"
	uploadURL="<%= ddmUserPersonalFolderItemSelectorViewDisplayContext.getUploadURL(liferayPortletResponse) %>"
/>