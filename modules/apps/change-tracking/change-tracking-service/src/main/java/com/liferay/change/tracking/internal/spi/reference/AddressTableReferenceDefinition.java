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

package com.liferay.change.tracking.internal.spi.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.AddressTable;
import com.liferay.portal.kernel.model.PhoneTable;
import com.liferay.portal.kernel.service.persistence.AddressPersistence;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tamas Molnar
 */
@Component(service = TableReferenceDefinition.class)
public class AddressTableReferenceDefinition
	implements TableReferenceDefinition<AddressTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<AddressTable>
			childTableReferenceInfoBuilder) {

		childTableReferenceInfoBuilder.classNameReference(
			AddressTable.INSTANCE.addressId, PhoneTable.INSTANCE.classPK,
			Address.class);
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<AddressTable>
			parentTableReferenceInfoBuilder) {
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _addressPersistence;
	}

	@Override
	public AddressTable getTable() {
		return AddressTable.INSTANCE;
	}

	@Reference
	private AddressPersistence _addressPersistence;

}