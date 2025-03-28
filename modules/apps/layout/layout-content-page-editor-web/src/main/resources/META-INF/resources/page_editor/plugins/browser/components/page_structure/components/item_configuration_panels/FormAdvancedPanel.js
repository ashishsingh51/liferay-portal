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

import React from 'react';

import {HideFromSearchField} from '../../../../../../app/components/fragment_configuration_fields/HideFromSearchField';
import {getLayoutDataItemPropTypes} from '../../../../../../prop_types/index';
import CSSFieldSet from './CSSFieldSet';

export default function FormAdvancedPanel({item}) {
	return (
		<>
			<HideFromSearchField item={item} />
			<CSSFieldSet item={item} />
		</>
	);
}

FormAdvancedPanel.propTypes = {
	item: getLayoutDataItemPropTypes().isRequired,
};
