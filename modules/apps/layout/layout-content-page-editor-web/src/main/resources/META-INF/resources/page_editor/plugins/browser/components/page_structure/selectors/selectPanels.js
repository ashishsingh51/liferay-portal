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

import {COLLECTION_APPLIED_FILTERS_FRAGMENT_ENTRY_KEY} from '../../../../../app/config/constants/collectionAppliedFiltersFragmentKey';
import {COLLECTION_FILTER_FRAGMENT_ENTRY_KEY} from '../../../../../app/config/constants/collectionFilterFragmentEntryKey';
import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../app/config/constants/editableFragmentEntryProcessor';
import {EDITABLE_TYPES} from '../../../../../app/config/constants/editableTypes';
import {FRAGMENT_ENTRY_TYPES} from '../../../../../app/config/constants/fragmentEntryTypes';
import {ITEM_TYPES} from '../../../../../app/config/constants/itemTypes';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../app/config/constants/viewportSizes';
import selectCanUpdateCSSAdvancedOptions from '../../../../../app/selectors/selectCanUpdateCSSAdvancedOptions';
import selectCanUpdateEditables from '../../../../../app/selectors/selectCanUpdateEditables';
import selectCanUpdateItemAdvancedConfiguration from '../../../../../app/selectors/selectCanUpdateItemAdvancedConfiguration';
import selectCanUpdateItemConfiguration from '../../../../../app/selectors/selectCanUpdateItemConfiguration';
import {formHasPermissions} from '../../../../../app/utils/formHasPermissions';
import getFragmentItem from '../../../../../app/utils/getFragmentItem';
import isEditableSubmit from '../../../../../app/utils/isEditableSubmit';
import {CollectionAppliedFiltersGeneralPanel} from '../components/item_configuration_panels/CollectionAppliedFiltersGeneralPanel';
import {CollectionFilterGeneralPanel} from '../components/item_configuration_panels/CollectionFilterGeneralPanel';
import ContainerAdvancedPanel from '../components/item_configuration_panels/ContainerAdvancedPanel';
import ContainerGeneralPanel from '../components/item_configuration_panels/ContainerGeneralPanel';
import {ContainerStylesPanel} from '../components/item_configuration_panels/ContainerStylesPanel';
import EditableLinkPanel from '../components/item_configuration_panels/EditableLinkPanel';
import FormAdvancedPanel from '../components/item_configuration_panels/FormAdvancedPanel';
import {FormGeneralPanel} from '../components/item_configuration_panels/FormGeneralPanel';
import {FormInputGeneralPanel} from '../components/item_configuration_panels/FormInputGeneralPanel';
import {FragmentAdvancedPanel} from '../components/item_configuration_panels/FragmentAdvancedPanel';
import {FragmentGeneralPanel} from '../components/item_configuration_panels/FragmentGeneralPanel';
import {FragmentStylesPanel} from '../components/item_configuration_panels/FragmentStylesPanel';
import ImageSourcePanel from '../components/item_configuration_panels/ImageSourcePanel';
import {MappingPanel} from '../components/item_configuration_panels/MappingPanel';
import {RowAdvancedPanel} from '../components/item_configuration_panels/RowAdvancedPanel';
import {RowGeneralPanel} from '../components/item_configuration_panels/RowGeneralPanel';
import {RowStylesPanel} from '../components/item_configuration_panels/RowStylesPanel';
import {CollectionGeneralPanel} from '../components/item_configuration_panels/collection_general_panel/CollectionGeneralPanel';

const FRAGMENT_WITH_CUSTOM_PANEL = [
	COLLECTION_FILTER_FRAGMENT_ENTRY_KEY,
	COLLECTION_APPLIED_FILTERS_FRAGMENT_ENTRY_KEY,
];

const PANEL_TYPES = {
	advanced: 'advanced',
	general: 'general',
	styles: 'styles',
};

export const PANEL_IDS = {
	collectionAppliedFiltersGeneral: 'collectionAppliedFiltersGeneral',
	collectionFilterGeneral: 'collectionFilterGeneral',
	collectionGeneral: 'collectionGeneral',
	containerAdvanced: 'containerAdvanced',
	containerGeneral: 'containerGeneral',
	containerStyles: 'containerStyles',
	editableLink: 'editableLink',
	editableMapping: 'editableMapping',
	formAdvancedPanel: 'formAdvancedPanel',
	formGeneral: 'formGeneral',
	formInputGeneral: 'formInputGeneral',
	fragmentAdvanced: 'fragmentAdvanced',
	fragmentGeneral: 'fragmentGeneral',
	fragmentStyles: 'fragmentStyles',
	imageSource: 'imageSource',
	rowAdvanced: 'rowAdvanced',
	rowGeneral: 'rowGeneral',
	rowStyles: 'rowStyles',
};

