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

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.headless.common.spi.service.context.ServiceContextRequestUtil;
import com.liferay.headless.delivery.dto.v1_0.ContentDocument;
import com.liferay.headless.delivery.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.delivery.dto.v1_0.PageSettings;
import com.liferay.headless.delivery.dto.v1_0.ParentSitePage;
import com.liferay.headless.delivery.dto.v1_0.SEOSettings;
import com.liferay.headless.delivery.dto.v1_0.SitePage;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.SitePageEntityModel;
import com.liferay.headless.delivery.resource.v1_0.SitePageResource;
import com.liferay.layout.seo.service.LayoutSEOEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.processor.SegmentsExperienceRequestProcessorRegistry;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperienceService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MultivaluedMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/site-page.properties",
	scope = ServiceScope.PROTOTYPE, service = SitePageResource.class
)
@CTAware
public class SitePageResourceImpl extends BaseSitePageResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public SitePage getSiteSitePage(Long siteId, String friendlyUrlPath)
		throws Exception {

		return _toSitePage(true, _getLayout(siteId, friendlyUrlPath), null);
	}

	@Override
	public SitePage getSiteSitePageExperienceExperienceKey(
			Long siteId, String friendlyUrlPath, String experienceKey)
		throws Exception {

		return _toSitePage(
			true, _getLayout(siteId, friendlyUrlPath), experienceKey);
	}

	@Override
	public String getSiteSitePageExperienceExperienceKeyRenderedPage(
			Long siteId, String friendlyUrlPath, String experienceKey)
		throws Exception {

		return _toHTML(friendlyUrlPath, siteId, experienceKey);
	}

	@Override
	public String getSiteSitePageRenderedPage(
			Long siteId, String friendlyUrlPath)
		throws Exception {

		return _toHTML(friendlyUrlPath, siteId, null);
	}

	@Override
	public Page<SitePage> getSiteSitePagesExperiencesPage(
			Long siteId, String friendlyUrlPath)
		throws Exception {

		Layout layout = _getLayout(siteId, friendlyUrlPath);

		return Page.of(
			transform(
				_getSegmentsExperiences(layout),
				segmentsExperience -> _toSitePage(
					_isEmbeddedPageDefinition(), layout,
					segmentsExperience.getSegmentsExperienceKey())));
	}

	@Override
	public Page<SitePage> getSiteSitePagesPage(
			Long siteId, String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.<String, Map<String, String>>put(
				"get",
				HashMapBuilder.put(
					"href",
					JaxRsLinkUtil.getJaxRsLink(
						"headless-delivery", BaseSitePageResourceImpl.class,
						"getSiteSitePagesPage", contextUriInfo, siteId)
				).put(
					"method", "GET"
				).build()
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.add(
					new TermFilter(Field.GROUP_ID, String.valueOf(siteId)),
					BooleanClauseOccur.MUST);
			},
			filter, Layout.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(Field.TITLE, search);
				searchContext.setAttribute(
					Field.TYPE,
					new String[] {
						LayoutConstants.TYPE_COLLECTION,
						LayoutConstants.TYPE_CONTENT,
						LayoutConstants.TYPE_EMBEDDED,
						LayoutConstants.TYPE_LINK_TO_LAYOUT,
						LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
						LayoutConstants.TYPE_PANEL,
						LayoutConstants.TYPE_PORTLET, LayoutConstants.TYPE_URL
					});
				searchContext.setAttribute(
					"privateLayout", Boolean.FALSE.toString());
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {siteId});
				searchContext.setKeywords(search);
			},
			sorts,
			document -> {
				long plid = GetterUtil.getLong(
					document.get(Field.ENTRY_CLASS_PK));

				return _toSitePage(
					_isEmbeddedPageDefinition(),
					_layoutLocalService.getLayout(plid), null);
			});
	}

	@Override
	public SitePage postSiteSitePage(Long siteId, SitePage sitePage)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPS-178052")) {
			throw new UnsupportedOperationException();
		}

		Map<Locale, String> titleMap = LocalizedMapUtil.getLocalizedMap(
			contextAcceptLanguage.getPreferredLocale(), sitePage.getTitle(),
			sitePage.getTitle_i18n());

		Layout layout = _addLayout(
			siteId, sitePage, titleMap,
			LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				sitePage.getFriendlyUrlPath(),
				sitePage.getFriendlyUrlPath_i18n(), titleMap));

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getBasicActions(layout), _dtoConverterRegistry,
				contextHttpServletRequest, layout.getPlid(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser);

		return _sitePageDTOConverter.toDTO(dtoConverterContext, layout);
	}

	private Layout _addLayout(
			Long siteId, SitePage sitePage, Map<Locale, String> nameMap,
			Map<Locale, String> friendlyUrlMap)
		throws Exception {

		long parentLayoutId = 0;

		ParentSitePage parentSitePage = sitePage.getParentSitePage();

		if (parentSitePage != null) {
			Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
				siteId, false, parentSitePage.getFriendlyUrlPath());

			if (layout == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Could not find parent site page");
				}
			}
			else {
				parentLayoutId = layout.getLayoutId();
			}
		}

		Map<Locale, String> titleMap = new HashMap<>();
		Map<Locale, String> descriptionMap = new HashMap<>();
		Map<Locale, String> keywordsMap = new HashMap<>();
		Map<Locale, String> robotsMap = new HashMap<>();
		boolean hidden = false;

		PageSettings pageSettings = sitePage.getPageSettings();

		if (pageSettings != null) {
			SEOSettings seoSettings = pageSettings.getSeoSettings();

			if (seoSettings != null) {
				titleMap = LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					seoSettings.getHtmlTitle(),
					seoSettings.getHtmlTitle_i18n());
				descriptionMap = LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					seoSettings.getDescription(),
					seoSettings.getDescription_i18n());
				keywordsMap = LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					seoSettings.getSeoKeywords(),
					seoSettings.getSeoKeywords_i18n());
				robotsMap = LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					seoSettings.getRobots(), seoSettings.getRobots_i18n());
			}

			hidden = GetterUtil.getBoolean(
				pageSettings.getHiddenFromNavigation());
		}

		Layout layout = _layoutService.addLayout(
			siteId, false, parentLayoutId, nameMap, titleMap, descriptionMap,
			keywordsMap, robotsMap, LayoutConstants.TYPE_CONTENT, null, hidden,
			friendlyUrlMap, 0, _createServiceContext(siteId, sitePage));

		layout.setStatus(WorkflowConstants.STATUS_APPROVED);

		layout = _layoutLocalService.updateLayout(layout);

		_updateSEOEntry(layout, sitePage);

		Layout draftLayout = layout.fetchDraftLayout();

		UnicodeProperties typeSettingsUnicodeProperties =
			draftLayout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put("published", Boolean.TRUE.toString());

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		return _layoutLocalService.updateLayout(
			siteId, false, layout.getLayoutId(), draftLayout.getModifiedDate());
	}

	private ServiceContext _createServiceContext(
		long groupId, SitePage sitePage) {

		Long[] assetCategoryIds = {};

		if (sitePage.getTaxonomyCategoryIds() != null) {
			assetCategoryIds = sitePage.getTaxonomyCategoryIds();
		}

		String[] assetTagNames = new String[0];

		if (sitePage.getKeywords() != null) {
			assetTagNames = sitePage.getKeywords();
		}

		return ServiceContextRequestUtil.createServiceContext(
			assetCategoryIds, assetTagNames, null, groupId,
			contextHttpServletRequest, null);
	}

	private Map<String, Map<String, String>> _getBasicActions(Layout layout) {
		return HashMapBuilder.<String, Map<String, String>>put(
			"get",
			addAction(
				ActionKeys.VIEW, layout.getPlid(), "getSiteSitePage", null,
				Layout.class.getName(), layout.getGroupId())
		).put(
			"get-experiences",
			() -> {
				if (!layout.isTypeContent()) {
					return null;
				}

				return addAction(
					ActionKeys.VIEW, "getSiteSitePagesExperiencesPage",
					Group.class.getName(), layout.getGroupId());
			}
		).put(
			"get-rendered-page",
			addAction(
				ActionKeys.VIEW, layout.getPlid(),
				"getSiteSitePageRenderedPage", null, Layout.class.getName(),
				layout.getGroupId())
		).build();
	}

	private Map<String, Map<String, String>> _getExperienceActions(
		Layout layout) {

		return HashMapBuilder.<String, Map<String, String>>put(
			"get",
			addAction(
				ActionKeys.VIEW, "getSiteSitePageExperienceExperienceKey",
				Group.class.getName(), layout.getGroupId())
		).put(
			"get-rendered-page",
			addAction(
				ActionKeys.VIEW,
				"getSiteSitePageExperienceExperienceKeyRenderedPage",
				Group.class.getName(), layout.getGroupId())
		).build();
	}

	private long _getFileEntryId(OpenGraphSettings openGraphSettings) {
		if (openGraphSettings == null) {
			return 0;
		}

		ContentDocument contentDocument = openGraphSettings.getImage();

		if (contentDocument == null) {
			return 0;
		}

		try {
			FileEntry fileEntry = _dlAppService.getFileEntry(
				contentDocument.getId());

			return fileEntry.getFileEntryId();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return 0;
	}

	private Layout _getLayout(long groupId, String friendlyUrlPath)
		throws Exception {

		String resourceName = ResourceActionsUtil.getCompositeModelName(
			Layout.class.getName(), "false");

		if (!StringUtil.startsWith(friendlyUrlPath, StringPool.FORWARD_SLASH)) {
			friendlyUrlPath = StringPool.FORWARD_SLASH + friendlyUrlPath;
		}

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
				groupId, _portal.getClassNameId(resourceName), friendlyUrlPath);

		return _layoutLocalService.getLayout(
			friendlyURLEntryLocalization.getClassPK());
	}

	private SegmentsExperience _getSegmentsExperience(
			Layout layout, String segmentsExperienceKey)
		throws Exception {

		if (Validator.isNull(segmentsExperienceKey)) {
			return _getUserSegmentsExperience(layout);
		}

		return _segmentsExperienceService.fetchSegmentsExperience(
			layout.getGroupId(), segmentsExperienceKey,
			_portal.getClassNameId(Layout.class), layout.getPlid());
	}

	private List<SegmentsExperience> _getSegmentsExperiences(Layout layout)
		throws Exception {

		if (!layout.isTypeContent()) {
			return Collections.emptyList();
		}

		return _segmentsExperienceLocalService.getSegmentsExperiences(
			layout.getGroupId(), _portal.getClassNameId(Layout.class.getName()),
			layout.getPlid(), true);
	}

	private ThemeDisplay _getThemeDisplay(Layout layout) throws Exception {
		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLayout(layout);
		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setSiteGroupId(layout.getGroupId());

		return themeDisplay;
	}

	private SegmentsExperience _getUserSegmentsExperience(Layout layout)
		throws Exception {

		contextHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

		long[] segmentsEntryIds = _segmentsEntryRetriever.getSegmentsEntryIds(
			layout.getGroupId(), contextUser.getUserId(),
			_requestContextMapper.map(contextHttpServletRequest));

		long[] segmentsExperienceIds =
			_segmentsExperienceRequestProcessorRegistry.
				getSegmentsExperienceIds(
					contextHttpServletRequest, null, layout.getGroupId(),
					_portal.getClassNameId(Layout.class.getName()),
					layout.getPlid(), segmentsEntryIds);

		if (ArrayUtil.isEmpty(segmentsExperienceIds)) {
			return _segmentsExperienceLocalService.fetchSegmentsExperience(
				layout.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid());
		}

		return _segmentsExperienceLocalService.getSegmentsExperience(
			segmentsExperienceIds[0]);
	}

	private boolean _isEmbeddedPageDefinition() {
		MultivaluedMap<String, String> queryParameters =
			contextUriInfo.getQueryParameters();

		String nestedFields = queryParameters.getFirst("nestedFields");

		if (nestedFields == null) {
			return false;
		}

		return nestedFields.contains("pageDefinition");
	}

	private String _toHTML(
			String friendlyUrlPath, long groupId, String segmentsExperienceKey)
		throws Exception {

		Layout layout = _getLayout(groupId, friendlyUrlPath);

		contextHttpServletRequest = DynamicServletRequest.addQueryString(
			contextHttpServletRequest, "p_l_id=" + layout.getPlid(), false);

		SegmentsExperience segmentsExperience = _getSegmentsExperience(
			layout, segmentsExperienceKey);

		if (segmentsExperience != null) {
			contextHttpServletRequest.setAttribute(
				SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
				new long[] {segmentsExperience.getSegmentsExperienceId()});
		}

		contextHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(layout));

		ServletContext servletContext = ServletContextPool.get(
			StringPool.BLANK);

		if (contextHttpServletRequest.getAttribute(WebKeys.CTX) == null) {
			contextHttpServletRequest.setAttribute(WebKeys.CTX, servletContext);
		}

		layout.includeLayoutContent(
			contextHttpServletRequest, contextHttpServletResponse);

		StringBundler sb =
			(StringBundler)contextHttpServletRequest.getAttribute(
				WebKeys.LAYOUT_CONTENT);

		LayoutSet layoutSet = layout.getLayoutSet();

		Document document = Jsoup.parse(
			ThemeUtil.include(
				servletContext, contextHttpServletRequest,
				contextHttpServletResponse, "portal_normal.ftl",
				layoutSet.getTheme(), false));

		Element bodyElement = document.body();

		bodyElement.html(sb.toString());

		return document.html();
	}

	private SitePage _toSitePage(
			boolean embeddedPageDefinition, Layout layout,
			String segmentsExperienceKey)
		throws Exception {

		Map<String, Map<String, String>> actions = null;

		if (Validator.isNotNull(segmentsExperienceKey)) {
			actions = _getExperienceActions(layout);
		}
		else {
			actions = _getBasicActions(layout);
		}

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), actions,
				_dtoConverterRegistry, contextHttpServletRequest,
				layout.getPlid(), contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser);

		dtoConverterContext.setAttribute(
			"embeddedPageDefinition", embeddedPageDefinition);

		if (Validator.isNotNull(segmentsExperienceKey)) {
			dtoConverterContext.setAttribute(
				"segmentsExperience",
				_getSegmentsExperience(layout, segmentsExperienceKey));
			dtoConverterContext.setAttribute("showExperience", Boolean.TRUE);
		}
		else {
			dtoConverterContext.setAttribute(
				"segmentsExperience", _getUserSegmentsExperience(layout));
		}

		return _sitePageDTOConverter.toDTO(dtoConverterContext, layout);
	}

	private void _updateSEOEntry(Layout layout, SitePage sitePage)
		throws Exception {

		PageSettings pageSettings = sitePage.getPageSettings();

		if (pageSettings == null) {
			return;
		}

		SEOSettings seoSettings = pageSettings.getSeoSettings();

		boolean canonicalURLEnabled = false;
		Map<Locale, String> canonicalURLMap = new HashMap<>();

		if (seoSettings != null) {
			canonicalURLMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				seoSettings.getCustomCanonicalURL(),
				seoSettings.getCustomCanonicalURL_i18n());

			if (MapUtil.isNotEmpty(canonicalURLMap)) {
				canonicalURLEnabled = true;
			}
		}

		boolean openGraphDescriptionEnabled = false;
		Map<Locale, String> openGraphDescriptionMap = new HashMap<>();
		Map<Locale, String> openImageAltMap = new HashMap<>();
		boolean openGraphTitleEnabled = false;
		Map<Locale, String> openGraphTitleMap = new HashMap<>();

		OpenGraphSettings openGraphSettings =
			pageSettings.getOpenGraphSettings();

		if (openGraphSettings != null) {
			openGraphDescriptionMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				openGraphSettings.getDescription(),
				openGraphSettings.getDescription_i18n());

			if (MapUtil.isNotEmpty(openGraphDescriptionMap)) {
				openGraphDescriptionEnabled = true;
			}

			openImageAltMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				openGraphSettings.getImageAlt(),
				openGraphSettings.getImageAlt_i18n());

			openGraphTitleMap = LocalizedMapUtil.getLocalizedMap(
				contextAcceptLanguage.getPreferredLocale(),
				openGraphSettings.getTitle(),
				openGraphSettings.getTitle_i18n());

			if (MapUtil.isNotEmpty(openGraphTitleMap)) {
				openGraphTitleEnabled = true;
			}
		}

		_layoutSEOEntryService.updateLayoutSEOEntry(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			canonicalURLEnabled, canonicalURLMap, openGraphDescriptionEnabled,
			openGraphDescriptionMap, openImageAltMap,
			_getFileEntryId(openGraphSettings), openGraphTitleEnabled,
			openGraphTitleMap,
			ServiceContextRequestUtil.createServiceContext(
				null, layout.getGroupId(), contextHttpServletRequest, null));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SitePageResourceImpl.class);

	private static final EntityModel _entityModel = new SitePageEntityModel();

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSEOEntryService _layoutSEOEntryService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private SegmentsEntryRetriever _segmentsEntryRetriever;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperienceRequestProcessorRegistry
		_segmentsExperienceRequestProcessorRegistry;

	@Reference
	private SegmentsExperienceService _segmentsExperienceService;

	@Reference(
		target = "(component.name=com.liferay.headless.delivery.internal.dto.v1_0.converter.SitePageDTOConverter)"
	)
	private DTOConverter<Layout, SitePage> _sitePageDTOConverter;

}