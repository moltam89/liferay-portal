/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.asset.service.persistence;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Juan Fernández
 */
public class AssetEntryQuery {

	public static final String[] ORDER_BY_COLUMNS = new String[] {
		"title", "createDate", "modifiedDate", "publishDate", "expirationDate",
		"priority", "viewCount", "ratings"
	};

	public static String checkOrderByCol(String orderByCol) {
		if (ArrayUtil.contains(ORDER_BY_COLUMNS, orderByCol)) {
			return orderByCol;
		}

		return ORDER_BY_COLUMNS[2];
	}

	public static String checkOrderByType(String orderByType) {
		if ((orderByType == null) || orderByType.equalsIgnoreCase("DESC")) {
			return "DESC";
		}
		else {
			return "ASC";
		}
	}

	public AssetEntryQuery() {
		Date now = new Date();

		_expirationDate = now;
		_publishDate = now;
	}

	public AssetEntryQuery(AssetEntryQuery assetEntryQuery) {
		setAllCategoryIds(assetEntryQuery.getAllCategoryIds());
		setAllTagIds(assetEntryQuery.getAllTagIds());
		setAllTagIdsArray(assetEntryQuery.getAllTagIdsArray());
		setAnyCategoryIds(assetEntryQuery.getAnyCategoryIds());
		setAnyCategoryIdsArray(assetEntryQuery.getAnyCategoryIdsArray());
		setAnyTagIds(assetEntryQuery.getAnyTagIds());
		setAnyTagIdsArray(assetEntryQuery.getAnyTagIdsArray());
		setClassNameIds(assetEntryQuery.getClassNameIds());
		setClassTypeIds(assetEntryQuery.getClassTypeIds());
		setEnablePermissions(assetEntryQuery.isEnablePermissions());
		setEnd(assetEntryQuery.getEnd());
		setExcludeZeroViewCount(assetEntryQuery.isExcludeZeroViewCount());
		setExpirationDate(assetEntryQuery.getExpirationDate());
		setGroupIds(assetEntryQuery.getGroupIds());
		setLayout(assetEntryQuery.getLayout());
		setLinkedAssetEntryId(assetEntryQuery.getLinkedAssetEntryId());
		setNotAllCategoryIds(assetEntryQuery.getNotAllCategoryIds());
		setNotAnyTagIds(assetEntryQuery.getNotAnyTagIds());
		setNotAllTagIdsArray(assetEntryQuery.getNotAllTagIdsArray());
		setNotAnyCategoryIds(assetEntryQuery.getNotAnyCategoryIds());
		setNotAllCategoryIdsArray(assetEntryQuery.getNotAllCategoryIdsArray());
		setNotAnyTagIds(assetEntryQuery.getNotAnyTagIds());
		setNotAllTagIdsList(assetEntryQuery.getNotAllTagIdsList());
		setOrderByCol1(assetEntryQuery.getOrderByCol1());
		setOrderByCol2(assetEntryQuery.getOrderByCol2());
		setOrderByType1(assetEntryQuery.getOrderByType1());
		setOrderByType2(assetEntryQuery.getOrderByType2());
		setPublishDate(assetEntryQuery.getPublishDate());
		setStart(assetEntryQuery.getStart());
		setVisible(assetEntryQuery.isVisible());
	}

	public AssetEntryQuery(
			long[] classNameIds, SearchContainer<?> searchContainer)
		throws PortalException, SystemException {

		this();

		setClassNameIds(classNameIds);
		_start = searchContainer.getStart();
		_end = searchContainer.getEnd();

		if (Validator.isNotNull(searchContainer.getOrderByCol())) {
			setOrderByCol1(searchContainer.getOrderByCol());
			setOrderByType1(searchContainer.getOrderByType());
		}

		PortletRequest portletRequest = searchContainer.getPortletRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_groupIds = new long[] {themeDisplay.getScopeGroupId()};

		long categoryId = ParamUtil.getLong(portletRequest, "categoryId");

		if (categoryId > 0) {
			_allCategoryIds = new long[] {categoryId};
		}

		String tagName = ParamUtil.getString(portletRequest, "tag");

		if (Validator.isNotNull(tagName)) {
			_allTagIds = AssetTagLocalServiceUtil.getTagIds(
				themeDisplay.getParentGroupId(), new String[] {tagName});

			_allTagIdsArray = new long[][] {_allTagIds};
		}
	}

