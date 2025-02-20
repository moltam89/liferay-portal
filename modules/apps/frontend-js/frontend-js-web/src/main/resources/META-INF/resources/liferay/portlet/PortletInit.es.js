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

import {v4 as uuidv4} from 'uuid';

import fetch from './../util/fetch.es';
import isObject from './../util/is_object';
import RenderState from './RenderState.es';
import PortletConstants from './portlet_constants.es';
import {
	decodeUpdateString,
	generateActionUrl,
	getUpdatedPublicRenderParameters,
	getUrl,
	validateArguments,
	validateForm,
	validateParameters,
	validateState,
} from './portlet_util.es';

/**
 * Flag specifying whether history is to be processed
 * (true if browser supports HTML5 session history APIs)
 * @type {boolean}
 * @review
 */

const doHistory = window.history && window.history.pushState;

/**
 * Regex for portlet-level events
 * @type {string}
 * @review
 */

const portletRegex = '^portlet[.].*';

/**
 * Determines if blocking action is currently in process.
 * @type {boolean}
 * @review
 */

let busy = false;

/**
 * An array containing the event listeners.
 * @type {Array.<Object>}
 * @review
 */

const eventListeners = {};

/**
 * An array containing the event listeners currently queued for being dispatched.
 * @type {Array.<Object>}
 * @review
 */

const eventListenersQueue = [];

/**
 * The page render state containing the all portlets and public parameters map.
 * @type {Object}
 * @review
 */

let pageRenderState;

/**
 * PortletInit
 * @review
 */

class PortletInit {
	constructor(portletId) {
		this._portletId = portletId;

		this.constants = {...PortletConstants};

		if (!pageRenderState) {
			pageRenderState = global.portlet.data.pageRenderState;

			this._updateHistory(true);
		}

		this.portletModes = pageRenderState.portlets[
			this._portletId
		].allowedPM.slice(0);

		this.windowStates = pageRenderState.portlets[
			this._portletId
		].allowedWS.slice(0);
	}

	/**
	 * Performs the actual action.
	 * @param {Object} parameters Additional parameters to be set.
	 * @param {HTMLFormElement} element Form to be submitted. May be <code>null</code>.
	 * @protected
	 * @review
	 */

	_executeAction(parameters, element) {
		return new Promise((resolve, reject) => {
			getUrl(pageRenderState, 'ACTION', this._portletId, parameters).then(
				(url) => {
					const options = generateActionUrl(
						this._portletId,
						url,
						element
					);

					fetch(options.url, options)
						.then((res) => res.text())
						.then((text) => {
							const updatedIds = this._updatePageStateFromString(
								text,
								this._portletId
							);

							resolve(updatedIds);
						})
						.catch((error) => {
							reject(error);
						});
				}
			);
		});
	}

	/**
	 * Returns true if an onStateChange listener is registered for the portlet.
	 * @memberof PortletInit
	 * @param {string} portletId The portlet ID.
	 * @return {boolean} Returns true if a listener is registered.
	 * @protected
	 * @review
	 */

	_hasListener(portletId) {
		const eventListenerPortletIds = Object.keys(eventListeners).map(
			(key) => eventListeners[key].id
		);

		return eventListenerPortletIds.includes(portletId);
	}

	/**
	 * Sends an onError event to all registered error event handlers for a given
	 * portlet.
	 * @memberof PortletInit
	 * @param {string} portletId The portlet ID.
	 * @param {string} err The error message.
	 * @protected
	 * @review
	 */

	_reportError(portletId, error) {
		Object.keys(eventListeners).map((key) => {
			const listener = eventListeners[key];

			if (
				listener.id === portletId &&
				listener.type === 'portlet.onError'
			) {
				setTimeout(() => {
					listener.handler('portlet.onError', error);
				});
			}

			return false;
		});
	}

	/**
	 * Callback function that must be called after a partial action has been
	 * started.
	 *
	 * The page state is generated by the portal and transmitted to the client by
	 * the portlet. The portlet client that initiated the partial action must
	 * pass the page state string to this function.
	 *
	 * The callback should only be called once to conclude a partial action sequence.
	 * @memberof PortletInit
	 * @param {string} portletId The portlet ID.
	 * @param {string} updateString The new page state in string form.
	 * @throws {TypeError} Thrown if the parameter is not a string.
	 * @protected
	 * @review
	 */

	_setPageState(portletId, updateString) {
		if (typeof updateString !== 'string') {
			throw new TypeError(`Invalid update string: ${updateString}`);
		}

		this._updatePageState(updateString, portletId).then(
			(updatedIds) => {
				this._updatePortletStates(updatedIds);
			},
			(error) => {
				busy = false;

				this._reportError(portletId, error);
			}
		);
	}

