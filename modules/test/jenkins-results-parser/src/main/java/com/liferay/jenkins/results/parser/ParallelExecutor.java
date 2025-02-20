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

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Peter Yoo
 */
public class ParallelExecutor<T> {

	public ParallelExecutor(
		Collection<Callable<T>> callables, boolean excludeNulls,
		ExecutorService executorService) {

		_callables = callables;
		_excludeNulls = excludeNulls;

		_executorService = executorService;

		if (executorService == null) {
			_disposeExecutor = true;
			_executorService = Executors.newSingleThreadExecutor();
		}
		else {
			_disposeExecutor = false;
		}
	}

	public ParallelExecutor(
		Collection<Callable<T>> callables, ExecutorService executorService) {

		this(callables, false, executorService);
	}

	public List<T> execute() {
		return execute(null);
	}

	public List<T> execute(Long timeoutSeconds) {
		start();

		return waitFor(timeoutSeconds);
	}

	public void shutdownNow() {
		_executorService.shutdownNow();
	}

	public synchronized void start() {
		if (_futures != null) {
			return;
		}

		_futures = new ArrayList<>(_callables.size());

		for (Callable<T> callable : _callables) {
			_futures.add(_executorService.submit(callable));
		}
	}

	public List<T> waitFor() {
		return waitFor(null);
	}

	public List<T> waitFor(Long timeoutSeconds) {
		if (_futures == null) {
			start();
		}

		if (timeoutSeconds == null) {
			timeoutSeconds = 60L * 60L * 2L;
		}

		try {
			List<T> results = new ArrayList<>(_callables.size());

			for (Future<T> future : _futures) {
				try {
					T result = null;

					try {
						result = future.get(timeoutSeconds, TimeUnit.SECONDS);
					}
					catch (TimeoutException timeoutException) {
						System.out.println(
							JenkinsResultsParserUtil.combine(
								"Parallel executor thread timed out after ",
								JenkinsResultsParserUtil.toDurationString(
									timeoutSeconds * 1000),
								"\n", timeoutException.getMessage()));

						future.cancel(true);

						result = null;
					}

					if ((result == null) && _excludeNulls) {
						continue;
					}

					results.add(result);
				}
				catch (ExecutionException | InterruptedException exception) {
					throw new RuntimeException(exception);
				}
			}

			return results;
		}
		finally {
			if (_disposeExecutor) {
				_executorService.shutdownNow();

				while (!_executorService.isShutdown()) {
					JenkinsResultsParserUtil.sleep(100);
				}

				_executorService = null;
			}
		}
	}

	private final Collection<Callable<T>> _callables;
	private final boolean _disposeExecutor;
	private boolean _excludeNulls;
	private ExecutorService _executorService;
	private ArrayList<Future<T>> _futures;

}