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

package com.liferay.account.internal.upgrade.registry;

import com.liferay.account.internal.upgrade.v1_1_0.SchemaUpgradeProcess;
import com.liferay.account.internal.upgrade.v2_3_0.AccountResourceUpgradeProcess;
import com.liferay.account.internal.upgrade.v2_4_0.AccountGroupResourceUpgradeProcess;
import com.liferay.account.internal.upgrade.v2_5_0.AccountRoleResourceUpgradeProcess;
import com.liferay.account.internal.upgrade.v2_7_1.AccountEntryUserRelUpgradeProcess;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = UpgradeStepRegistrator.class)
public class AccountServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.0.1",
			new com.liferay.account.internal.upgrade.v1_0_1.
				RoleUpgradeProcess());

		registry.register(
			"1.0.1", "1.0.2",
			new com.liferay.account.internal.upgrade.v1_0_2.
				RoleUpgradeProcess());

		registry.register(
			"1.0.2", "1.0.3",
			new com.liferay.account.internal.upgrade.v1_0_3.
				RoleUpgradeProcess());

		registry.register(
			"1.0.3", "1.1.0",
			new com.liferay.account.internal.upgrade.v1_1_0.
				AccountEntryUpgradeProcess(),
			new SchemaUpgradeProcess());

		registry.register(
			"1.1.0", "1.1.1",
			new com.liferay.account.internal.upgrade.v1_1_1.
				AccountEntryUpgradeProcess());

		registry.register("1.1.1", "1.2.0", new DummyUpgradeStep());

		registry.register(
			"1.2.0", "1.2.1",
			new com.liferay.account.internal.upgrade.v1_2_1.
				RoleUpgradeProcess());

		registry.register(
			"1.2.1", "1.3.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "defaultBillingAddressId LONG",
				"defaultShippingAddressId LONG",
				"emailAddress VARCHAR(254) null",
				"taxExemptionCode VARCHAR(75) null"),
			UpgradeProcessFactory.addColumns(
				"AccountGroup", "defaultAccountGroup BOOLEAN"));

		registry.register(
			"1.3.0", "2.0.0",
			new com.liferay.account.internal.upgrade.v2_0_0.
				AccountGroupAccountEntryRelUpgradeProcess());

		registry.register(
			"2.0.0", "2.1.0",
			new com.liferay.account.internal.upgrade.v2_1_0.
				AccountGroupUpgradeProcess());

		registry.register(
			"2.1.0", "2.2.0",
			new com.liferay.account.internal.upgrade.v2_2_0.
				AccountGroupRelUpgradeProcess(_companyLocalService));

		registry.register(
			"2.2.0", "2.3.0", new AccountResourceUpgradeProcess());

		registry.register(
			"2.3.0", "2.4.0",
			new AccountGroupResourceUpgradeProcess(_resourceLocalService));

		registry.register(
			"2.4.0", "2.5.0",
			new AccountRoleResourceUpgradeProcess(_resourceLocalService));

		registry.register(
			"2.5.0", "2.6.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "defaultDeliveryCTermEntryId LONG",
				"defaultPaymentCTermEntryId LONG"));

		registry.register(
			"2.6.0", "2.7.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "defaultCPaymentMethodKey VARCHAR(75)"));

		registry.register(
			"2.7.0", "2.7.1", new AccountEntryUserRelUpgradeProcess());

		registry.register(
			"2.7.1", "2.8.0",
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"AccountEntry", "accountEntryId"},
						{"AccountGroup", "accountGroupId"}
					};
				}

			});

		registry.register(
			"2.8.0", "2.8.1",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"AccountEntry", "accountEntryId"},
						{"AccountGroup", "accountGroupId"}
					};
				}

			});

		registry.register(
			"2.8.1", "2.9.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "restrictMembership BOOLEAN"));

		registry.register(
			"2.9.0", "2.10.0",
			UpgradeProcessFactory.addColumns(
				"AccountEntry", "statusByUserId LONG",
				"statusByUserName VARCHAR(75) null", "statusDate DATE null"));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

}