export const PANELS = {
	[PANEL_IDS.collectionAppliedFiltersGeneral]: {
		component: CollectionAppliedFiltersGeneralPanel,
		label: Liferay.Language.get('general'),
		priority: 2,
		type: PANEL_TYPES.general,
	},
	[PANEL_IDS.collectionFilterGeneral]: {
		component: CollectionFilterGeneralPanel,
		label: Liferay.Language.get('general'),
		priority: 2,
		type: PANEL_TYPES.general,
	},
	[PANEL_IDS.collectionGeneral]: {
		component: CollectionGeneralPanel,
		label: Liferay.Language.get('general'),
		priority: 0,
		type: PANEL_TYPES.general,
	},
	[PANEL_IDS.containerAdvanced]: {
		component: ContainerAdvancedPanel,
		label: Liferay.Language.get('advanced'),
		priority: 0,
		type: PANEL_TYPES.advanced,
	},
	[PANEL_IDS.containerGeneral]: {
		component: ContainerGeneralPanel,
		label: Liferay.Language.get('general'),
		priority: 2,
		type: PANEL_TYPES.general,
	},
	[PANEL_IDS.containerStyles]: {
		component: ContainerStylesPanel,
		label: Liferay.Language.get('styles'),
		priority: 1,
		type: PANEL_TYPES.styles,
	},
	[PANEL_IDS.editableLink]: {
		component: EditableLinkPanel,
		label: Liferay.Language.get('link'),
		priority: 0,
	},
	[PANEL_IDS.editableMapping]: {
		component: MappingPanel,
		label: Liferay.Language.get('mapping'),
		priority: 1,
	},
	[PANEL_IDS.formAdvancedPanel]: {
		component: FormAdvancedPanel,
		label: Liferay.Language.get('advanced'),
		priority: 0,
		type: PANEL_TYPES.advanced,
	},
	[PANEL_IDS.formGeneral]: {
		component: FormGeneralPanel,
		label: Liferay.Language.get('general'),
		priority: 2,
	},
	[PANEL_IDS.formInputGeneral]: {
		component: FormInputGeneralPanel,
		label: Liferay.Language.get('general'),
		priority: 2,
		type: PANEL_TYPES.general,
	},
	[PANEL_IDS.fragmentAdvanced]: {
		component: FragmentAdvancedPanel,
		label: Liferay.Language.get('advanced'),
		priority: 0,
		type: PANEL_TYPES.advanced,
	},
	[PANEL_IDS.fragmentGeneral]: {
		component: FragmentGeneralPanel,
		label: Liferay.Language.get('general'),
		priority: 2,
		type: PANEL_TYPES.general,
	},
	[PANEL_IDS.fragmentStyles]: {
		component: FragmentStylesPanel,
		label: Liferay.Language.get('styles'),
		priority: 1,
		type: PANEL_TYPES.styles,
	},
	[PANEL_IDS.imageSource]: {
		component: ImageSourcePanel,
		label: Liferay.Language.get('image-source'),
		priority: 3,
	},
	[PANEL_IDS.rowAdvanced]: {
		component: RowAdvancedPanel,
		label: Liferay.Language.get('advanced'),
		priority: 0,
		type: PANEL_TYPES.advanced,
	},
	[PANEL_IDS.rowGeneral]: {
		component: RowGeneralPanel,
		label: Liferay.Language.get('general'),
		priority: 2,
		type: PANEL_TYPES.general,
	},
	[PANEL_IDS.rowStyles]: {
		component: RowStylesPanel,
		label: Liferay.Language.get('styles'),
		priority: 1,
		type: PANEL_TYPES.styles,
	},
};

