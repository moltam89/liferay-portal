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

package com.liferay.petra.function.transform;

import com.liferay.petra.function.UnsafeFunction;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class TransformUtil {

	public static <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		try {
			return unsafeTransform(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> R[] transform(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction, Class<?> clazz) {

		try {
			return unsafeTransform(array, unsafeFunction, clazz);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> R[] transformToArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
		Class<?> clazz) {

		try {
			return unsafeTransformToArray(collection, unsafeFunction, clazz);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> int[] transformToIntArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		try {
			return unsafeTransformToIntArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <R, E extends Throwable> List<R> transformToList(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction) {

		try {
			return unsafeTransformToList(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <R, E extends Throwable> List<R> transformToList(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction) {

		try {
			return unsafeTransformToList(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> List<R> transformToList(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction) {

		try {
			return unsafeTransformToList(array, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> long[] transformToLongArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		try {
			return unsafeTransformToLongArray(collection, unsafeFunction);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T, R, E extends Throwable> List<R> unsafeTransform(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		if (collection == null) {
			return new ArrayList<>();
		}

		List<R> list = new ArrayList<>(collection.size());

		for (T item : collection) {
			R newItem = unsafeFunction.apply(item);

			if (newItem != null) {
				list.add(newItem);
			}
		}

		return list;
	}

	public static <T, R, E extends Throwable> R[] unsafeTransform(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction, Class<?> clazz)
		throws E {

		List<R> list = unsafeTransformToList(array, unsafeFunction);

		return list.toArray((R[])Array.newInstance(clazz, 0));
	}

	public static <T, R, E extends Throwable> R[] unsafeTransformToArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
			Class<?> clazz)
		throws E {

		List<R> list = unsafeTransform(collection, unsafeFunction);

		return list.toArray((R[])Array.newInstance(clazz, 0));
	}

	public static <T, R, E extends Throwable> int[] unsafeTransformToIntArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return (int[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, int[].class);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction)
		throws E {

		if (array == null) {
			return new ArrayList<>();
		}

		List<R> list = new ArrayList<>(array.length);

		for (Integer item : array) {
			R newItem = unsafeFunction.apply(item);

			if (newItem != null) {
				list.add(newItem);
			}
		}

		return list;
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction)
		throws E {

		if (array == null) {
			return new ArrayList<>();
		}

		List<R> list = new ArrayList<>(array.length);

		for (Long item : array) {
			R newItem = unsafeFunction.apply(item);

			if (newItem != null) {
				list.add(newItem);
			}
		}

		return list;
	}

	public static <T, R, E extends Throwable> List<R> unsafeTransformToList(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		if (array == null) {
			return new ArrayList<>();
		}

		List<R> list = new ArrayList<>(array.length);

		for (T item : array) {
			R newItem = unsafeFunction.apply(item);

			if (newItem != null) {
				list.add(newItem);
			}
		}

		return list;
	}

	public static <T, R, E extends Throwable> long[] unsafeTransformToLongArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return (long[])_unsafeTransformToPrimitiveArray(
			collection, unsafeFunction, long[].class);
	}

	private static <T, R, E extends Throwable> Object
			_unsafeTransformToPrimitiveArray(
				Collection<T> collection,
				UnsafeFunction<T, R, E> unsafeFunction, Class<?> clazz)
		throws E {

		List<R> list = unsafeTransform(collection, unsafeFunction);

		Object array = clazz.cast(
			Array.newInstance(clazz.getComponentType(), list.size()));

		for (int i = 0; i < list.size(); i++) {
			Array.set(array, i, list.get(i));
		}

		return array;
	}

}