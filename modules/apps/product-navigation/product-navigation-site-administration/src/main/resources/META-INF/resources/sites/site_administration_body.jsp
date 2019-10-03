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
PanelCategory panelCategory = (PanelCategory)request.getAttribute(ApplicationListWebKeys.PANEL_CATEGORY);

SiteAdministrationPanelCategoryDisplayContext siteAdministrationPanelCategoryDisplayContext = new SiteAdministrationPanelCategoryDisplayContext(liferayPortletRequest, liferayPortletResponse, null);

Group group = siteAdministrationPanelCategoryDisplayContext.getGroup();

String liveGroupURL;

boolean showStagingInfo = siteAdministrationPanelCategoryDisplayContext.isShowStagingInfo();
%>

<c:if test="<%= group != null %>">
	<div class="row">
		<div class="col-md-12">
			<c:if test="<%= showStagingInfo %>">

				<%
				Map<String, Object> data = new HashMap<String, Object>();

				data.put("qa-id", "staging");
				%>

				<div class="float-right staging-links">
					<span class="<%= Validator.isNull(siteAdministrationPanelCategoryDisplayContext.getStagingGroupURL()) ? "active" : StringPool.BLANK %>">
						<aui:a data="<%= data %>" href="<%= siteAdministrationPanelCategoryDisplayContext.getStagingGroupURL() %>" label="staging" />
					</span>
					<span class="links-separator"> |</span>

					<%
					data.put("qa-id", "live");
					%>

					<aui:a data="<%= data %>" href="javascript:;" id="remoteLiveLink" label="<%= siteAdministrationPanelCategoryDisplayContext.getLiveGroupLabel() %>" />

					<div class="inline-item" id="<portlet:namespace />loader">
						<span aria-hidden="true" class="loading-animation loading-animation-light loading-animation-sm"></span>
					</div>
				</div>
			</c:if>

			<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.isDisplaySiteLink() %>">
				<aui:a cssClass="goto-link list-group-heading panel-header-link" href="<%= siteAdministrationPanelCategoryDisplayContext.getGroupURL() %>" label="go-to-site" />
			</c:if>
		</div>
	</div>

	<c:if test="<%= showStagingInfo %>">
		<aui:script use="aui-base,aui-io-request,aui-tooltip">
			var link = A.one('#<portlet:namespace />remoteLiveLink');
			var loader = A.one('#<portlet:namespace />loader');

			var failureCallback = function() {
				link.removeAttribute('href');

				new A.Tooltip({
					bodyContent: Liferay.Language.get(
						'the-connection-to-the-remote-live-site-cannot-be-established-due-to-a-network-problem'
					),
					position: 'right',
					trigger: A.one('#<portlet:namespace />remoteLiveLink'),
					visible: false,
					zIndex: Liferay.zIndex.TOOLTIP
				}).render();
			};

			Liferay.on('allPortletsReady', function() {
				A.io.request(
					'<%= liveGroupURL = siteAdministrationPanelCategoryDisplayContext.getLiveGroupURL() %>',
					{
						on: {
							beforeSend: function() {
								loader.show();
							},
							failure: failureCallback,
							success: function() {
								if (<%= !liveGroupURL.equals(StringPool.BLANK) %>) {
									link.attr('href', '<%= liveGroupURL %>');

									if (link.getAttribute('href') === 'javascript:;') {
										link.removeAttribute('href');
										link.addClass('active');
									}
								} else {
									failureCallback();
								}
							},
							complete: function() {
								loader.hide();
							}
						}
					}
				);
			});
		</aui:script>
	</c:if>

	<c:if test="<%= siteAdministrationPanelCategoryDisplayContext.isShowSiteAdministration() %>">
		<liferay-application-list:panel-category-body
			panelCategory="<%= panelCategory %>"
		/>
	</c:if>
</c:if>