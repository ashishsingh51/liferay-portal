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

import ClayLoadingIndicator from '@clayui/loading-indicator';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';

import {SelectField} from '../../../../../../app/components/fragment_configuration_fields/SelectField';
import {
	TargetCollectionsField,
	selectConfiguredCollectionDisplays,
} from '../../../../../../app/components/fragment_configuration_fields/TargetCollectionsField';
import {COMMON_STYLES_ROLES} from '../../../../../../app/config/constants/commonStylesRoles';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../app/config/constants/freemarkerFragmentEntryProcessor';
import {
	useDispatch,
	useSelector,
	useSelectorCallback,
} from '../../../../../../app/contexts/StoreContext';
import selectLanguageId from '../../../../../../app/selectors/selectLanguageId';
import CollectionService from '../../../../../../app/services/CollectionService';
import updateFragmentConfiguration from '../../../../../../app/thunks/updateFragmentConfiguration';
import {deepEqual} from '../../../../../../app/utils/checkDeepEqual';
import {getResponsiveConfig} from '../../../../../../app/utils/getResponsiveConfig';
import isEmptyArray from '../../../../../../app/utils/isEmptyArray';
import isEmptyObject from '../../../../../../app/utils/isEmptyObject';
import updateConfigurationValue from '../../../../../../app/utils/updateConfigurationValue';
import Collapse from '../../../../../../common/components/Collapse';
import getLayoutDataItemPropTypes from '../../../../../../prop_types/getLayoutDataItemPropTypes';
import {CommonStyles} from './CommonStyles';
import {FieldSet} from './FieldSet';

export function CollectionFilterGeneralPanel({item}) {
	const collections = useSelectorCallback(
		selectConfiguredCollectionDisplays,
		[],
		deepEqual
	);

	const [filterableCollections, setFilterableCollections] = useState(null);
	const [loading, setLoading] = useState(false);

	const selectedViewportSize = useSelector(
		(state) => state.selectedViewportSize
	);

	const itemConfig = getResponsiveConfig(item.config, selectedViewportSize);

	useEffect(() => {
		if (isEmptyArray(collections)) {
			setFilterableCollections({});

			return;
		}

		setLoading(true);

		CollectionService.getCollectionSupportedFilters(
			collections.map((item) => ({
				collectionId: item.itemId,
				layoutObjectReference: item.config?.collection,
			}))
		)
			.then((response) => {
				const nextFilterableCollections = {};

				collections
					.filter(
						(collection) =>
							!isEmptyArray(response[collection.itemId])
					)
					.forEach((collection) => {
						nextFilterableCollections[collection.itemId] = {
							...collection,
							supportedFilters: response[collection.itemId],
						};
					});

				setFilterableCollections(nextFilterableCollections);
			})
			.finally(() => setLoading(false));
	}, [collections]);

	if (loading) {
		return <ClayLoadingIndicator className="my-0" size="sm" />;
	}

	return (
		<>
			<div className="mb-3">
				{isEmptyObject(filterableCollections) ? (
					<p
						className="alert alert-info mt-2 text-center"
						role="alert"
					>
						{Liferay.Language.get(
							'display-a-collection-on-the-page-that-support-at-least-one-type-of-filter'
						)}
					</p>
				) : (
					<CollectionFilterGeneralPanelContent
						filterableCollections={filterableCollections}
						item={item}
					/>
				)}
			</div>

			<CommonStyles
				commonStylesValues={itemConfig.styles}
				item={item}
				role={COMMON_STYLES_ROLES.general}
			/>
		</>
	);
}

export function CollectionFilterGeneralPanelContent({
	filterableCollections,
	item,
}) {
	const dispatch = useDispatch();

	const fragmentEntryLink = useSelectorCallback(
		(state) => state.fragmentEntryLinks[item.config.fragmentEntryLinkId],
		[item.config.fragmentEntryLinkId]
	);

	const languageId = useSelector(selectLanguageId);

	const configurationValues =
		fragmentEntryLink.editableValues[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR] ||
		{};

	const [collectionFilters, setCollectionFilters] = useState(null);

	const selectedFilter = collectionFilters?.[configurationValues.filterKey];

	const targetCollections =
		configurationValues.targetCollections?.filter(
			(targetCollection) => filterableCollections[targetCollection]
		) ?? [];

	useEffect(() => {
		if (!collectionFilters) {
			CollectionService.getCollectionFilters().then(
				(collectionFilters) => {
					setCollectionFilters(collectionFilters);
				}
			);
		}
	}, [collectionFilters]);

	const onValueSelect = useCallback(
		(name, value) => {
			updateConfigurationValue({
				configuration: selectedFilter?.configuration,
				dispatch,
				fragmentEntryLink,
				languageId,
				name,
				value,
			});
		},
		[dispatch, selectedFilter, fragmentEntryLink, languageId]
	);

	return (
		<>
			<Collapse
				label={Liferay.Language.get('collection-filter-options')}
				open
			>
				<TargetCollectionsField
					enableCompatibleCollections
					filterableCollections={filterableCollections}
					onValueSelect={(name, value) => {
						if (!isEmptyArray(value)) {
							onValueSelect(name, value);
						}
						else {
							const nextConfigurationValues = {
								filterKey: '',
								[name]: value,
							};

							dispatch(
								updateFragmentConfiguration({
									configurationValues: nextConfigurationValues,
									fragmentEntryLink,
									languageId,
								})
							);
						}
					}}
					value={targetCollections}
				/>

				{!isEmptyArray(targetCollections) &&
					!isEmptyObject(collectionFilters) && (
						<SelectField
							field={{
								label: Liferay.Language.get('filter'),
								name: 'filterKey',
								typeOptions: {
									validValues: [
										{
											label: Liferay.Language.get('none'),
											value: '',
										},
										...filterSupportedFilters({
											collectionFilters: Object.values(
												collectionFilters
											),
											filterableCollections,
											targetCollections,
										}).map(({key, label}) => ({
											label,
											value: key,
										})),
									],
								},
							}}
							onValueSelect={onValueSelect}
							value={configurationValues.filterKey}
						/>
					)}
			</Collapse>

			{!isEmptyArray(targetCollections) &&
				selectedFilter?.configuration &&
				selectedFilter.configuration.fieldSets
					?.filter((fieldSet) => fieldSet.fields.length)
					.map((fieldSet, index) => (
						<div
							className="mt-3"
							key={`${fieldSet.label || ''}-${index}`}
						>
							<FieldSet
								description={fieldSet.description}
								fields={fieldSet.fields}
								label={fieldSet.label}
								languageId={languageId}
								onValueSelect={(name, value) =>
									onValueSelect(name, value)
								}
								values={configurationValues}
							/>
						</div>
					))}
		</>
	);
}

CollectionFilterGeneralPanel.propTypes = {
	item: getLayoutDataItemPropTypes({
		config: PropTypes.shape({
			fragmentEntryLinkId: PropTypes.string.isRequired,
		}).isRequired,
	}),
};

function filterSupportedFilters({
	collectionFilters,
	targetCollections = [],
	filterableCollections,
}) {
	if (isEmptyArray(targetCollections)) {
		return {};
	}

	const targetCollectionsSupportedFilters = targetCollections
		.map(
			(targetCollection) =>
				filterableCollections[targetCollection]?.supportedFilters
		)
		.filter(Boolean);

	return collectionFilters.filter(({key}) =>
		targetCollectionsSupportedFilters.every(
			(targetCollectionSupportedFilters) =>
				targetCollectionSupportedFilters.includes(key)
		)
	);
}