	/**
	 * Sets state for the portlet.
	 * returns array of IDs for portlets that were affected by the change,
	 * taking into account the public render parameters.
	 * @memberof PortletInit
	 * @param {Object} state The state to be set.
	 * @return {Array}
	 * @protected
	 * @review
	 */

	_setState(state) {
		const publicRenderParameters = getUpdatedPublicRenderParameters(
			pageRenderState,
			this._portletId,
			state
		);

		const updatedIds = [];

		const parameterKeys = Object.keys(publicRenderParameters);

		parameterKeys.forEach((parameterKey) => {
			const newValue = publicRenderParameters[parameterKey];

			const groupMap = pageRenderState.prpMap[parameterKey];

			const groupKeys = Object.keys(groupMap);

			groupKeys.forEach((groupKey) => {
				if (groupKey !== this._portletId) {
					const parts = groupMap[groupKey].split('|');

					const portletId = parts[0];

					const parameterName = parts[1];

					if (newValue === undefined) {
						delete pageRenderState.portlets[portletId].state
							.parameters[parameterName];
					}
					else {
						pageRenderState.portlets[portletId].state.parameters[
							parameterName
						] = [...newValue];
					}

					updatedIds.push(portletId);
				}
			});
		});

		const portletId = this._portletId;

		// Update state for the initiating portlet.

		pageRenderState.portlets[portletId].state = state;
		updatedIds.push(portletId);

		// Delete render data for all affected portlets in order to avoid dispatching
		// stale render data

		updatedIds.forEach((updatedId) => {
			pageRenderState.portlets[updatedId].renderData.content = null;
		});

		// Update history for back-button support

		this._updateHistory();

		return Promise.resolve(updatedIds);
	}

	/**
	 * Sets up for the action.
	 * @memberof PortletInit
	 * @param {Object} parameters Additional parameters. May be <code>null</code>.
	 * @param {HTMLFormElement} element Form to be submitted May be <code>null</code>.
	 * @throws {AccessDeniedException} Thrown if a blocking operation is already in progress.
	 * @throws {NotInitializedException} Thrown if a portlet ID is provided, but no onStateChange
	 * listener has been registered.
	 * @protected
	 * @review
	 */

	_setupAction(parameters, element) {
		if (this.isInProgress()) {
			throw {
				message: 'Operation is already in progress',
				name: 'AccessDeniedException',
			};
		}

		if (!this._hasListener(this._portletId)) {
			throw {
				message: `No onStateChange listener registered for portlet: ${this._portletId}`,
				name: 'NotInitializedException',
			};
		}

		busy = true;

		return this._executeAction(parameters, element).then(
			(updatedIds) => {
				return this._updatePortletStates(updatedIds).then(
					(updatedIds) => {
						busy = false;

						return updatedIds;
					}
				);
			},
			(error) => {
				busy = false;

				this._reportError(this._portletId, error);
			}
		);
	}

	/**
	 * Called when the page state has been updated to allow
	 * the browser history to be taken care of.
	 * @memberof PortletInit
	 * @param {boolean} replace Replace the state rather than pushing.
	 * @protected
	 * @review
	 */

	_updateHistory(replace) {
		if (doHistory) {
			getUrl(pageRenderState, 'RENDER', null, {}).then((url) => {
				const token = JSON.stringify(pageRenderState);

				if (replace) {
					history.replaceState(token, '');
				}
				else {
					try {
						history.pushState(token, '', url);
					}
					catch (error) {

						// Do nothing

					}
				}
			});
		}
	}

	/**
	 * Update page state passed in after partial action. The list of
	 * ID's of updated portlets is passed back through a promise in order
	 * to decouple the layers.
	 *
	 * @memberof PortletInit
	 * @param {string} updateString The updated render state string.
	 * @review
	 */

	_updatePageState(updateString) {
		return new Promise((resolve, reject) => {
			try {
				const updatedIds = this._updatePageStateFromString(
					updateString,
					this._portletId
				);

				resolve(updatedIds);
			}
			catch (error) {
				reject(
					new Error(`Partial Action decode status: ${error.message}`)
				);
			}
		});
	}

	/**
	 * Updates page state from string and returns array of portlet IDs
	 * to be updated.
	 *
	 * @memberof PortletInit
	 * @param {string} updateString The update string.
	 * @param {string} portletId The portlet ID.
	 * @protected
	 * @review
	 */

