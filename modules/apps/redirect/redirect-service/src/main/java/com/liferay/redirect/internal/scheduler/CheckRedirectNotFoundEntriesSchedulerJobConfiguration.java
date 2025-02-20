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

package com.liferay.redirect.internal.scheduler;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.Time;
import com.liferay.redirect.internal.configuration.RedirectConfiguration;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 * @author Alicia García
 */
@Component(
	configurationPid = "com.liferay.redirect.internal.configuration.RedirectConfiguration",
	service = SchedulerJobConfiguration.class
)
public class CheckRedirectNotFoundEntriesSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			_removeMaximumOverflowRedirectNotFoundEntries();
			_removeOldRedirectNotFoundEntries();
		};
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			_redirectConfiguration.checkRedirectNotFoundEntriesInterval(),
			TimeUnit.HOUR);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_redirectConfiguration = ConfigurableUtil.createConfigurable(
			RedirectConfiguration.class, properties);
	}

	private void _removeMaximumOverflowRedirectNotFoundEntries()
		throws Exception {

		int redirectNotFoundEntriesCount =
			_redirectNotFoundEntryLocalService.
				getRedirectNotFoundEntriesCount();

		int maximumNumberOfRedirectNotFoundEntries =
			_redirectConfiguration.maximumNumberOfRedirectNotFoundEntries();

		if (redirectNotFoundEntriesCount <
				maximumNumberOfRedirectNotFoundEntries) {

			return;
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_redirectNotFoundEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.setLimit(
				maximumNumberOfRedirectNotFoundEntries,
				redirectNotFoundEntriesCount));

		actionableDynamicQuery.setAddOrderCriteriaMethod(
			dynamicQuery -> dynamicQuery.addOrder(
				OrderFactoryUtil.desc("modifiedDate")));
		actionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<RedirectNotFoundEntry>)
				_redirectNotFoundEntryLocalService::
					deleteRedirectNotFoundEntry);

		actionableDynamicQuery.performActions();
	}

	private void _removeOldRedirectNotFoundEntries() throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_redirectNotFoundEntryLocalService.getActionableDynamicQuery();

		int redirectNotFoundEntryMaxAge =
			_redirectConfiguration.redirectNotFoundEntryMaxAge();

		Date thresholdDate = new Date(
			System.currentTimeMillis() -
				(redirectNotFoundEntryMaxAge * Time.DAY));

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.lt("modifiedDate", thresholdDate)));

		actionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<RedirectNotFoundEntry>)
				_redirectNotFoundEntryLocalService::
					deleteRedirectNotFoundEntry);

		actionableDynamicQuery.performActions();
	}

	private volatile RedirectConfiguration _redirectConfiguration;

	@Reference
	private RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

}