	public AssetEntryQuery(String className, SearchContainer<?> searchContainer)
		throws PortalException, SystemException {

		this(
			new long[] {PortalUtil.getClassNameId(className)}, searchContainer);
	}

	public void addAllCategoryIds(long[] allCategoryIds) {
		if (allCategoryIds.length == 0) {
			return;
		}

		setAllCategoryIds(ArrayUtil.append(_allCategoryIds, allCategoryIds));
	}

	public void addAllTagIdsArray(long[] allTagsIds) {
		if (allTagsIds.length == 0) {
			return;
		}

		setAllTagIdsArray(ArrayUtil.append(_allTagIdsArray, allTagsIds));
	}

	public void addAnyCategoryIdsArray(long[] anyCategoryIds) {
		if (anyCategoryIds.length == 0) {
			return;
		}

		setAnyCategoryIdsArray(
			ArrayUtil.append(_anyCategoryIdsArray, anyCategoryIds));
	}

	public void addAnyTagIdsArray(long[] anyTagIds) {
		if (anyTagIds.length == 0) {
			return;
		}

		setAnyTagIdsArray(ArrayUtil.append(_anyTagIdsArray, anyTagIds));
	}

	public void addNotAllCategoryIdsArray(long[] notAllCategoryIds) {
		if (notAllCategoryIds.length == 0) {
			return;
		}

		setNotAllCategoryIdsArray(
			ArrayUtil.append(_notAllCategoryIdsArray, notAllCategoryIds));
	}

	public void addNotAllTagIdsArray(long[][] notAllTagIds) {
		if (notAllTagIds.length == 0) {
			return;
		}

		_notAllTagIdsList.add(notAllTagIds);

		_toString = null;
	}

	public void addNotAnyCategoryIds(long[] notAnyCategoryIds) {
		if (notAnyCategoryIds.length == 0) {
			return;
		}

		setNotAnyCategoryIds(
			ArrayUtil.append(_notAnyCategoryIds, notAnyCategoryIds));
	}

	public void addNotAnyTagIds(long[] notAnyTagIds) {
		if (notAnyTagIds.length == 0) {
			return;
		}

		setNotAnyTagIds(ArrayUtil.append(_notAnyTagIds, notAnyTagIds));
	}

	public long[] getAllCategoryIds() {
		return _allCategoryIds;
	}

	public long[] getAllLeftAndRightCategoryIds() {
		return _getLeftAndRightCategoryIds(_allCategoryIds);
	}

	@Deprecated
	public long[] getAllTagIds() {
		return _allTagIds;
	}

	public long[][] getAllTagIdsArray() {
		return _allTagIdsArray;
	}

	@Deprecated
	public long[] getAnyCategoryIds() {
		return _anyCategoryIds;
	}

	public long[][] getAnyCategoryIdsArray() {
		return _anyCategoryIdsArray;
	}

	public long[] getAnyLeftAndRightCategoryIds() {
		return _getLeftAndRightCategoryIds(_anyCategoryIds);
	}

	@Deprecated
	public long[] getAnyTagIds() {
		return _anyTagIds;
	}

	public long[][] getAnyTagIdsArray() {
		return _anyTagIdsArray;
	}

	public long[] getClassNameIds() {
		return _classNameIds;
	}

	public long[] getClassTypeIds() {
		return _classTypeIds;
	}

	public int getEnd() {
		return _end;
	}

	public Date getExpirationDate() {
		return _expirationDate;
	}

	public long[] getGroupIds() {
		return _groupIds;
	}

	public Layout getLayout() {
		return _layout;
	}

	public long getLinkedAssetEntryId() {
		return _linkedAssetEntryId;
	}

	@Deprecated
	public long[] getNotAllCategoryIds() {
		return _notAllCategoryIds;
	}

	public long[][] getNotAllCategoryIdsArray() {
		return _notAllCategoryIdsArray;
	}

