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
CPPublisherConfigurationDisplayContext cpPublisherConfigurationDisplayContext = (CPPublisherConfigurationDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<aui:fieldset markupView="lexicon">
	<aui:input checked="<%= cpPublisherConfigurationDisplayContext.isRenderSelectionADT() %>" id="renderSelectionADT" label="use-adt" name="preferences--renderSelection--" onChange='<%= liferayPortletResponse.getNamespace() + "chooseRenderSelection();" %>' type="radio" value="adt" />

	<aui:input checked="<%= cpPublisherConfigurationDisplayContext.isRenderSelectionCustomRenderer() %>" id="renderSelectionCustomRenderer" label="use-custom-renderer" name="preferences--renderSelection--" onChange='<%= liferayPortletResponse.getNamespace() + "chooseRenderSelection();" %>' type="radio" value="custom" />
</aui:fieldset>

<aui:script>
	function <portlet:namespace />chooseRenderSelection() {
		var form = window.document['<portlet:namespace />fm'];

		form['<portlet:namespace /><%= Constants.CMD %>'].value =
			'render-selection';

		form.submit();
	}
</aui:script>