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

package com.liferay.layout.utility.page.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutUtilityPageEntryNameException extends PortalException {

	public static class MustNotBeDuplicate
		extends LayoutUtilityPageEntryNameException {

		public MustNotBeDuplicate(long groupId, String name) {
			super(
				String.format(
					StringBundler.concat(
						"Duplicate utility page for group ", groupId,
						" with name ", name)));
		}

	}

	public static class MustNotBeNull
		extends LayoutUtilityPageEntryNameException {

		public MustNotBeNull(long groupId) {
			super("Name must not be null for group " + groupId);
		}

	}

	public static class MustNotContainInvalidCharacters
		extends LayoutUtilityPageEntryNameException {

		public MustNotContainInvalidCharacters(char character) {
			super("Invalid character in name " + character);

			this.character = character;
		}

		public final char character;

	}

	public static class MustNotExceedMaximumSize
		extends LayoutUtilityPageEntryNameException {

		public MustNotExceedMaximumSize(int maxLength) {
			super("Maximum length of name exceeded " + maxLength);
		}

	}

	private LayoutUtilityPageEntryNameException(String msg) {
		super(msg);
	}

}