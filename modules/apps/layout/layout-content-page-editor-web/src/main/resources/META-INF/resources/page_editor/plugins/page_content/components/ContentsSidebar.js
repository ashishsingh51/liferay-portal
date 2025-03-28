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

import React, {useMemo} from 'react';

import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../app/config/constants/editableFragmentEntryProcessor';
import {EDITABLE_TYPES} from '../../../app/config/constants/editableTypes';
import {useSelector} from '../../../app/contexts/StoreContext';
import selectLanguageId from '../../../app/selectors/selectLanguageId';
import {selectPageContents} from '../../../app/selectors/selectPageContents';
import isMapped from '../../../app/utils/editable_value/isMapped';
import {getEditableLocalizedValue} from '../../../app/utils/getEditableLocalizedValue';
import getFragmentItem from '../../../app/utils/getFragmentItem';
import {hasFormParentWithPermissions} from '../../../app/utils/hasFormParentWithPermissions';
import SidebarPanelHeader from '../../../common/components/SidebarPanelHeader';
import NoPageContents from './NoPageContents';
import PageContents from './PageContents';

const getEditableTitle = (editable, languageId) => {
	const div = document.createElement('div');

	div.innerHTML = getEditableLocalizedValue(editable, languageId);

	return div.textContent.trim();
};

const getEditableValues = (
	fragmentEntryLinks,
	segmentsExperienceId,
	layoutData
) =>
	Object.values(fragmentEntryLinks)
		.filter((fragmentEntryLink) => {
			if (Liferay.FeatureFlags['LPS-169923']) {
				const item = getFragmentItem(
					layoutData,
					fragmentEntryLink.fragmentEntryLinkId
				);

				if (item && !hasFormParentWithPermissions(item, layoutData)) {
					return;
				}
			}

			return (
				!fragmentEntryLink.masterLayout &&
				fragmentEntryLink.editableValues &&
				!fragmentEntryLink.removed &&
				fragmentEntryLink.segmentsExperienceId === segmentsExperienceId
			);
		})
		.map((fragmentEntryLink) => {
			const editableValues = Object.entries(
				fragmentEntryLink.editableValues[
					EDITABLE_FRAGMENT_ENTRY_PROCESSOR
				] ?? {}
			);

			return editableValues
				.filter(
					([key, value]) =>
						(fragmentEntryLink.editableTypes[key] ===
							EDITABLE_TYPES.text ||
							fragmentEntryLink.editableTypes[key] ===
								EDITABLE_TYPES['rich-text']) &&
						!isMapped(value)
				)
				.map(([key, value]) => ({
					...value,
					editableId: `${fragmentEntryLink.fragmentEntryLinkId}-${key}`,
					type: fragmentEntryLink.editableTypes[key],
				}));
		})
		.reduce(
			(editableValuesA, editableValuesB) => [
				...editableValuesA,
				...editableValuesB,
			],
			[]
		);

const normalizeEditableValues = (editable, languageId) => {
	return {
		...editable,
		icon: 'align-left',
		title: getEditableTitle(editable, languageId),
	};
};

const normalizePageContents = (pageContents) =>
	pageContents.reduce(
		(acc, content) =>
			acc[content.type]
				? {...acc, [content.type]: [...acc[content.type], content]}
				: {...acc, [content.type]: [content]},
		{}
	);

export default function ContentsSidebar() {
	const fragmentEntryLinks = useSelector((state) => state.fragmentEntryLinks);
	const languageId = useSelector(selectLanguageId);
	const layoutData = useSelector((state) => state.layoutData);
	const pageContents = useSelector(selectPageContents);
	const segmentsExperienceId = useSelector(
		(state) => state.segmentsExperienceId
	);

	const inlineTextContents = useMemo(
		() =>
			getEditableValues(
				fragmentEntryLinks,
				segmentsExperienceId,
				layoutData
			)
				.map((editable) =>
					normalizeEditableValues(editable, languageId)
				)
				.filter((editable) => editable.title),
		[fragmentEntryLinks, languageId, segmentsExperienceId, layoutData]
	);

	const contents = normalizePageContents(pageContents);

	const contentsWithInlineText = {
		...contents,
		...(inlineTextContents.length && {
			[Liferay.Language.get('inline-text')]: inlineTextContents,
		}),
	};

	const view = Object.keys(contentsWithInlineText).length ? (
		<PageContents pageContents={contentsWithInlineText} />
	) : (
		<NoPageContents />
	);

	return (
		<>
			<SidebarPanelHeader>
				{Liferay.Language.get('page-content')}
			</SidebarPanelHeader>

			<div className="d-flex flex-column page-editor__page-contents">
				{view}
			</div>
		</>
	);
}