	_updatePageStateFromString(updateString, portletId) {
		const portlets = decodeUpdateString(pageRenderState, updateString);
		const updatedIds = [];

		let stateUpdated = false;

		// Update portlets and collect IDs of affected portlets.

		const entries = Object.entries(portlets);

		entries.forEach(([key, portletData]) => {
			pageRenderState.portlets[key] = portletData;
			updatedIds.push(key);
			stateUpdated = true;
		});

		// portletId will be null or undefined when called from onpopstate routine.
		// In that case, don't update history.

		if (stateUpdated && portletId) {
			this._updateHistory();
		}

		return updatedIds;
	}

	/**
	 *
	 * Accepts an object containing changed render states.
	 * Updates the state for each portlet present.
	 *
	 * @memberof PortletInit
	 * @param {Array} updatedIds Array of portlet IDs to be updated.
	 * @return {Promsise.<string>}
	 * @protected
	 * @review
	 */

	_updatePortletStates(updatedIds) {
		return new Promise((resolve) => {
			if (!updatedIds.length) {
				busy = false;
			}
			else {
				updatedIds.forEach((updatedId) => {
					this._updateStateForPortlet(updatedId);
				});
			}

			resolve(updatedIds);
		});
	}

	/**
	 * Updates the page render state
	 *
	 * @memberof PortletInit
	 * @param {Object} state The new state to be set.
	 * @protected
	 * @review
	 */

	_updateState(state) {
		if (busy) {
			throw {
				message: 'Operation in progress',
				name: 'AccessDeniedException',
			};
		}
		else if (!this._hasListener(this._portletId)) {
			throw {
				message: `No onStateChange listener registered for portlet: ${this._portletId}`,
				name: 'NotInitializedException',
			};
		}

		busy = true;

		this._setState(state)
			.then((updatedIds) => {
				this._updatePortletStates(updatedIds);
			})
			.catch((error) => {
				busy = false;
				this._reportError(this._portletId, error);
			});
	}

	/**
	 * Calls the portlet onStateChange method in an asynchronous manner in order
	 * to decouple the public API. This method is intended for use after
	 * portlet client registers an onStateChange listener.
	 *
	 * @memberof PortletInit
	 * @param {string} portletId The portlet ID.
	 * @protected
	 * @review
	 */

	_updateStateForPortlet(portletId) {
		const updateQueueIds = eventListenersQueue.map((item) => item.handle);

		const entries = Object.entries(eventListeners);

		entries.forEach(([key, eventData]) => {
			if (eventData.type !== 'portlet.onStateChange') {
				return;
			}

			if (eventData.id === portletId && !updateQueueIds.includes(key)) {
				eventListenersQueue.push(eventData);
			}
		});

		if (eventListenersQueue.length) {
			setTimeout(() => {
				busy = true;

				while (eventListenersQueue.length) {
					const eventData = eventListenersQueue.shift();
					const handler = eventData.handler;
					const id = eventData.id;

					if (!pageRenderState.portlets[id]) {
						continue;
					}

					const renderData = pageRenderState.portlets[id].renderData;
					const renderState = new RenderState(
						pageRenderState.portlets[id].state
					);

					if (renderData && renderData.content) {
						handler(
							'portlet.onStateChange',
							renderState,
							renderData
						);
					}
					else {
						handler('portlet.onStateChange', renderState);
					}
				}

				busy = false;
			});
		}
	}

	/**
	 * Initiates a portlet action using the specified action parameters and
	 * element arguments.
	 *
	 * @memberof PortletInit
	 * @param {PortletParameters} parameters Action parameters to be added to the URL
	 * @param {HTMLFormElement} element DOM element of form to be submitted
	 * @return {Promise} A Promise object that is resolved with no argument when the action request has completed.
	 * @throws {TypeError} Thrown if the input parameters are invalid
	 * @throws {AccessDeniedException} Thrown if a blocking operation is already in progress.
	 * @throws {NotInitializedException} Thrown if a portlet ID is provided, but no onStateChange listener has been registered.
	 * @review
	 */

	action(...args) {
		let actionParameters = null;
		let argCount = 0;
		let element = null;

		args.forEach((arg) => {
			if (arg instanceof HTMLFormElement) {
				if (element !== null) {
					throw new TypeError(
						`Too many [object HTMLFormElement] arguments: ${arg}, ${element}`
					);
				}

				element = arg;
			}
			else if (isObject(arg)) {
				validateParameters(arg);

				if (actionParameters !== null) {
					throw new TypeError('Too many parameters arguments');
				}

				actionParameters = arg;
			}
			else if (arg !== undefined) {
				const type = Object.prototype.toString.call(arg);

				throw new TypeError(
					`Invalid argument type. Argument ${
						argCount + 1
					} is of type ${type}`
				);
			}
			argCount++;
		});

		if (element) {
			validateForm(element);
		}

		return this._setupAction(actionParameters, element)
			.then((val) => {
				Promise.resolve(val);
			})
			.catch((error) => {
				Promise.reject(error);
			});
	}