export function selectPanels(activeItemId, activeItemType, state) {
	let activeItem = null;
	let panelsIds = {};

	if (activeItemType === ITEM_TYPES.layoutDataItem) {
		activeItem = state.layoutData.items[activeItemId];
	}
	else if (activeItemType === ITEM_TYPES.editable) {
		const [fragmentEntryLinkId] = activeItemId.split('-');

		const editableId = activeItemId.substr(
			activeItemId.indexOf('-') + 1,
			activeItemId.length
		);

		activeItem = {
			editableId,
			editableValueNamespace: EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
			fragmentEntryLinkId,
			itemId: activeItemId,
			parentId: getFragmentItem(state.layoutData, fragmentEntryLinkId)
				.itemId,
			type:
				state.fragmentEntryLinks[fragmentEntryLinkId].editableTypes[
					editableId
				],
		};
	}

	if (!activeItem) {
		return {activeItem, panelsIds};
	}

	const canUpdateEditables = selectCanUpdateEditables(state);
	const canUpdateItemAdvancedConfiguration = selectCanUpdateItemAdvancedConfiguration(
		state
	);
	const canUpdateCSSAdvancedOptions = selectCanUpdateCSSAdvancedOptions(
		state
	);

	const canUpdateItemConfiguration = selectCanUpdateItemConfiguration(state);

	const haveAtLeastLimitedPermission =
		state.permissions.UPDATE || state.permissions.UPDATE_LAYOUT_LIMITED;

	if (canUpdateEditables && activeItem.editableId) {
		panelsIds = {
			[PANEL_IDS.editableLink]:
				[
					EDITABLE_TYPES.text,
					EDITABLE_TYPES.image,
					EDITABLE_TYPES.link,
				].includes(activeItem.type) &&
				state.selectedViewportSize === VIEWPORT_SIZES.desktop &&
				!isEditableSubmit(activeItem.editableId, activeItem.parentId),
			[PANEL_IDS.imageSource]:
				activeItem.type === EDITABLE_TYPES.image ||
				activeItem.type === EDITABLE_TYPES.backgroundImage,
			[PANEL_IDS.editableMapping]:
				state.selectedViewportSize === VIEWPORT_SIZES.desktop &&
				activeItem.type !== EDITABLE_TYPES.image &&
				activeItem.type !== EDITABLE_TYPES.backgroundImage,
		};
	}
	else if (!canUpdateItemConfiguration) {
		return {activeItem, panelsIds};
	}
	else if (activeItem.type === LAYOUT_DATA_ITEM_TYPES.collection) {
		panelsIds = {
			[PANEL_IDS.collectionGeneral]: true,
		};
	}
	else if (activeItem.type === LAYOUT_DATA_ITEM_TYPES.container) {
		panelsIds = {
			[PANEL_IDS.containerAdvanced]:
				(canUpdateItemAdvancedConfiguration &&
					state.selectedViewportSize === VIEWPORT_SIZES.desktop) ||
				canUpdateCSSAdvancedOptions,
			[PANEL_IDS.containerGeneral]: true,
			[PANEL_IDS.containerStyles]: true,
		};
	}
	else if (activeItem.type === LAYOUT_DATA_ITEM_TYPES.form) {
		panelsIds =
			Liferay.FeatureFlags['LPS-169923'] &&
			!formHasPermissions(activeItem)
				? {
						[PANEL_IDS.formGeneral]:
							state.selectedViewportSize ===
							VIEWPORT_SIZES.desktop,
				  }
				: {
						[PANEL_IDS.formAdvancedPanel]:
							(canUpdateItemAdvancedConfiguration &&
								state.selectedViewportSize ===
									VIEWPORT_SIZES.desktop) ||
							canUpdateCSSAdvancedOptions,
						[PANEL_IDS.formGeneral]:
							state.selectedViewportSize ===
							VIEWPORT_SIZES.desktop,
						[PANEL_IDS.containerStyles]: haveAtLeastLimitedPermission,
				  };
	}
	else if (activeItem.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
		const {fragmentEntryKey, fragmentEntryType} = state.fragmentEntryLinks[
			activeItem.config.fragmentEntryLinkId
		];

		panelsIds = {
			[PANEL_IDS.fragmentAdvanced]:
				(canUpdateItemAdvancedConfiguration &&
					state.selectedViewportSize === VIEWPORT_SIZES.desktop) ||
				canUpdateCSSAdvancedOptions,
			[PANEL_IDS.fragmentStyles]: haveAtLeastLimitedPermission,
			[PANEL_IDS.fragmentGeneral]:
				fragmentEntryType !== FRAGMENT_ENTRY_TYPES.input &&
				!FRAGMENT_WITH_CUSTOM_PANEL.includes(fragmentEntryKey),
			[PANEL_IDS.collectionAppliedFiltersGeneral]:
				fragmentEntryKey ===
					COLLECTION_APPLIED_FILTERS_FRAGMENT_ENTRY_KEY &&
				state.selectedViewportSize === VIEWPORT_SIZES.desktop,
			[PANEL_IDS.collectionFilterGeneral]:
				fragmentEntryKey === COLLECTION_FILTER_FRAGMENT_ENTRY_KEY &&
				state.selectedViewportSize === VIEWPORT_SIZES.desktop,
			[PANEL_IDS.formInputGeneral]:
				fragmentEntryType === FRAGMENT_ENTRY_TYPES.input &&
				state.selectedViewportSize === VIEWPORT_SIZES.desktop,
		};
	}
	else if (activeItem.type === LAYOUT_DATA_ITEM_TYPES.row) {
		panelsIds = {
			[PANEL_IDS.rowStyles]: true,
			[PANEL_IDS.rowGeneral]: true,
			[PANEL_IDS.rowAdvanced]:
				(canUpdateItemAdvancedConfiguration &&
					state.selectedViewportSize === VIEWPORT_SIZES.desktop) ||
				canUpdateCSSAdvancedOptions,
		};
	}

	return {activeItem, panelsIds};
}