	@Deprecated
	public long[] getNotAllLeftAndRightCategoryIds() {
		return _getLeftAndRightCategoryIds(_notAllCategoryIds);
	}

	@Deprecated
	public long[] getNotAllTagIds() {
		return _notAllTagIds;
	}

	@Deprecated
	public long[][] getNotAllTagIdsArray() {
		return _notAllTagIdsArray;
	}

	public List<long[][]> getNotAllTagIdsList() {
		return _notAllTagIdsList;
	}

	public long[] getNotAnyCategoryIds() {
		return _notAnyCategoryIds;
	}

	public long[] getNotAnyLeftAndRightCategoryIds() {
		return _getLeftAndRightCategoryIds(_notAnyCategoryIds);
	}

	public long[] getNotAnyTagIds() {
		return _notAnyTagIds;
	}

	public String getOrderByCol1() {
		return checkOrderByCol(_orderByCol1);
	}

	public String getOrderByCol2() {
		return checkOrderByCol(_orderByCol2);
	}

	public String getOrderByType1() {
		return checkOrderByType(_orderByType1);
	}

	public String getOrderByType2() {
		return checkOrderByType(_orderByType2);
	}

	public Date getPublishDate() {
		return _publishDate;
	}

	public int getStart() {
		return _start;
	}

	public boolean isEnablePermissions() {
		return _enablePermissions;
	}

	public boolean isExcludeZeroViewCount() {
		return _excludeZeroViewCount;
	}

	public Boolean isVisible() {
		return _visible;
	}

	public void setAllCategoryIds(long[] allCategoryIds) {
		_allCategoryIds = allCategoryIds;

		_toString = null;
	}

	@Deprecated
	public void setAllTagIds(long[] allTagIds) {
		_allTagIds = allTagIds;

		_allTagIdsArray = _expandTagIds(allTagIds);

		_toString = null;
	}

	public void setAllTagIdsArray(long[][] allTagIdsArray) {
		_allTagIdsArray = allTagIdsArray;

		_allTagIds = _flattenTagIds(allTagIdsArray);

		_toString = null;
	}

	@Deprecated
	public void setAnyCategoryIds(long[] anyCategoryIds) {
		_anyCategoryIds = anyCategoryIds;

		_toString = null;
	}

	public void setAnyCategoryIdsArray(long[][] anyCategoryIdsArray) {
		_anyCategoryIdsArray = anyCategoryIdsArray;

		_toString = null;
	}

	@Deprecated
	public void setAnyTagIds(long[] anyTagIds) {
		_anyTagIds = anyTagIds;

		_toString = null;
	}

	public void setAnyTagIdsArray(long[][] anyTagIdsArray) {
		_anyTagIdsArray = anyTagIdsArray;

		_toString = null;
	}

	public void setClassName(String className) {
		long classNameId = PortalUtil.getClassNameId(className);

		_classNameIds = new long[] {classNameId};

		_toString = null;
	}

	public void setClassNameIds(long[] classNameIds) {
		_classNameIds = classNameIds;

		_toString = null;
	}

	public void setClassTypeIds(long[] classTypeIds) {
		_classTypeIds = classTypeIds;

		_toString = null;
	}

	public void setEnablePermissions(boolean enablePermissions) {
		_enablePermissions = enablePermissions;
	}

	public void setEnd(int end) {
		_end = end;

		_toString = null;
	}

	public void setExcludeZeroViewCount(boolean excludeZeroViewCount) {
		_excludeZeroViewCount = excludeZeroViewCount;

		_toString = null;
	}

	public void setExpirationDate(Date expirationDate) {
		_expirationDate = expirationDate;

		_toString = null;
	}

	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;

