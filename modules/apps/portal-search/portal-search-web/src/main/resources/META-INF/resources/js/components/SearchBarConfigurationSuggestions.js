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

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useModal} from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import {ClayTooltipProvider} from '@clayui/tooltip';
import getCN from 'classnames';
import {fetch, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import LearnMessage from '../shared/LearnMessage';
import cleanSuggestionsContributorConfiguration from '../utils/clean_suggestions_contributor_configuration';
import {CONTRIBUTOR_TYPES} from '../utils/types/contributorTypes';
import FieldList from './FieldList';
import SelectSXPBlueprintModal from './select_sxp_blueprint_modal/SelectSXPBlueprintModal';

const DEFAULT_ATTRIBUTES = {
	characterThreshold: '',
	fields: [],
	includeAssetSearchSummary: true,
	includeAssetURL: true,
	sxpBlueprintId: '',
};

/**
 * Cleans up the fields array by removing those that do not have the required
 * fields (contributorName, displayGroupName, size). If blueprint, check
 * for sxpBlueprintId as well.
 * @param {Array} fields The list of fields.
 * @return {Array} The cleaned up list of fields.
 */
const removeEmptyFields = (fields) =>
	fields.filter(({attributes, contributorName, displayGroupName, size}) => {
		if (contributorName === CONTRIBUTOR_TYPES.BASIC) {
			return displayGroupName && size;
		}

		return (
			contributorName &&
			displayGroupName &&
			size &&
			attributes?.sxpBlueprintId
		);
	});

function SXPBlueprintAttributes({onBlur, onChange, touched, value}) {
	const [showModal, setShowModal] = useState(false);
	const [sxpBlueprint, setSXPBlueprint] = useState({
		loading: false,
		title: '',
	});

	const [multiSelectValue, setMultiSelectValue] = useState('');
	const [multiSelectItems, setMultiSelectItems] = useState(
		(value.attributes?.fields || []).map((field) => ({
			label: field,
			value: field,
		}))
	);

	const {observer, onClose} = useModal({
		onClose: () => setShowModal(false),
	});

	useEffect(() => {

		// Fetch the blueprint title using sxpBlueprintId inside attributes, since
		// title is not saved within initialSuggestionsContributorConfiguration.

		if (value.attributes?.sxpBlueprintId) {
			setSXPBlueprint({loading: true, title: ''});

			fetch(
				`${window.location.origin}/o/search-experiences-rest/v1.0/sxp-blueprints/${value.attributes?.sxpBlueprintId}`,
				{
					headers: new Headers({
						'Accept': 'application/json',
						'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
						'Content-Type': 'application/json',
					}),
					method: 'GET',
				}
			)
				.then((response) =>
					response.json().then((data) => ({
						data,
						ok: response.ok,
					}))
				)
				.then(({data, ok}) => {
					setSXPBlueprint({
						loading: false,
						title:
							!ok || data.status === 'NOT_FOUND'
								? `${value.attributes?.sxpBlueprintId}`
								: data.title,
					});
				})
				.catch(() => {
					setSXPBlueprint({
						loading: false,
						title: `${value.attributes?.sxpBlueprintId}`,
					});
				});
		}
	}, []); //eslint-disable-line

	const _handleSXPBlueprintSelectorSubmit = (id, title) => {
		onChange({
			attributes: {
				...value.attributes,
				sxpBlueprintId: id,
			},
		});

		setSXPBlueprint({loading: false, title});
	};

	const _handleSXPBlueprintSelectorClickRemove = () => {
		_handleSXPBlueprintSelectorSubmit('', '');

		onBlur('sxpBlueprintId')();
	};

	const _handleSXPBlueprintSelectorClickSelect = () => {
		setShowModal(true);
	};

	const _handleSXPBlueprintSelectorChange = (event) => {

		// To use validation from 'required' field, keep the onChange and value
		// properties but make its behavior resemble readOnly (input can only be
		// changed with the selector modal).

		event.preventDefault();
	};

	const _handleChangeAttribute = (property) => (event) => {
		onChange({
			attributes: {...value.attributes, [property]: event.target.value},
		});
	};

	const _handleMultiSelectBlur = () => {
		if (multiSelectValue) {
			_handleMultiSelectChange([
				...multiSelectItems,
				{
					label: multiSelectValue,
					value: multiSelectValue,
				},
			]);

			setMultiSelectValue('');
		}
	};

	const _handleMultiSelectChange = (newValue) => {
		onChange({
			attributes: {
				...value.attributes,
				fields: newValue.map((item) => item.value),
			},
		});
		setMultiSelectItems(newValue);
	};

	return (
		<>
			{showModal && (
				<SelectSXPBlueprintModal
					observer={observer}
					onClose={onClose}
					onSubmit={_handleSXPBlueprintSelectorSubmit}
					selectedId={value.attributes?.sxpBlueprintId || ''}
				/>
			)}

			<div className="form-group-autofit">
				<ClayInput.GroupItem
					className={getCN({
						'has-error':
							!value.attributes?.sxpBlueprintId &&
							touched.sxpBlueprintId,
					})}
				>
					<label>
						{Liferay.Language.get('blueprint')}

						<span className="reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>
					</label>

					<div className="select-sxp-blueprint">
						{sxpBlueprint.loading ? (
							<div className="form-control" readOnly>
								<ClayLoadingIndicator small />
							</div>
						) : (
							<ClayInput
								onBlur={onBlur('sxpBlueprintId')}
								onChange={_handleSXPBlueprintSelectorChange}
								required
								type="text"
								value={sxpBlueprint.title}
							/>
						)}

						{sxpBlueprint.title && (
							<ClayButton
								className="remove-sxp-blueprint"
								displayType="secondary"
								onClick={_handleSXPBlueprintSelectorClickRemove}
								small
							>
								<ClayIcon symbol="times-circle" />
							</ClayButton>
						)}

						<ClayButton
							displayType="secondary"
							onClick={_handleSXPBlueprintSelectorClickSelect}
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</div>
				</ClayInput.GroupItem>
			</div>

			<div className="form-group-autofit">
				<ClayInput.GroupItem>
					<label>{Liferay.Language.get('character-threshold')}</label>

					<ClayInput
						aria-label={Liferay.Language.get('character-threshold')}
						min="0"
						onChange={_handleChangeAttribute('characterThreshold')}
						type="number"
						value={value.attributes?.characterThreshold || ''}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem>
					<label>
						{Liferay.Language.get('include-asset-url')}

						<ClayTooltipProvider>
							<span
								className="ml-2"
								data-tooltip-align="top"
								title={Liferay.Language.get(
									'include-asset-url-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<ClaySelect
						aria-label={Liferay.Language.get('include-asset-url')}
						onChange={_handleChangeAttribute('includeAssetURL')}
						value={value.attributes?.includeAssetURL}
					>
						<ClaySelect.Option
							label={Liferay.Language.get('true')}
							value={true}
						/>

						<ClaySelect.Option
							label={Liferay.Language.get('false')}
							value={false}
						/>
					</ClaySelect>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem>
					<label>
						{Liferay.Language.get('include-asset-summary')}

						<ClayTooltipProvider>
							<span
								className="ml-2"
								data-tooltip-align="top"
								title={Liferay.Language.get(
									'include-asset-summary-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<ClaySelect
						aria-label={Liferay.Language.get(
							'include-asset-summary'
						)}
						onChange={_handleChangeAttribute(
							'includeAssetSearchSummary'
						)}
						value={value.attributes?.includeAssetSearchSummary}
					>
						<ClaySelect.Option
							label={Liferay.Language.get('true')}
							value={true}
						/>

						<ClaySelect.Option
							label={Liferay.Language.get('false')}
							value={false}
						/>
					</ClaySelect>
				</ClayInput.GroupItem>
			</div>

			<div className="form-group-autofit">
				<ClayInput.GroupItem>
					<label>
						{Liferay.Language.get('fields')}

						<ClayTooltipProvider>
							<span
								className="ml-2"
								data-tooltip-align="top"
								title={Liferay.Language.get(
									'fields-suggestion-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<ClayMultiSelect
						items={multiSelectItems}
						onBlur={_handleMultiSelectBlur}
						onChange={setMultiSelectValue}
						onItemsChange={_handleMultiSelectChange}
						value={multiSelectValue}
					/>
				</ClayInput.GroupItem>
			</div>
		</>
	);
}

function Inputs({onChange, onReplace, contributorOptions, value = {}}) {
	const [touched, setTouched] = useState({
		displayGroupName: false,
		size: false,
		sxpBlueprintId: false,
	});

	const _handleBlur = (field) => () => {
		setTouched({...touched, [field]: true});
	};

	const _handleChange = (property) => (event) => {
		onChange({[property]: event.target.value});
	};

	const _handleChangeContributorName = (contributorName) => {
		if (contributorName === CONTRIBUTOR_TYPES.BASIC) {
			onReplace({
				contributorName,
				displayGroupName: value.displayGroupName,
				size: value.size,
			});
		}
		else {
			onChange({
				attributes: DEFAULT_ATTRIBUTES,
				contributorName,
				displayGroupName: value.displayGroupName,
				size: value.size,
			});
		}
	};

	return (
		<ClayInput.GroupItem>
			<div className="form-group-autofit">
				<ClayInput.GroupItem>
					<label>
						{Liferay.Language.get('suggestion-contributor')}

						<span className="reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>

						<ClayTooltipProvider>
							<span
								className="ml-2"
								data-tooltip-align="top"
								title={Liferay.Language.get(
									'suggestion-contributor-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<ClayDropDown
						closeOnClick
						menuWidth="sm"
						trigger={
							<ClayButton
								aria-label={Liferay.Language.get(
									'suggestion-contributor'
								)}
								className="form-control form-control-select"
								displayType="unstyled"
							>
								{
									contributorOptions.find(
										({name}) =>
											name === value.contributorName
									).title
								}
							</ClayButton>
						}
					>
						<ClayDropDown.ItemList items={contributorOptions}>
							{(item) => (
								<ClayDropDown.Item
									active={value.contributorName === item.name}
									key={item.name}
									onClick={() =>
										_handleChangeContributorName(item.name)
									}
								>
									<div>{item.title}</div>

									<div className="text-2">
										{item.subtitle}
									</div>
								</ClayDropDown.Item>
							)}
						</ClayDropDown.ItemList>
					</ClayDropDown>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem
					className={getCN({
						'has-error':
							!value.displayGroupName && touched.displayGroupName,
					})}
				>
					<label>
						{Liferay.Language.get('display-group-name')}

						<span className="reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>

						<ClayTooltipProvider>
							<span
								className="ml-2"
								data-tooltip-align="top"
								title={Liferay.Language.get(
									'display-group-name-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<ClayInput
						onBlur={_handleBlur('displayGroupName')}
						onChange={_handleChange('displayGroupName')}
						required
						type="text"
						value={value.displayGroupName || ''}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem
					className={getCN('size-input', {
						'has-error':
							(!value.size || value.size < 0) && touched.size,
					})}
				>
					<label>
						{Liferay.Language.get('size')}

						<span className="reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>

						<ClayTooltipProvider>
							<span
								className="ml-2"
								data-tooltip-align="top"
								title={Liferay.Language.get(
									'size-suggestion-help'
								)}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</ClayTooltipProvider>
					</label>

					<ClayInput
						aria-label={Liferay.Language.get('size')}
						min="0"
						onBlur={_handleBlur('size')}
						onChange={_handleChange('size')}
						required
						type="number"
						value={value.size || ''}
					/>

					{value.size < 0 && touched.size && (
						<div className="form-feedback-group">
							<div className="form-feedback-item">
								{sub(
									Liferay.Language.get(
										'please-enter-a-value-greater-than-or-equal-to-x'
									),
									'0'
								)}
							</div>
						</div>
					)}
				</ClayInput.GroupItem>
			</div>

			{value.contributorName === CONTRIBUTOR_TYPES.SXP_BLUEPRINT && (
				<SXPBlueprintAttributes
					onBlur={_handleBlur}
					onChange={onChange}
					touched={touched}
					value={value}
				/>
			)}
		</ClayInput.GroupItem>
	);
}

function SearchBarConfigurationSuggestions({
	initialSuggestionsContributorConfiguration = '[]',
	isDXP = false,
	isSearchExperiencesSupported = true,
	learnMessages,
	namespace = '',
	suggestionsContributorConfigurationName = '',
}) {
	const blueprintsEnabled = isDXP && isSearchExperiencesSupported;

	const [
		suggestionsContributorConfiguration,
		setSuggestionsContributorConfiguration,
	] = useState(
		cleanSuggestionsContributorConfiguration(
			initialSuggestionsContributorConfiguration,
			isSearchExperiencesSupported
		).map((item, index) => ({
			...item,
			id: index, // For FieldList item `key` when reordering.
		}))
	);

	/*
	 * If blueprints are not enabled, exactly one contributor can be added.
	 */
	const _hasAvailableContributors = () =>
		blueprintsEnabled || !suggestionsContributorConfiguration.length;

	const _getContributorOptions = (index) => {
		const BASIC_OPTION = {
			name: CONTRIBUTOR_TYPES.BASIC,
			subtitle: Liferay.Language.get(
				'basic-suggestions-contributor-help'
			),
			title: Liferay.Language.get('basic'),
		};

		const BLUEPRINT_OPTION = {
			name: CONTRIBUTOR_TYPES.SXP_BLUEPRINT,
			subtitle: (
				<>
					{Liferay.Language.get(
						'blueprint-suggestions-contributor-help'
					)}

					<LearnMessage
						className="ml-1"
						learnMessages={learnMessages}
						resourceKey="search-bar-suggestions-blueprints"
					/>
				</>
			),
			title: Liferay.Language.get('blueprint'),
		};

		if (!blueprintsEnabled) {
			return [BASIC_OPTION];
		}

		const indexOfBasic = suggestionsContributorConfiguration.findIndex(
			(value) => value.contributorName === CONTRIBUTOR_TYPES.BASIC
		);

		if (indexOfBasic > -1 && index !== indexOfBasic) {
			return [BLUEPRINT_OPTION];
		}

		return [BASIC_OPTION, BLUEPRINT_OPTION];
	};

	const _getDefaultValue = () => {
		if (
			suggestionsContributorConfiguration.some(
				(config) => config.contributorName === CONTRIBUTOR_TYPES.BASIC
			)
		) {
			return {
				attributes: DEFAULT_ATTRIBUTES,
				contributorName: CONTRIBUTOR_TYPES.SXP_BLUEPRINT,
				displayGroupName: '',
				size: '',
			};
		}

		return {
			contributorName: CONTRIBUTOR_TYPES.BASIC,
			displayGroupName: '',
			size: '',
		};
	};

	return (
		<div className="search-bar-configuration-suggestions">
			{removeEmptyFields(suggestionsContributorConfiguration).length ? (
				removeEmptyFields(
					suggestionsContributorConfiguration
				).map(({id, ...item}) => (
					<input
						hidden
						key={id}
						name={`${namespace}${suggestionsContributorConfigurationName}`}
						readOnly
						value={JSON.stringify(item)}
					/>
				))
			) : (
				<input
					hidden
					name={`${namespace}${suggestionsContributorConfigurationName}`}
					readOnly
					value=""
				/>
			)}

			<FieldList
				addButtonLabel={Liferay.Language.get('add-contributor')}
				defaultValue={_getDefaultValue()}
				onChange={setSuggestionsContributorConfiguration}
				renderInputs={({index, onChange, onReplace, value}) => (
					<Inputs
						contributorOptions={_getContributorOptions(index)}
						key={index}
						onChange={onChange}
						onReplace={onReplace}
						value={value}
					/>
				)}
				showAddButton={_hasAvailableContributors()}
				showDeleteButton={true}
				showDragButton={blueprintsEnabled}
				value={suggestionsContributorConfiguration}
			/>
		</div>
	);
}

export default SearchBarConfigurationSuggestions;
