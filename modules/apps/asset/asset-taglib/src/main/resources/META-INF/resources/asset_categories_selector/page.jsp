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

<%@ include file="/asset_categories_selector/init.jsp" %>

<%
Map<String, Object> data = (Map<String, Object>)request.getAttribute("liferay-asset:asset-categories-selector:data");

List<Map<String, Object>> vocabularies = (List<Map<String, Object>>)data.get("vocabularies");

List<Map<String, String>> myCategories = (List<Map<String, String>>)data.get("myCategories");

System.out.println("myCategories = " + myCategories.toString());
%>

<div>
	<react:component
		module="asset_categories_selector/AssetCategoriesSelectorTag.es"
		props="<%= data %>"
	/>
</div>