		_toString = null;
	}

	public void setLayout(Layout layout) {
		_layout = layout;

		_toString = null;
	}

	public void setLinkedAssetEntryId(long linkedAssetEntryId) {
		_linkedAssetEntryId = linkedAssetEntryId;

		_toString = null;
	}

	public void setNotAllCategoryIds(long[] notAllCategoryIds) {
		_notAllCategoryIds = notAllCategoryIds;

		_toString = null;
	}

	public void setNotAllCategoryIdsArray(long[][] notAllCategoryIdsArray) {
		_notAllCategoryIdsArray = notAllCategoryIdsArray;

		_toString = null;
	}

	@Deprecated
	public void setNotAllTagIds(long[] notAllTagIds) {
		_notAllTagIds = notAllTagIds;

		_notAllTagIdsArray = _expandTagIds(notAllTagIds);

		_toString = null;
	}

	@Deprecated
	public void setNotAllTagIdsArray(long[][] notAllTagIdsArray) {
		_notAllTagIdsArray = notAllTagIdsArray;

		_notAllTagIds = _flattenTagIds(notAllTagIdsArray);

		_toString = null;
	}

	public void setNotAllTagIdsList(List<long[][]> notAllTagIdsList) {
		_notAllTagIdsList = notAllTagIdsList;

		_toString = null;
	}

	public void setNotAnyCategoryIds(long[] notAnyCategoryIds) {
		_notAnyCategoryIds = notAnyCategoryIds;

		_toString = null;
	}

	public void setNotAnyTagIds(long[] notAnyTagIds) {
		_notAnyTagIds = notAnyTagIds;

		_toString = null;
	}

	public void setOrderByCol1(String orderByCol1) {
		_orderByCol1 = orderByCol1;

		_toString = null;
	}

	public void setOrderByCol2(String orderByCol2) {
		_orderByCol2 = orderByCol2;

		_toString = null;
	}

	public void setOrderByType1(String orderByType1) {
		_orderByType1 = orderByType1;

		_toString = null;
	}

	public void setOrderByType2(String orderByType2) {
		_orderByType2 = orderByType2;

		_toString = null;
	}

	public void setPublishDate(Date publishDate) {
		_publishDate = publishDate;

		_toString = null;
	}

	public void setStart(int start) {
		_start = start;

		_toString = null;
	}

	public void setVisible(Boolean visible) {
		_visible = visible;

		_toString = null;
	}

	@Override
	public String toString() {
		if (_toString != null) {
			return _toString;
		}

		StringBundler sb = new StringBundler(55);

		sb.append("{allCategoryIds=");
		sb.append(StringUtil.merge(_allCategoryIds));
		sb.append(", allTagIds=");
		sb.append(StringUtil.merge(_allTagIds));
		sb.append(", anyCategoryIds=");
		sb.append(StringUtil.merge(_anyCategoryIds));
		sb.append(", anyCategoryIdsArray=");
		sb.append(_2DArrayToString(_anyCategoryIdsArray));
		sb.append(", anyTagIds=");
		sb.append(StringUtil.merge(_anyTagIds));
		sb.append(", anyTagIdsArray=");
		sb.append(_2DArrayToString(_anyTagIdsArray));
		sb.append(", classNameIds=");
		sb.append(StringUtil.merge(_classNameIds));
		sb.append(", classTypeIds=");
		sb.append(StringUtil.merge(_classTypeIds));

		if (_layout != null) {
			sb.append(", layout=");
			sb.append(_layout.getPlid());
		}

		sb.append(", end=");
		sb.append(_end);
		sb.append(", excludeZeroViewCount=");
		sb.append(_excludeZeroViewCount);
		sb.append(", expirationDate=");
		sb.append(_expirationDate);
		sb.append(", groupIds=");
		sb.append(StringUtil.merge(_groupIds));
		sb.append(", linkedAssetEntryId=");
		sb.append(_linkedAssetEntryId);
		sb.append(", notAllCategoryIds=");
		sb.append(StringUtil.merge(_notAllCategoryIds));
		sb.append(", notAllCategoryIdsArray=");
		sb.append(_2DArrayToString(_notAllCategoryIdsArray));
		sb.append(", notAllTagIds=");
		sb.append(StringUtil.merge(_notAllTagIds));
		sb.append(", notAllTagIdsList=");
		sb.append(_tagIdsListToString(_notAllTagIdsList));
		sb.append(", notAnyCategoryIds=");
		sb.append(StringUtil.merge(_notAnyCategoryIds));
		sb.append(", notAnyTagIds=");
		sb.append(StringUtil.merge(_notAnyTagIds));

		sb.append(", orderByCol1=");
		sb.append(_orderByCol1);
		sb.append(", orderByCol2=");
		sb.append(_orderByCol2);
		sb.append(", orderByType1=");
		sb.append(_orderByType1);
		sb.append(", orderByType2=");
		sb.append(_orderByType2);
		sb.append(", publishDate=");
		sb.append(_publishDate);
		sb.append(", start=");
		sb.append(_start);
		sb.append(", visible=");
		sb.append(_visible);
		sb.append("}");

		_toString = sb.toString();

		return _toString;
	}

	private String _2DArrayToString(long[][] array) {
		StringBundler sb = new StringBundler(array.length * 2);

		for (long[] it : array) {
			sb.append(StringUtil.merge(it));
			sb.append(StringPool.SEMICOLON);
		}

		return sb.toString();
	}

	private long[][] _expandTagIds(long[] tagIds) {
		long[][] tagIdsArray = new long[tagIds.length][1];

		for (int i = 0; i < tagIds.length; i++) {
			tagIdsArray[i][0] = tagIds[i];
		}

		return tagIdsArray;
	}

	private long[] _flattenTagIds(long[][] tagIdsArray) {
		List<Long> tagIdsList = new LinkedList<Long>();

		for (int i = 0; i < tagIdsArray.length; i++) {
			long[] tagIds = tagIdsArray[i];

			for (int j = 0; j < tagIds.length; j++) {
				long tagId = tagIds[j];

				tagIdsList.add(tagId);
			}
		}

		return ArrayUtil.toArray(
			tagIdsList.toArray(new Long[tagIdsList.size()]));
	}

	private long[] _getLeftAndRightCategoryIds(long[] categoryIds) {
		long[] leftRightIds = new long[categoryIds.length * 3];

		for (int i = 0; i < categoryIds.length; i++) {
			long categoryId = categoryIds[i];

			try {
				AssetCategory category =
					AssetCategoryLocalServiceUtil.getCategory(categoryId);

				leftRightIds[3 * i] = category.getGroupId();
				leftRightIds[3 * i + 1] = category.getLeftCategoryId();
				leftRightIds[3 * i + 2] = category.getRightCategoryId();
			}
			catch (Exception e) {
				_log.warn("Error retrieving category " + categoryId);
			}
		}

		return leftRightIds;
	}

	private String _tagIdsListToString(List<long[][]> tagIdsList) {
		StringBundler sb = new StringBundler(tagIdsList.size() * 2);

		for (long[][] it : tagIdsList) {
			sb.append(_2DArrayToString(it));
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private static Log _log = LogFactoryUtil.getLog(AssetEntryQuery.class);

	private long[] _allCategoryIds = new long[0];
	private long[] _allTagIds = new long[0];
	private long[][] _allTagIdsArray = new long[0][];
	private long[] _anyCategoryIds = new long[0];
	private long[][] _anyCategoryIdsArray = new long[0][];
	private long[] _anyTagIds = new long[0];
	private long[][] _anyTagIdsArray = new long[0][];
	private long[] _classNameIds = new long[0];
	private long[] _classTypeIds = new long[0];
	private boolean _enablePermissions;
	private int _end = QueryUtil.ALL_POS;
	private boolean _excludeZeroViewCount;
	private Date _expirationDate;
	private long[] _groupIds = new long[0];
	private Layout _layout;
	private long _linkedAssetEntryId = 0;
	private long[] _notAllCategoryIds = new long[0];
	private long[][] _notAllCategoryIdsArray = new long[0][];
	private long[] _notAllTagIds = new long[0];
	private long[][] _notAllTagIdsArray = new long[0][];
	private List<long[][]> _notAllTagIdsList = new LinkedList<long[][]>();
	private long[] _notAnyCategoryIds = new long[0];
	private long[] _notAnyTagIds = new long[0];
	private String _orderByCol1;
	private String _orderByCol2;
	private String _orderByType1;
	private String _orderByType2;
	private Date _publishDate;
	private int _start = QueryUtil.ALL_POS;
	private String _toString;
	private Boolean _visible = Boolean.TRUE;

}