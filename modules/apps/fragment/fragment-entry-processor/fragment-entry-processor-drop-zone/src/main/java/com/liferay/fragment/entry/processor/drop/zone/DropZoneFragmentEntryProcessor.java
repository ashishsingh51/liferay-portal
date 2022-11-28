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

package com.liferay.fragment.entry.processor.drop.zone;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessor;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.renderer.FragmentDropZoneRenderer;
import com.liferay.layout.constants.LayoutWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	immediate = true, property = "fragment.entry.processor.priority:Integer=6",
	service = FragmentEntryProcessor.class
)
public class DropZoneFragmentEntryProcessor implements FragmentEntryProcessor {

	@Override
	public JSONArray getAvailableTagsJSONArray() {
		return JSONUtil.put(
			JSONUtil.put(
				"content", "<lfr-drop-zone></lfr-drop-zone>"
			).put(
				"name", "lfr-drop-zone"
			));
	}

	@Override
	public JSONArray getDataAttributesJSONArray() {
		return JSONUtil.put("lfr-priority");
	}

	@Override
	public String processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink, String html,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		Document document = _getDocument(html);

		Elements elements = document.select("lfr-drop-zone");

		if (elements.size() <= 0) {
			return html;
		}

		HttpServletRequest httpServletRequest =
			fragmentEntryProcessorContext.getHttpServletRequest();

		LayoutStructure layoutStructure = null;

		if (httpServletRequest != null) {
			layoutStructure = (LayoutStructure)httpServletRequest.getAttribute(
				LayoutWebKeys.LAYOUT_STRUCTURE);
		}

		if (layoutStructure == null) {
			LayoutPageTemplateStructure layoutPageTemplateStructure =
				_layoutPageTemplateStructureLocalService.
					fetchLayoutPageTemplateStructure(
						fragmentEntryLink.getGroupId(),
						fragmentEntryLink.getPlid());

			if (layoutPageTemplateStructure == null) {
				return html;
			}

			layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructure.getData(
					fragmentEntryLink.getSegmentsExperienceId()));
		}

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLink.getFragmentEntryLinkId());

		if (layoutStructureItem == null) {
			return html;
		}

		List<String> dropZoneItemIds = layoutStructureItem.getChildrenItemIds();

		if (Objects.equals(
				fragmentEntryProcessorContext.getMode(),
				FragmentEntryLinkConstants.EDIT)) {

			boolean idsAvailable = true;

			for (Element element : elements) {
				String dropZoneId = element.attr("data-lfr-drop-zone-id");

				if (Validator.isBlank(dropZoneId)) {
					idsAvailable = false;

					break;
				}
			}

			if (!GetterUtil.getBoolean(
					PropsUtil.get("feature.flag.LPS-167932")) ||
				!idsAvailable) {

				for (int i = 0;
					 (i < dropZoneItemIds.size()) && (i < elements.size());
					 i++) {

					Element element = elements.get(i);

					element.attr("uuid", dropZoneItemIds.get(i));
				}
			}
			else {
				for (int i = 0; i < elements.size(); i++) {
					Element element = elements.get(i);

					String dropZoneId = element.attr("data-lfr-drop-zone-id");

					for (String itemId : dropZoneItemIds) {
						LayoutStructureItem childLayoutStructureItem =
							layoutStructure.getLayoutStructureItem(itemId);

						if (!(childLayoutStructureItem instanceof
								FragmentDropZoneLayoutStructureItem)) {

							continue;
						}

						FragmentDropZoneLayoutStructureItem
							fragmentDropZoneLayoutStructureItem =
								(FragmentDropZoneLayoutStructureItem)
									childLayoutStructureItem;

						if (Objects.equals(
								dropZoneId,
								fragmentDropZoneLayoutStructureItem.
									getFragmentDropZoneId())) {

							element.attr("uuid", itemId);

							break;
						}
					}
				}
			}

			Element bodyElement = document.body();

			return bodyElement.html();
		}

		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);

			String dropZoneHTML = _fragmentDropZoneRenderer.renderDropZone(
				fragmentEntryProcessorContext.getHttpServletRequest(),
				fragmentEntryProcessorContext.getHttpServletResponse(),
				dropZoneItemIds.get(i), fragmentEntryProcessorContext.getMode(),
				true);

			Element dropZoneElement = new Element("div");

			dropZoneElement.html(dropZoneHTML);

			element.replaceWith(dropZoneElement);
		}

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	@Override
	public void validateFragmentEntryHTML(String html, String configuration)
		throws PortalException {

		if (!GetterUtil.getBoolean(PropsUtil.get("feature.flag.LPS-167932"))) {
			return;
		}

		Document document = _getDocument(html);

		Elements elements = document.select("lfr-drop-zone");

		if (elements.isEmpty()) {
			return;
		}

		Set<String> elementDropZoneIds = new LinkedHashSet<>();

		for (Element element : elements) {
			String dropZoneId = element.attr("data-lfr-drop-zone-id");

			if (Validator.isNotNull(dropZoneId)) {
				elementDropZoneIds.add(dropZoneId);
			}
		}

		if (!elementDropZoneIds.isEmpty() &&
			(elementDropZoneIds.size() != elements.size())) {

			throw new FragmentEntryContentException(
				_language.get(
					_portal.getResourceBundle(LocaleUtil.getDefault()),
					"you-must-define-a-unique-id-for-each-drop-zone"));
		}
	}

	private Document _getDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);

		document.outputSettings(outputSettings);

		return document;
	}

	@Reference
	private FragmentDropZoneRenderer _fragmentDropZoneRenderer;

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

}