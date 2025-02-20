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

import PropTypes from 'prop-types';
import React, {useRef, useState} from 'react';

import {SIZES, Size} from '../constants/sizes';
import Preview from './Preview';
import SizeSelector from './SizeSelector';

import '../../css/main.scss';

interface IProps {
	portletNamespace: string;
}

export default function App({portletNamespace: namespace}: IProps) {
	const [activeSize, setActiveSize] = useState<Size>(SIZES.desktop);

	const previewRef = useRef<HTMLDivElement>(null);

	return (
		<>
			<SizeSelector
				activeSize={activeSize}
				namespace={namespace}
				previewRef={previewRef}
				setActiveSize={setActiveSize}
			/>

			<Preview activeSize={activeSize} previewRef={previewRef} />
		</>
	);
}

App.propTypes = {
	portletNamespace: PropTypes.string.isRequired,
};
