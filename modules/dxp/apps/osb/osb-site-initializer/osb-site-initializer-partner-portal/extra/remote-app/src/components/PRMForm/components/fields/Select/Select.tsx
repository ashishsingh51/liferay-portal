/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import {ClaySelectWithOption} from '@clayui/form';
import React from 'react';

import WrapperInput from '../common/components/WrapperInput';
import PRMFormFieldProps from '../common/interfaces/prmFormFieldProps';
import PRMFormFieldStateProps from '../common/interfaces/prmFormFieldStateProps';

const Select = ({
	field,
	meta,
	label,
	options = [],
	required,
	...props
}: PRMFormFieldProps &
	React.ComponentProps<typeof ClaySelectWithOption> &
	PRMFormFieldStateProps<string>) => {
	const defaultOptions = {
		disabled: true,
		label: options.length ? 'Choose a option' : 'No options available',
		value: '',
	};

	return (
		<WrapperInput {...meta} label={label} required={required}>
			<ClaySelectWithOption
				options={[defaultOptions, ...options]}
				{...field}
				{...props}
				required={required}
			/>
		</WrapperInput>
	);
};

export default Select;