	/**
	 * Adds a listener function for specified event type.
	 *
	 * @memberof PortletInit
	 * @param {string} type The type of listener
	 * @param {function} handler Function called when event occurs
	 * @return {Object} A handle that can be used to remove the event listener
	 * @throws {TypeError} Thrown if the input parameters are invalid
	 * @review
	 */

	addEventListener(type, handler) {
		if (arguments.length > 2) {
			throw new TypeError(
				'Too many arguments passed to addEventListener'
			);
		}

		if (typeof type !== 'string' || typeof handler !== 'function') {
			throw new TypeError('Invalid arguments passed to addEventListener');
		}

		const id = this._portletId;

		if (type.startsWith('portlet.')) {
			if (
				type !== 'portlet.onStateChange' &&
				type !== 'portlet.onError'
			) {
				throw new TypeError(
					`The system event type is invalid: ${type}`
				);
			}
		}

		const handle = uuidv4();

		const listener = {
			handle,
			handler,
			id,
			type,
		};

		eventListeners[handle] = listener;

		if (type === 'portlet.onStateChange') {
			this._updateStateForPortlet(this._portletId);
		}

		return handle;
	}

	/**
	 * Returns a promise for a resource URL with parameters set appropriately
	 * for the page state according to the resource parameters, cacheability
	 * option, and resource ID provided.
	 * @memberof PortletInit
	 * @param {Object} parameters Resource parameters to be added to the URL
	 * @param {string} cache Cacheability option. The strings defined under
	 * {@link PortletConstants} should be used to specifiy cacheability.
	 * @param {string} resourceId Resource ID.
	 * @return {Promise} A Promise object. Returns a string representing the
	 * resource URL on successful resolution. Returns an Error object containing
	 * a descriptive message on failure.
	 * @throws {TypeError} Thrown if the input parameters are invalid
	 * @review
	 */

	createResourceUrl(parameters, cache, resourceId) {
		if (arguments.length > 3) {
			throw new TypeError('Too many arguments. 3 arguments are allowed.');
		}

		if (parameters) {
			if (isObject(parameters)) {
				validateParameters(parameters);
			}
			else {
				throw new TypeError(
					'Invalid argument type. Resource parameters must be a parameters object.'
				);
			}
		}

		let cacheability = null;

		if (cache) {
			if (typeof cache === 'string') {
				if (
					cache === 'cacheLevelPage' ||
					cache === 'cacheLevelPortlet' ||
					cache === 'cacheLevelFull'
				) {
					cacheability = cache;
				}
				else {
					throw new TypeError(
						`Invalid cacheability argument: ${cache}`
					);
				}
			}
			else {
				throw new TypeError(
					'Invalid argument type. Cacheability argument must be a string.'
				);
			}
		}

		if (!cacheability) {
			cacheability = 'cacheLevelPage';
		}

		if (resourceId && typeof resourceId !== 'string') {
			throw new TypeError(
				'Invalid argument type. Resource ID argument must be a string.'
			);
		}

		return getUrl(
			pageRenderState,
			'RESOURCE',
			this._portletId,
			parameters,
			cacheability,
			resourceId
		);
	}

	/**
	 * Dispatches a client event.
	 * @memberof PortletInit
	 * @param {string} type The type of listener.
	 * @param {any} payload The payload to be delivered.
	 * @return {number} The number of events queued for delivery.
	 * @throws {TypeError} Thrown if the type is a system event type.
	 * @review
	 */

	dispatchClientEvent(type, payload) {
		validateArguments(arguments, 2, 2, ['string']);

		if (type.match(new RegExp(portletRegex))) {
			throw new TypeError('The event type is invalid: ' + type);
		}

		return Object.keys(eventListeners).reduce((amount, key) => {
			const listener = eventListeners[key];

			if (type.match(listener.type)) {
				listener.handler(type, payload);
				amount++;
			}

			return amount;
		}, 0);
	}

	/**
	 * Tests whether a blocking operation is in progress.
	 * @memberof PortletInit
	 * @return {boolean}
	 * @review
	 */

	isInProgress() {
		return busy;
	}

	/**
	 * Creates and returns a new PortletParameters object.
	 * @memberof PortletInit
	 * @param {?Object} optParameters The optional parameters to be copied.
	 * @return {Object} The new parameters object.
	 * @review
	 */

