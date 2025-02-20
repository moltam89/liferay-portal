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

package com.liferay.portal.search.index;

import com.liferay.portal.kernel.search.Document;

import java.util.Collection;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author André de Oliveira
 */
@ProviderType
public interface UpdateDocumentIndexWriter {

	public void updateDocument(long companyId, Document document);

	public void updateDocumentPartially(
		long companyId, Document document, boolean commitImmediately);

	public void updateDocuments(
		long companyId, Collection<Document> documents,
		boolean commitImmediately);

	public void updateDocumentsPartially(
		long companyId, Collection<Document> documents,
		boolean commitImmediately);

}