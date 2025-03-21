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
import React, {useCallback} from 'react';

import {HideFromSearchField} from '../../../../../../app/components/fragment_configuration_fields/HideFromSearchField';
import {FRAGMENT_CONFIGURATION_ROLES} from '../../../../../../app/config/constants/fragmentConfigurationRoles';
import {config} from '../../../../../../app/config/index';
import {
	useDispatch,
	useSelector,
	useSelectorCallback,
	useSelectorRef,
} from '../../../../../../app/contexts/StoreContext';
import selectLanguageId from '../../../../../../app/selectors/selectLanguageId';
import getFragmentConfigurationValues from '../../../../../../app/utils/getFragmentConfigurationValues';
import updateConfigurationValue from '../../../../../../app/utils/updateConfigurationValue';
import {getLayoutDataItemPropTypes} from '../../../../../../prop_types/index';
import CSSFieldSet from './CSSFieldSet';
import {FieldSet} from './FieldSet';

export function FragmentAdvancedPanel({item}) {
	const dispatch = useDispatch();

	const fragmentEntryLink = useSelectorCallback(
		(state) => state.fragmentEntryLinks[item.config.fragmentEntryLinkId],
		[item.config.fragmentEntryLinkId]
	);
	const languageId = useSelector(selectLanguageId);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const fragmentEntryLinksRef = useSelectorRef(
		(state) => state.fragmentEntryLinks
	);

	const onConfigurationValueSelect = useCallback(
		(name, value) => {
			updateConfigurationValue({
				configuration: fragmentEntryLink.configuration,
				dispatch,
				fragmentEntryLink,
				languageId,
				name,
				value,
			});
		},
		[dispatch, fragmentEntryLink, languageId]
	);

	const fieldSets = fragmentEntryLink.configuration?.fieldSets?.filter(
		(fieldSet) =>
			fieldSet.configurationRole === FRAGMENT_CONFIGURATION_ROLES.advanced
	);

	return (
		<>
			<h1 className="sr-only">
				{Liferay.Language.get('advanced-configuration')}
			</h1>

			{fieldSets?.length > 0 &&
				fieldSets.map((fieldSet, index) => {
					return (
						<FieldSet
							description={fieldSet.description}
							fields={fieldSet.fields}
							fragmentEntryLinks={fragmentEntryLinksRef.current}
							key={index}
							label={fieldSet.label}
							languageId={config.defaultLanguageId}
							onValueSelect={onConfigurationValueSelect}
							selectedViewportSize={selectedViewportSize}
							values={getFragmentConfigurationValues(
								fragmentEntryLink
							)}
						/>
					);
				})}

			<HideFromSearchField item={item} />

			<CSSFieldSet item={item} />
		</>
	);
}

FragmentAdvancedPanel.propTypes = {
	item: getLayoutDataItemPropTypes({
		config: PropTypes.shape({
			fragmentEntryLinkId: PropTypes.string.isRequired,
		}).isRequired,
	}),
};