	newParameters(optParameters = {}) {
		const newParameters = {};

		Object.keys(optParameters).forEach((key) => {
			if (Array.isArray(optParameters[key])) {
				newParameters[key] = [...optParameters[key]];
			}
		});

		return newParameters;
	}

	/**
	 * Creates and returns a new RenderState object.
	 * @memberof PortletInit
	 * @param {?RenderState} optState An optional RenderState object to be copied.
	 * @return {RenderState} The new RenderState object.
	 * @review
	 */

	newState(optState) {
		return new RenderState(optState);
	}

	/**
	 * Removes a previously added listener function designated by the handle.
	 * The handle must be the same object previously returned by the
	 * addEventListener function.
	 * @memberof PortletInit
	 * @param {Object} handle The handle of the listener to be removed.
	 * @throws {TypeError} Thrown if the input parameters are invalid.
	 * @throws {AccessDeniedException} Thrown if the event listener associated
	 * with this handle was registered by a different portlet.
	 * @review
	 */

	removeEventListener(handle) {
		if (arguments.length > 1) {
			throw new TypeError(
				'Too many arguments passed to removeEventListener'
			);
		}

		if (handle === undefined || handle === null) {
			throw new TypeError(
				`The event handle provided is ${typeof handle}`
			);
		}

		let found = false;

		if (isObject(eventListeners[handle])) {
			if (eventListeners[handle].id === this._portletId) {
				delete eventListeners[handle];

				const l = eventListenersQueue.length;

				for (let i = 0; i < l; i++) {
					const eventData = eventListenersQueue[i];

					if (eventData && eventData.handle === handle) {
						eventListenersQueue.splice(i, 1);
					}
				}

				found = true;
			}
		}

		if (!found) {
			throw new TypeError(
				"The event listener handle doesn't match any listeners."
			);
		}
	}

	/**
	 * Sets the render state, which consists of the public and private render
	 * parameters, the portlet mode, and the window state.
	 * @memberof PortletInit
	 * @param {RenderState} state The new state to be set.
	 * @throws {TypeError} Thrown if the input parameters are invalid.
	 * @throws {AccessDeniedException} Thrown if a blocking operation is already in progress.
	 * @throws {NotInitializedException} Thrown if a portlet ID is provided, but no onStateChange
	 * listener has been registered.
	 * @review
	 */

	setRenderState(state) {
		validateArguments(arguments, 1, 1, ['object']);

		if (
			pageRenderState.portlets &&
			pageRenderState.portlets[this._portletId]
		) {
			const portletData = pageRenderState.portlets[this._portletId];

			validateState(state, portletData);
			this._updateState(state);
		}
	}

	/**
	 * Starts partial action processing and returns a {@link PartialActionInit} object to the caller.
	 * @memberof PortletInit
	 * @param {PortletParameters} actionParameters Action parameters to be added to the URL.
	 * @return {Promise} A Promise. Returns a {@link PortletActionInit} object
	 * containing a partial action URL and the _setPageState callback function
	 * on successful resolution. Returns an Error object containing a
	 * descriptive message on failure.
	 * @throws {TypeError} Thrown if the input parameters are invalid.
	 * @throws {AccessDeniedException} Thrown if a blocking operation is already in progress.
	 * @throws {NotInitializedException} Thrown if a portlet ID is provided, but
	 * no onStateChange listener has been registered.
	 * @review
	 */

	startPartialAction(actionParameters) {
		const instance = this;

		let parameters = null;

		if (arguments.length > 1) {
			throw new TypeError('Too many arguments. 1 arguments are allowed');
		}
		else if (actionParameters !== undefined) {
			if (isObject(actionParameters)) {
				validateParameters(actionParameters);

				parameters = actionParameters;
			}
			else {
				throw new TypeError(
					`Invalid argument type. Argument is of type ${typeof actionParameters}`
				);
			}
		}

		if (busy === true) {
			throw {
				message: 'Operation in progress',
				name: 'AccessDeniedException',
			};
		}
		else if (!this._hasListener(this._portletId)) {
			throw {
				message: `No onStateChange listener registered for portlet: ${this._portletId}`,
				name: 'NotInitializedException',
			};
		}

		busy = true;

		const partialActionInitObject = {
			setPageState(updateString) {
				instance._setPageState(instance._portletId, updateString);
			},
			url: '',
		};

		return getUrl(
			pageRenderState,
			'PARTIAL_ACTION',
			this._portletId,
			parameters
		).then((url) => {
			partialActionInitObject.url = url;

			return partialActionInitObject;
		});
	}
}

export {PortletInit};
export default PortletInit;
