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

package com.liferay.expando.exportimport.internal.staged.model.repository;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.adapter.StagedExpandoColumn;
import com.liferay.expando.kernel.model.adapter.StagedExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.staged.model.repository.StagedModelRepository;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.adapter.ModelAdapterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(
	immediate = true,
	property = "model.class.name=com.liferay.expando.kernel.model.adapter.StagedExpandoColumn",
	service = StagedModelRepository.class
)
public class StagedExpandoColumnStagedModelRepository
	implements StagedModelRepository<StagedExpandoColumn> {

	@Override
	public StagedExpandoColumn addStagedModel(
			PortletDataContext portletDataContext,
			StagedExpandoColumn stagedExpandoColumn)
		throws PortalException {

		Object defaultData = stagedExpandoColumn.getDefaultData();

		if (!StringPool.BLANK.equals(defaultData) && (defaultData != null)) {
			defaultData = _createProperDefaultData(stagedExpandoColumn);
		}

		ExpandoColumn expandoColumn = _expandoColumnLocalService.addColumn(
			stagedExpandoColumn.getTableId(), stagedExpandoColumn.getName(),
			stagedExpandoColumn.getType(), defaultData);

		expandoColumn = _expandoColumnLocalService.updateTypeSettings(
			expandoColumn.getColumnId(), stagedExpandoColumn.getTypeSettings());

		return ModelAdapterUtil.adapt(
			expandoColumn, ExpandoColumn.class, StagedExpandoColumn.class);
	}

	@Override
	public void deleteStagedModel(StagedExpandoColumn stagedExpandoColumn)
		throws PortalException {

		_expandoColumnLocalService.deleteColumn(stagedExpandoColumn);
	}

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException {

		JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject(
			extraData);

		List<StagedExpandoColumn> stagedExpandoColumns =
			fetchStagedModelsByUuidAndCompanyId(
				extraDataJSONObject.getString("uuid"),
				extraDataJSONObject.getInt("companyId"));

		if (ListUtil.isEmpty(stagedExpandoColumns)) {
			return;
		}

		for (StagedExpandoColumn stagedExpandoColumn : stagedExpandoColumns) {
			deleteStagedModel(stagedExpandoColumn);
		}
	}

	@Override
	public void deleteStagedModels(PortletDataContext portletDataContext)
		throws PortalException {

		List<ExpandoColumn> expandoColumns =
			_expandoColumnLocalService.getExpandoColumns(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (ExpandoColumn expandoColumn : expandoColumns) {
			_expandoColumnLocalService.deleteColumn(expandoColumn);
		}
	}

	@Override
	public StagedExpandoColumn fetchMissingReference(
		String uuid, long groupId) {

		return null;
	}

	@Override
	public StagedExpandoColumn fetchStagedModelByUuidAndGroupId(
		String uuid, long groupId) {

		return null;
	}

	@Override
	public List<StagedExpandoColumn> fetchStagedModelsByUuidAndCompanyId(
		String uuid, long companyId) {

		DynamicQuery dynamicQuery = _expandoColumnLocalService.dynamicQuery();

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(companyId));

		List<StagedExpandoTable> stagedExpandoTables =
			_stagedExpandoTableStagedModelRepository.
				fetchStagedModelsByUuidAndCompanyId(
					_parseExpandoTableUuid(uuid), companyId);

		if (ListUtil.isEmpty(stagedExpandoTables)) {
			return null;
		}

		StagedExpandoTable stagedExpandoTable = stagedExpandoTables.get(0);

		Property tableIdProperty = PropertyFactoryUtil.forName("tableId");

		dynamicQuery.add(tableIdProperty.eq(stagedExpandoTable.getTableId()));

		Property nameProperty = PropertyFactoryUtil.forName("name");

		String name = _parseExpandoColumnName(uuid);

		dynamicQuery.add(nameProperty.eq(name));

		List<ExpandoColumn> expandoColumns =
			_expandoColumnLocalService.dynamicQuery(dynamicQuery);

		if (ListUtil.isNotEmpty(expandoColumns)) {
			return ModelAdapterUtil.adapt(
				expandoColumns, ExpandoColumn.class, StagedExpandoColumn.class);
		}

		return Collections.emptyList();
	}

	@Override
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		final PortletDataContext portletDataContext) {

		final ExportActionableDynamicQuery exportActionableDynamicQuery =
			new ExportActionableDynamicQuery() {

				@Override
				public long performCount() throws PortalException {
					ManifestSummary manifestSummary =
						portletDataContext.getManifestSummary();

					StagedModelType stagedModelType = getStagedModelType();

					long modelAdditionCount = super.performCount();

					manifestSummary.addModelAdditionCount(
						stagedModelType, modelAdditionCount);

					long modelDeletionCount =
						_exportImportHelper.getModelDeletionCount(
							portletDataContext, stagedModelType);

					manifestSummary.addModelDeletionCount(
						stagedModelType, modelDeletionCount);

					return modelAdditionCount;
				}

			};

		exportActionableDynamicQuery.setBaseLocalService(
			_expandoColumnLocalService);

		Class<? extends ExpandoColumnLocalService>
			expandoColumnLocalServiceClass =
				_expandoColumnLocalService.getClass();

		exportActionableDynamicQuery.setClassLoader(
			expandoColumnLocalServiceClass.getClassLoader());

		exportActionableDynamicQuery.setCompanyId(
			portletDataContext.getCompanyId());
		exportActionableDynamicQuery.setModelClass(ExpandoColumn.class);
		exportActionableDynamicQuery.setPerformActionMethod(
			(ExpandoColumn expandoColumn) -> {
				StagedExpandoColumn stagedExpandoColumn =
					ModelAdapterUtil.adapt(
						expandoColumn, ExpandoColumn.class,
						StagedExpandoColumn.class);

				StagedModelDataHandlerUtil.exportStagedModel(
					portletDataContext, stagedExpandoColumn);
			});
		exportActionableDynamicQuery.setPrimaryKeyPropertyName("tableId");
		exportActionableDynamicQuery.setStagedModelType(
			new StagedModelType(StagedExpandoColumn.class));

		return exportActionableDynamicQuery;
	}

	@Override
	public void restoreStagedModel(
			PortletDataContext portletDataContext,
			StagedExpandoColumn stagedModel)
		throws PortletDataException {
	}

	@Override
	public StagedExpandoColumn saveStagedModel(
			StagedExpandoColumn stagedExpandoColumn)
		throws PortalException {

		ExpandoColumn expandoColumn =
			_expandoColumnLocalService.updateExpandoColumn(stagedExpandoColumn);

		return ModelAdapterUtil.adapt(
			expandoColumn, ExpandoColumn.class, StagedExpandoColumn.class);
	}

	@Override
	public StagedExpandoColumn updateStagedModel(
			PortletDataContext portletDataContext,
			StagedExpandoColumn stagedExpandoColumn)
		throws PortalException {

		Object defaultData = stagedExpandoColumn.getDefaultData();

		if (!StringPool.BLANK.equals(defaultData) && (defaultData != null)) {
			defaultData = _createProperDefaultData(stagedExpandoColumn);
		}

		_expandoColumnLocalService.updateColumn(
			stagedExpandoColumn.getColumnId(), stagedExpandoColumn.getName(),
			stagedExpandoColumn.getType(), defaultData);

		ExpandoColumn expandoColumn =
			_expandoColumnLocalService.updateTypeSettings(
				stagedExpandoColumn.getColumnId(),
				stagedExpandoColumn.getTypeSettings());

		return ModelAdapterUtil.adapt(
			expandoColumn, ExpandoColumn.class, StagedExpandoColumn.class);
	}

	@Reference(
		target = "(model.class.name=com.liferay.expando.kernel.model.adapter.StagedExpandoTable)",
		unbind = "-"
	)
	protected void setStagedExpandoTableStagedModelRepository(
		StagedModelRepository<StagedExpandoTable>
			stagedExpandoTableStagedModelRepository) {

		_stagedExpandoTableStagedModelRepository =
			stagedExpandoTableStagedModelRepository;
	}

	private <T> T _convert(Class<T> casterClass, String arguments)
		throws PortalException {

		try {
			Method casterMethod = casterClass.getDeclaredMethod(
				"valueOf", String.class);

			T convertedValue = (T)casterMethod.invoke(casterClass, arguments);

			return convertedValue;
		}
		catch (Exception e) {
			throw new PortalException(e);
		}
	}

	private Object _convertArrays(int type, String defaultData)
		throws PortalException {

		String defaultValuesSeparatedByComma = (String)defaultData;

		String[] defaultValues = defaultValuesSeparatedByComma.split(",");

		if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			List<Boolean> resultList = _convertListOfSpecificType(
				Boolean.class, defaultValues);

			return ArrayUtils.toPrimitive(resultList.toArray(new Boolean[0]));
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			Date[] result = new Date[defaultValues.length];

			for (int index = 0; index < result.length; index++) {
				result[index] = new Date(
					(Long)_convert(Long.class, defaultValues[index]));
			}

			return result;
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			List<Double> resultList = _convertListOfSpecificType(
				Double.class, defaultValues);

			return ArrayUtils.toPrimitive(resultList.toArray(new Double[0]));
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			List<Float> resultList = _convertListOfSpecificType(
				Float.class, defaultValues);

			return ArrayUtils.toPrimitive(resultList.toArray(new Float[0]));
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			List<Integer> resultList = _convertListOfSpecificType(
				Integer.class, defaultValues);

			return ArrayUtils.toPrimitive(resultList.toArray(new Integer[0]));
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			List<Long> resultList = _convertListOfSpecificType(
				Long.class, defaultValues);

			return ArrayUtils.toPrimitive(resultList.toArray(new Long[0]));
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			Number[] result = new Number[defaultValues.length];

			for (int index = 0; index < result.length; index++) {
				result[index] = NumberUtils.createNumber(defaultValues[index]);
			}

			return result;
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			List<Short> resultList = _convertListOfSpecificType(
				Short.class, defaultValues);

			return ArrayUtils.toPrimitive(resultList.toArray(new Short[0]));
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			List<String> resultList = _convertListOfSpecificType(
				String.class, defaultValues);

			return resultList.toArray(new String[0]);
		}

		return null;
	}

	private <T> List<T> _convertListOfSpecificType(
			Class<T> casterClass, String[] defaultValues)
		throws PortalException {

		List<T> result = new ArrayList<>();

		for (String element : defaultValues) {
			result.add(_convert(casterClass, element));
		}

		return result;
	}

	private Object _convertPrimitives(int type, String defaultData)
		throws PortalException {

		if (type == ExpandoColumnConstants.BOOLEAN) {
			return (Boolean)_convert(Boolean.class, defaultData);
		}
		else if (type == ExpandoColumnConstants.DATE) {
			return new Date((Long)_convert(Long.class, defaultData));
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			return (Double)_convert(Double.class, defaultData);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			return (Float)_convert(Float.class, defaultData);
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			return (Integer)_convert(Integer.class, defaultData);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			return (Long)_convert(Long.class, defaultData);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			return NumberUtils.createNumber(defaultData);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			return (Short)_convert(Short.class, defaultData);
		}
		else if (type == ExpandoColumnConstants.STRING) {
			return defaultData;
		}
		else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
			return LocalizationUtil.getLocalizationMap((String)defaultData);
		}

		return null;
	}

	private Object _createProperDefaultData(
			StagedExpandoColumn stagedExpandoColumn)
		throws PortalException {

		int type = stagedExpandoColumn.getType();
		String defaultData = stagedExpandoColumn.getDefaultData();

		Object convertedDefaultData = null;

		if (!_ARRAY_TYPES.contains(type)) {
			convertedDefaultData = _convertPrimitives(type, defaultData);
		}
		else {
			convertedDefaultData = _convertArrays(type, defaultData);
		}

		return convertedDefaultData;
	}

	private String _parseExpandoColumnName(String uuid) {
		return uuid.substring(uuid.lastIndexOf(StringPool.POUND) + 1);
	}

	private String _parseExpandoTableUuid(String uuid) {
		return uuid.substring(0, uuid.lastIndexOf(StringPool.POUND));
	}

	private static final List _ARRAY_TYPES = new ArrayList<>(
		Arrays.asList(
			ExpandoColumnConstants.BOOLEAN_ARRAY,
			ExpandoColumnConstants.DATE_ARRAY,
			ExpandoColumnConstants.DOUBLE_ARRAY,
			ExpandoColumnConstants.FLOAT_ARRAY,
			ExpandoColumnConstants.INTEGER_ARRAY,
			ExpandoColumnConstants.LONG_ARRAY,
			ExpandoColumnConstants.NUMBER_ARRAY,
			ExpandoColumnConstants.SHORT_ARRAY,
			ExpandoColumnConstants.STRING_ARRAY));

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	private StagedModelRepository<StagedExpandoTable>
		_stagedExpandoTableStagedModelRepository;

}