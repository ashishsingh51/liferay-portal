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

package com.liferay.layout.internal.importer;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.delivery.dto.v1_0.ContentSubtype;
import com.liferay.headless.delivery.dto.v1_0.ContentType;
import com.liferay.headless.delivery.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.delivery.dto.v1_0.MasterPage;
import com.liferay.headless.delivery.dto.v1_0.PageDefinition;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.headless.delivery.dto.v1_0.PageTemplate;
import com.liferay.headless.delivery.dto.v1_0.PageTemplateCollection;
import com.liferay.headless.delivery.dto.v1_0.Settings;
import com.liferay.headless.delivery.dto.v1_0.StyleBook;
import com.liferay.headless.delivery.dto.v1_0.UtilityPageTemplate;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.importer.LayoutsImporterResultEntry;
import com.liferay.layout.internal.headless.delivery.dto.v1_0.util.UtilityPageTemplateUtil;
import com.liferay.layout.internal.importer.exception.DropzoneLayoutStructureItemException;
import com.liferay.layout.internal.importer.structure.util.LayoutStructureItemImporter;
import com.liferay.layout.internal.importer.validator.DisplayPageTemplateValidator;
import com.liferay.layout.internal.importer.validator.MasterPageValidator;
import com.liferay.layout.internal.importer.validator.PageDefinitionValidator;
import com.liferay.layout.internal.importer.validator.PageTemplateCollectionValidator;
import com.liferay.layout.internal.importer.validator.PageTemplateValidator;
import com.liferay.layout.internal.importer.validator.UtilityPageTemplateValidator;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateExportImportConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.LayoutCopyHelper;
import com.liferay.layout.util.constants.LayoutStructureConstants;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.utility.page.constants.LayoutUtilityPageExportImportConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.validator.JSONValidatorException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = LayoutsImporter.class)
public class LayoutsImporterImpl implements LayoutsImporter {

	@Override
	public void importFile(
			long userId, long groupId, File file, boolean overwrite)
		throws Exception {

		importFile(userId, groupId, 0, file, overwrite);
	}

	@Override
	public List<LayoutsImporterResultEntry> importFile(
			long userId, long groupId, long layoutPageTemplateCollectionId,
			File file, boolean overwrite)
		throws Exception {

		_layoutsImporterResultEntries = new ArrayList<>();

		try (ZipFile zipFile = new ZipFile(file)) {
			_processLayoutUtilityPageEntries(groupId, overwrite, zipFile);

			_processMasterLayoutPageTemplateEntries(
				groupId, overwrite, zipFile);

			_processDisplayPageTemplatePageTemplateEntries(
				groupId, overwrite, zipFile);

			_processBasicLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, overwrite, zipFile);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);

				throw portalException;
			}
		}

		return _layoutsImporterResultEntries;
	}

	@Override
	public List<FragmentEntryLink> importPageElement(
			Layout layout, LayoutStructure layoutStructure, String parentItemId,
			String pageElementJSON, int position)
		throws Exception {

		Consumer<LayoutStructure> consumer = processedLayoutStructure -> {
			try {
				_updateLayoutPageTemplateStructure(
					layout, processedLayoutStructure);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		};

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		return _importPageElement(
			consumer, layout, layoutStructure, parentItemId, pageElementJSON,
			position, segmentsExperienceId);
	}

	@Override
	public List<FragmentEntryLink> importPageElement(
			Layout layout, LayoutStructure layoutStructure, String parentItemId,
			String pageElementJSON, int position, long segmentsExperienceId)
		throws Exception {

		Consumer<LayoutStructure> consumer = processedLayoutStructure -> {
			try {
				_layoutPageTemplateStructureLocalService.
					updateLayoutPageTemplateStructureData(
						layout.getGroupId(), layout.getPlid(),
						segmentsExperienceId,
						processedLayoutStructure.toString());
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		};

		return _importPageElement(
			consumer, layout, layoutStructure, parentItemId, pageElementJSON,
			position, segmentsExperienceId);
	}

	private void _deleteExistingPortletPreferences(long plid) {
		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid);

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			_portletPreferencesLocalService.deletePortletPreferences(
				portletPreferences);
		}
	}

	private PageTemplateCollectionEntry
		_getDefaultPageTemplateCollectionEntry() {

		PageTemplateCollection pageTemplateCollection =
			new PageTemplateCollection() {
				{
					name = _PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT;
				}
			};

		return new PageTemplateCollectionEntry(
			_PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT, pageTemplateCollection);
	}

	private List<DisplayPageTemplateEntry> _getDisplayPageTemplateEntries(
			long groupId, ZipFile zipFile)
		throws Exception {

		List<DisplayPageTemplateEntry> displayPageTemplateEntries =
			new ArrayList<>();

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if ((zipEntry == null) ||
				!_isDisplayPageTemplateFile(zipEntry.getName())) {

				continue;
			}

			String content = StringUtil.read(zipFile.getInputStream(zipEntry));

			DisplayPageTemplate displayPageTemplate = null;

			try {
				DisplayPageTemplateValidator.validateDisplayPageTemplate(
					content);

				displayPageTemplate = _objectMapper.readValue(
					content, DisplayPageTemplate.class);
			}
			catch (JSONValidatorException jsonValidatorException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid display page template for: " +
							zipEntry.getName(),
						jsonValidatorException);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						zipEntry.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_DISPLAY_PAGE,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-its-display-" +
								"page-template-is-invalid",
							new String[] {zipEntry.getName()})));

				continue;
			}

			try {
				String pageDefinitionJSON = _getPageDefinitionJSON(
					zipEntry.getName(), zipFile);

				PageDefinitionValidator.validatePageDefinition(
					pageDefinitionJSON);

				displayPageTemplateEntries.add(
					new DisplayPageTemplateEntry(
						displayPageTemplate,
						_getKey(
							_DISPLAY_PAGE_TEMPLATE_ENTRY_KEY_DEFAULT,
							displayPageTemplate.getName(), zipEntry),
						_objectMapper.readValue(
							pageDefinitionJSON, PageDefinition.class),
						_getThumbnailZipEntry(zipEntry.getName(), zipFile),
						zipEntry.getName()));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page definition for: " +
							displayPageTemplate.getName(),
						exception);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						displayPageTemplate.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_DISPLAY_PAGE,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid",
							new String[] {zipEntry.getName()})));
			}
		}

		return displayPageTemplateEntries;
	}

	private String _getErrorMessage(
			long groupId, String languageKey, String[] arguments)
		throws PortalException {

		Locale locale = null;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			locale = serviceContext.getLocale();
		}
		else {
			locale = _portal.getSiteDefaultLocale(groupId);
		}

		return _language.format(locale, languageKey, arguments);
	}

	private String _getKey(String defaultKey, String name, ZipEntry zipEntry) {
		String[] pathParts = StringUtil.split(
			zipEntry.getName(), CharPool.SLASH);

		String key = defaultKey;

		if (Validator.isNotNull(name)) {
			key = name;
		}

		if (pathParts.length > 1) {
			key = pathParts[pathParts.length - 2];
		}

		key = StringUtil.replace(key, CharPool.SPACE, CharPool.DASH);
		key = StringUtil.toLowerCase(key);

		return key;
	}

	private LayoutPageTemplateCollection _getLayoutPageTemplateCollection(
			long groupId, long layoutPageTemplateCollectionId,
			PageTemplateCollectionEntry pageTemplateCollectionEntry,
			boolean overwrite)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection = null;

		if (layoutPageTemplateCollectionId > 0) {
			layoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					fetchLayoutPageTemplateCollection(
						layoutPageTemplateCollectionId);

			if (layoutPageTemplateCollection == null) {
				throw new PortalException(
					"Invalid layout page template collection ID: " +
						layoutPageTemplateCollectionId);
			}

			return layoutPageTemplateCollection;
		}

		String layoutPageTemplateCollectionKey =
			pageTemplateCollectionEntry.getKey();

		PageTemplateCollection pageTemplateCollection =
			pageTemplateCollectionEntry.getPageTemplateCollection();

		layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					groupId, layoutPageTemplateCollectionKey);

		if (layoutPageTemplateCollection == null) {
			layoutPageTemplateCollection =
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollectionByName(
						groupId, pageTemplateCollection.getName());

			if (layoutPageTemplateCollection == null) {
				return _layoutPageTemplateCollectionService.
					addLayoutPageTemplateCollection(
						groupId, pageTemplateCollection.getName(),
						pageTemplateCollection.getDescription(),
						ServiceContextThreadLocal.getServiceContext());
			}
		}

		if (overwrite) {
			return _layoutPageTemplateCollectionService.
				updateLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					pageTemplateCollection.getName(),
					pageTemplateCollection.getDescription());
		}

		if (layoutPageTemplateCollection == null) {
			throw new PortalException(
				"Invalid layout page template collection ID: " +
					layoutPageTemplateCollectionId);
		}

		return layoutPageTemplateCollection;
	}

	private List<UtilityPageTemplateEntry> _getLayoutUtilityPageEntries(
			long groupId, ZipFile zipFile)
		throws Exception {

		List<UtilityPageTemplateEntry> utilityPageTemplateEntries =
			new ArrayList<>();

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if ((zipEntry == null) ||
				!_isUtilityPageTemplateFile(zipEntry.getName())) {

				continue;
			}

			String content = StringUtil.read(zipFile.getInputStream(zipEntry));

			UtilityPageTemplate utilityPageTemplate = null;

			try {
				UtilityPageTemplateValidator.validateUtilityPageTemplate(
					content);

				utilityPageTemplate = _objectMapper.readValue(
					content, UtilityPageTemplate.class);
			}
			catch (JSONValidatorException jsonValidatorException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid utility page template for: " +
							zipEntry.getName(),
						jsonValidatorException);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						zipEntry.getName(),
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-its-utility-" +
								"page-is-invalid",
							new String[] {zipEntry.getName()})));

				continue;
			}

			try {
				String pageDefinitionJSON = _getPageDefinitionJSON(
					zipEntry.getName(), zipFile);

				PageDefinitionValidator.validatePageDefinition(
					pageDefinitionJSON);

				utilityPageTemplateEntries.add(
					new UtilityPageTemplateEntry(
						utilityPageTemplate,
						_getKey(
							_UTILITY_PAGE_TEMPLATE_ENTRY_KEY_DEFAULT,
							utilityPageTemplate.getName(), zipEntry),
						_objectMapper.readValue(
							pageDefinitionJSON, PageDefinition.class),
						_getThumbnailZipEntry(zipEntry.getName(), zipFile),
						zipEntry.getName()));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page definition for: " +
							utilityPageTemplate.getName(),
						exception);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						utilityPageTemplate.getName(),
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid",
							new String[] {zipEntry.getName()})));
			}
		}

		return utilityPageTemplateEntries;
	}

	private List<MasterPageEntry> _getMasterPageEntries(
			long groupId, ZipFile zipFile)
		throws Exception {

		List<MasterPageEntry> masterPageEntries = new ArrayList<>();

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if ((zipEntry == null) || !_isMasterPageFile(zipEntry.getName())) {
				continue;
			}

			String content = StringUtil.read(zipFile.getInputStream(zipEntry));

			MasterPage masterPage = null;

			try {
				MasterPageValidator.validateMasterPage(content);

				masterPage = _objectMapper.readValue(content, MasterPage.class);
			}
			catch (JSONValidatorException jsonValidatorException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid master page for: " + zipEntry.getName(),
						jsonValidatorException);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						zipEntry.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_MASTER_LAYOUT,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-its-master-page-" +
								"is-invalid",
							new String[] {zipEntry.getName()})));

				continue;
			}

			try {
				String pageDefinitionJSON = _getPageDefinitionJSON(
					zipEntry.getName(), zipFile);

				PageDefinitionValidator.validatePageDefinition(
					pageDefinitionJSON);

				masterPageEntries.add(
					new MasterPageEntry(
						_getKey(
							_MASTER_PAGE_ENTRY_KEY_DEFAULT,
							masterPage.getName(), zipEntry),
						masterPage,
						_objectMapper.readValue(
							pageDefinitionJSON, PageDefinition.class),
						_getThumbnailZipEntry(zipEntry.getName(), zipFile),
						zipEntry.getName()));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page definition for: " + masterPage.getName(),
						exception);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						masterPage.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_MASTER_LAYOUT,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid",
							new String[] {zipEntry.getName()})));
			}
		}

		return masterPageEntries;
	}

	private String _getPageDefinitionJSON(String fileName, ZipFile zipFile)
		throws IOException {

		String path = fileName.substring(
			0, fileName.lastIndexOf(StringPool.FORWARD_SLASH) + 1);

		ZipEntry zipEntry = zipFile.getEntry(
			path +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_PAGE_DEFINITION);

		if (zipEntry == null) {
			return null;
		}

		return StringUtil.read(zipFile.getInputStream(zipEntry));
	}

	private Map<String, PageTemplateCollectionEntry>
			_getPageTemplateCollectionEntryMap(long groupId, ZipFile zipFile)
		throws Exception {

		Map<String, PageTemplateCollectionEntry> pageTemplateCollectionMap =
			new HashMap<>();

		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if ((zipEntry == null) ||
				!_isPageTemplateCollectionFile(zipEntry.getName())) {

				continue;
			}

			String[] pathParts = StringUtil.split(
				zipEntry.getName(), CharPool.SLASH);

			String pageTemplateCollectionKey = "imported";

			if (pathParts.length > 1) {
				pageTemplateCollectionKey = pathParts[pathParts.length - 2];
			}

			String content = StringUtil.read(zipFile.getInputStream(zipEntry));

			PageTemplateCollectionValidator.validatePageTemplateCollection(
				content);

			PageTemplateCollection pageTemplateCollection =
				_objectMapper.readValue(content, PageTemplateCollection.class);

			pageTemplateCollectionMap.put(
				pageTemplateCollectionKey,
				new PageTemplateCollectionEntry(
					pageTemplateCollectionKey, pageTemplateCollection));
		}

		enumeration = zipFile.entries();

		if (MapUtil.isEmpty(pageTemplateCollectionMap)) {
			pageTemplateCollectionMap.put(
				_PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT,
				_getDefaultPageTemplateCollectionEntry());
		}

		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();

			if ((zipEntry == null) ||
				!_isPageTemplateFile(zipEntry.getName())) {

				continue;
			}

			String content = StringUtil.read(zipFile.getInputStream(zipEntry));

			PageTemplate pageTemplate = null;

			try {
				PageTemplateValidator.validatePageTemplate(content);

				pageTemplate = _objectMapper.readValue(
					content, PageTemplate.class);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page template for: " + zipEntry.getName(),
						exception);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						zipEntry.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_BASIC,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-its-page-" +
								"template-is-invalid",
							new String[] {zipEntry.getName()})));

				continue;
			}

			PageTemplateCollectionEntry pageTemplateCollectionEntry =
				pageTemplateCollectionMap.get(
					_getPageTemplateCollectionKey(zipEntry.getName(), zipFile));

			try {
				String pageDefinitionJSON = _getPageDefinitionJSON(
					zipEntry.getName(), zipFile);

				PageDefinitionValidator.validatePageDefinition(
					pageDefinitionJSON);

				PageDefinition pageDefinition = _objectMapper.readValue(
					pageDefinitionJSON, PageDefinition.class);

				pageTemplateCollectionEntry.addPageTemplateEntry(
					_getKey(
						_PAGE_TEMPLATE_ENTRY_KEY_DEFAULT,
						pageTemplate.getName(), zipEntry),
					new PageTemplateEntry(
						pageTemplate, pageDefinition,
						_getThumbnailZipEntry(zipEntry.getName(), zipFile),
						zipEntry.getName()));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Invalid page definition for: " +
							pageTemplate.getName(),
						exception);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						pageTemplate.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_BASIC,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-its-page-" +
								"definition-is-invalid",
							new String[] {zipEntry.getName()})));
			}
		}

		return pageTemplateCollectionMap;
	}

	private String _getPageTemplateCollectionKey(
		String fileName, ZipFile zipFile) {

		if (fileName.lastIndexOf(CharPool.SLASH) == -1) {
			return "imported";
		}

		String path = fileName.substring(
			0, fileName.lastIndexOf(StringPool.FORWARD_SLASH));

		ZipEntry zipEntry = zipFile.getEntry(
			path + CharPool.SLASH +
				LayoutPageTemplateExportImportConstants.
					FILE_NAME_PAGE_TEMPLATE_COLLECTION);

		if (zipEntry == null) {
			return _getPageTemplateCollectionKey(path, zipFile);
		}

		int pos = path.lastIndexOf(CharPool.SLASH);

		String layoutPageTemplateCollectionKey = path.substring(pos + 1);

		if (Validator.isNotNull(layoutPageTemplateCollectionKey)) {
			return layoutPageTemplateCollectionKey;
		}

		return _PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT;
	}

	private long _getPreviewFileEntryId(
			long groupId, String className, long classPK, ZipEntry zipEntry,
			ZipFile zipFile)
		throws Exception {

		if (zipEntry == null) {
			return 0;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Repository repository = _portletFileRepository.fetchPortletRepository(
			groupId, LayoutAdminPortletKeys.GROUP_PAGES);

		if (repository == null) {
			repository = _portletFileRepository.addPortletRepository(
				groupId, LayoutAdminPortletKeys.GROUP_PAGES, serviceContext);
		}

		String imageFileName =
			classPK + "_preview." + FileUtil.getExtension(zipEntry.getName());

		byte[] bytes = null;

		try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
			bytes = FileUtil.getBytes(inputStream);
		}

		FileEntry fileEntry = _portletFileRepository.fetchPortletFileEntry(
			groupId, repository.getDlFolderId(), imageFileName);

		if (fileEntry != null) {
			_portletFileRepository.deletePortletFileEntry(
				fileEntry.getFileEntryId());
		}

		fileEntry = _portletFileRepository.addPortletFileEntry(
			groupId, serviceContext.getUserId(), className, classPK,
			LayoutAdminPortletKeys.GROUP_PAGES, repository.getDlFolderId(),
			bytes, imageFileName, MimeTypesUtil.getContentType(imageFileName),
			false);

		return fileEntry.getFileEntryId();
	}

	private String _getThemeId(long companyId, String themeName) {
		List<Theme> themes = ListUtil.filter(
			_themeLocalService.getThemes(companyId),
			theme -> Objects.equals(theme.getName(), themeName));

		if (ListUtil.isNotEmpty(themes)) {
			Theme theme = themes.get(0);

			return theme.getThemeId();
		}

		return null;
	}

	private ZipEntry _getThumbnailZipEntry(String fileName, ZipFile zipFile) {
		String path = fileName.substring(
			0, fileName.lastIndexOf(StringPool.FORWARD_SLASH) + 1);

		for (String thumbnailExtension : _THUMBNAIL_VALID_EXTENSIONS) {
			ZipEntry zipEntry = zipFile.getEntry(
				path + _THUMBNAIL_FILE_NAME + thumbnailExtension);

			if (zipEntry != null) {
				return zipEntry;
			}
		}

		return null;
	}

	private List<FragmentEntryLink> _importPageElement(
			Consumer<LayoutStructure> consumer, Layout layout,
			LayoutStructure layoutStructure, String parentItemId,
			String pageElementJSON, int position, long segmentsExperienceId)
		throws Exception {

		PageElement pageElement = _objectMapper.readValue(
			pageElementJSON, PageElement.class);

		List<FragmentEntryLink> fragmentEntryLinks = new ArrayList<>();

		_processPageElement(
			fragmentEntryLinks, layout, layoutStructure,
			LayoutStructureConstants.LATEST_PAGE_DEFINITION_VERSION,
			pageElement, parentItemId, position, segmentsExperienceId,
			new HashSet<>());

		consumer.accept(layoutStructure);

		return fragmentEntryLinks;
	}

	private boolean _isDisplayPageTemplateFile(String fileName) {
		if (fileName.endsWith(
				CharPool.SLASH +
					LayoutPageTemplateExportImportConstants.
						FILE_NAME_DISPLAY_PAGE_TEMPLATE)) {

			return true;
		}

		return false;
	}

	private boolean _isMasterPageFile(String fileName) {
		if (fileName.endsWith(
				CharPool.SLASH +
					LayoutPageTemplateExportImportConstants.
						FILE_NAME_MASTER_PAGE)) {

			return true;
		}

		return false;
	}

	private boolean _isPageTemplateCollectionFile(String fileName) {
		if (fileName.endsWith(
				CharPool.SLASH +
					LayoutPageTemplateExportImportConstants.
						FILE_NAME_PAGE_TEMPLATE_COLLECTION)) {

			return true;
		}

		return false;
	}

	private boolean _isPageTemplateFile(String fileName) {
		if (fileName.endsWith(
				CharPool.SLASH +
					LayoutPageTemplateExportImportConstants.
						FILE_NAME_PAGE_TEMPLATE)) {

			return true;
		}

		return false;
	}

	private boolean _isUtilityPageTemplateFile(String fileName) {
		if (fileName.endsWith(
				CharPool.SLASH +
					LayoutUtilityPageExportImportConstants.
						FILE_NAME_UTILITY_PAGE)) {

			return true;
		}

		return false;
	}

	private void _processBasicLayoutPageTemplateEntries(
			long groupId, long layoutPageTemplateCollectionId,
			boolean overwrite, ZipFile zipFile)
		throws Exception {

		Map<String, PageTemplateCollectionEntry>
			pageTemplateCollectionEntryMap = _getPageTemplateCollectionEntryMap(
				groupId, zipFile);

		for (Map.Entry<String, PageTemplateCollectionEntry> entry :
				pageTemplateCollectionEntryMap.entrySet()) {

			PageTemplateCollectionEntry pageTemplateCollectionEntry =
				entry.getValue();

			Map<String, PageTemplateEntry> pageTemplatesEntries =
				pageTemplateCollectionEntry.getPageTemplatesEntries();

			if (MapUtil.isEmpty(pageTemplatesEntries)) {
				continue;
			}

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_getLayoutPageTemplateCollection(
					groupId, layoutPageTemplateCollectionId,
					pageTemplateCollectionEntry, overwrite);

			_processPageTemplateEntries(
				groupId, layoutPageTemplateCollection, pageTemplatesEntries,
				overwrite, zipFile);
		}
	}

	private void _processDisplayPageTemplatePageTemplateEntries(
			long groupId, boolean overwrite, ZipFile zipFile)
		throws Exception {

		List<DisplayPageTemplateEntry> displayPageTemplateEntries =
			_getDisplayPageTemplateEntries(groupId, zipFile);

		for (DisplayPageTemplateEntry displayPageTemplateEntry :
				displayPageTemplateEntries) {

			Callable<Void> callable = new DisplayPagesImporterCallable(
				groupId, displayPageTemplateEntry, overwrite, zipFile);

			try {
				TransactionInvokerUtil.invoke(_transactionConfig, callable);
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(throwable, throwable);
				}

				DisplayPageTemplate displayPageTemplate =
					displayPageTemplateEntry.getDisplayPageTemplate();

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						displayPageTemplate.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_DISPLAY_PAGE,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-of-invalid-" +
								"values-in-its-page-definition",
							new String[] {displayPageTemplate.getName()})));
			}
		}
	}

	private LayoutPageTemplateEntry _processLayoutPageTemplateEntry(
			long classNameId, long classTypeId, long groupId,
			long layoutPageTemplateCollectionId,
			LayoutPageTemplateEntry layoutPageTemplateEntry, String name,
			PageDefinition pageDefinition, int layoutPageTemplateEntryType,
			boolean overwrite, ZipEntry thumbnailZipEntry, String zipPath,
			ZipFile zipFile)
		throws Exception {

		try {
			boolean added = false;

			if (layoutPageTemplateEntry == null) {
				if (classNameId == 0) {
					layoutPageTemplateEntry =
						_layoutPageTemplateEntryService.
							addLayoutPageTemplateEntry(
								groupId, layoutPageTemplateCollectionId, name,
								layoutPageTemplateEntryType, 0,
								WorkflowConstants.STATUS_APPROVED,
								ServiceContextThreadLocal.getServiceContext());
				}
				else {
					layoutPageTemplateEntry =
						_layoutPageTemplateEntryService.
							addLayoutPageTemplateEntry(
								groupId, layoutPageTemplateCollectionId,
								classNameId, classTypeId, name, 0,
								WorkflowConstants.STATUS_APPROVED,
								ServiceContextThreadLocal.getServiceContext());
				}

				added = true;
			}
			else if (overwrite) {
				_deleteExistingPortletPreferences(
					layoutPageTemplateEntry.getPlid());

				layoutPageTemplateEntry =
					_layoutPageTemplateEntryService.
						updateLayoutPageTemplateEntry(
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId(),
							name);

				added = true;
			}

			if (added) {
				Set<String> warningMessages = new HashSet<>();

				_processPageDefinition(
					layoutPageTemplateEntry.getPlid(), pageDefinition,
					warningMessages);

				long previewFileEntryId = _getPreviewFileEntryId(
					groupId, LayoutPageTemplateEntry.class.getName(),
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					thumbnailZipEntry, zipFile);

				layoutPageTemplateEntry =
					_layoutPageTemplateEntryService.
						updateLayoutPageTemplateEntry(
							layoutPageTemplateEntry.
								getLayoutPageTemplateEntryId(),
							previewFileEntryId);

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						name, layoutPageTemplateEntryType,
						LayoutsImporterResultEntry.Status.IMPORTED,
						warningMessages.toArray(new String[0])));
			}
			else {
				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						name, layoutPageTemplateEntryType,
						LayoutsImporterResultEntry.Status.IGNORED,
						_getErrorMessage(
							groupId, _MESSAGE_KEY_IGNORED,
							new String[] {
								zipPath,
								_toTypeName(layoutPageTemplateEntryType)
							})));
			}
		}
		catch (DropzoneLayoutStructureItemException
					dropzoneLayoutStructureItemException) {

			if (_log.isWarnEnabled()) {
				_log.warn(dropzoneLayoutStructureItemException);
			}

			throw new PortalException();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			_layoutsImporterResultEntries.add(
				new LayoutsImporterResultEntry(
					name, layoutPageTemplateEntryType,
					LayoutsImporterResultEntry.Status.INVALID,
					_getErrorMessage(
						groupId, _MESSAGE_KEY_INVALID,
						new String[] {
							zipPath, _toTypeName(layoutPageTemplateEntryType)
						})));

			return null;
		}

		return layoutPageTemplateEntry;
	}

	private void _processLayoutUtilityPageEntries(
			long groupId, boolean overwrite, ZipFile zipFile)
		throws Exception {

		List<UtilityPageTemplateEntry> utilityPageTemplateEntries =
			_getLayoutUtilityPageEntries(groupId, zipFile);

		for (UtilityPageTemplateEntry utilityPageTemplateEntry :
				utilityPageTemplateEntries) {

			Callable<Void> callable = new UtilityPageImporterCallable(
				groupId, utilityPageTemplateEntry, overwrite, zipFile);

			try {
				TransactionInvokerUtil.invoke(_transactionConfig, callable);
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(throwable, throwable);
				}

				UtilityPageTemplate utilityPageTemplate =
					utilityPageTemplateEntry.getUtilityPageTemplate();

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						utilityPageTemplate.getName(),
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-of-invalid-" +
								"values-in-its-page-definition",
							new String[] {utilityPageTemplate.getName()})));
			}
		}
	}

	private void _processLayoutUtilityPageTemplateEntry(
			String externalReferenceCode, long groupId,
			LayoutUtilityPageEntry layoutUtilityPageEntry, String name,
			PageDefinition pageDefinition, String type, boolean overwrite,
			ZipEntry thumbnailZipEntry, String zipPath, ZipFile zipFile)
		throws Exception {

		try {
			boolean added = false;

			if (layoutUtilityPageEntry == null) {
				layoutUtilityPageEntry =
					_layoutUtilityPageEntryService.addLayoutUtilityPageEntry(
						externalReferenceCode, groupId, 0, 0, false, name, type,
						0);

				added = true;
			}
			else if (overwrite) {
				_deleteExistingPortletPreferences(
					layoutUtilityPageEntry.getPlid());

				layoutUtilityPageEntry =
					_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
						layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
						name);

				added = true;
			}

			if (added) {
				Set<String> warningMessages = new HashSet<>();

				_processPageDefinition(
					layoutUtilityPageEntry.getPlid(), pageDefinition,
					warningMessages);

				long previewFileEntryId = _getPreviewFileEntryId(
					groupId, LayoutUtilityPageEntry.class.getName(),
					layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
					thumbnailZipEntry, zipFile);

				if (previewFileEntryId > 0) {
					_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
						layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
						previewFileEntryId);
				}

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						name, LayoutsImporterResultEntry.Status.IMPORTED,
						warningMessages.toArray(new String[0])));
			}
			else {
				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						name, LayoutsImporterResultEntry.Status.IGNORED,
						_getErrorMessage(
							groupId, _MESSAGE_KEY_IGNORED,
							new String[] {zipPath, "utility page"})));
			}
		}
		catch (DropzoneLayoutStructureItemException
					dropzoneLayoutStructureItemException) {

			if (_log.isWarnEnabled()) {
				_log.warn(dropzoneLayoutStructureItemException);
			}

			throw new PortalException();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			_layoutsImporterResultEntries.add(
				new LayoutsImporterResultEntry(
					name, LayoutsImporterResultEntry.Status.INVALID,
					_getErrorMessage(
						groupId, _MESSAGE_KEY_INVALID,
						new String[] {zipPath, "utility page"})));
		}
	}

	private void _processMasterLayoutPageTemplateEntries(
			long groupId, boolean overwrite, ZipFile zipFile)
		throws Exception {

		List<MasterPageEntry> masterPageEntries = _getMasterPageEntries(
			groupId, zipFile);

		for (MasterPageEntry masterPageEntry : masterPageEntries) {
			Callable<Void> callable = new MasterLayoutTemplatesImporterCallable(
				groupId, masterPageEntry, overwrite, zipFile);

			try {
				TransactionInvokerUtil.invoke(_transactionConfig, callable);
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(throwable, throwable);
				}

				MasterPage masterPage = masterPageEntry.getMasterPage();

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						masterPage.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_MASTER_LAYOUT,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-of-invalid-" +
								"values-in-its-page-definition",
							new String[] {masterPage.getName()})));
			}
		}
	}

	private void _processPageDefinition(
			long plid, PageDefinition pageDefinition,
			Set<String> warningMessages)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(plid);

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		if (pageDefinition != null) {
			PageElement pageElement = pageDefinition.getPageElement();

			if ((pageElement.getType() == PageElement.Type.ROOT) &&
				(pageElement.getPageElements() != null)) {

				double pageDefinitionVersion = GetterUtil.getDouble(
					pageDefinition.getVersion(), 1);
				int position = 0;

				for (PageElement childPageElement :
						pageElement.getPageElements()) {

					if (_processPageElement(
							new ArrayList<>(), layout, layoutStructure,
							pageDefinitionVersion, childPageElement,
							rootLayoutStructureItem.getItemId(), position,
							_segmentsExperienceLocalService.
								fetchDefaultSegmentsExperienceId(
									layout.getPlid()),
							warningMessages)) {

						position++;
					}
				}
			}

			Settings settings = pageDefinition.getSettings();

			layout = _layoutLocalService.fetchLayout(layout.getPlid());

			_updateLayoutSettings(layout, settings);
		}

		_updateLayoutPageTemplateStructure(layout, layoutStructure);

		_updateLayouts(plid);
	}

	private boolean _processPageElement(
			List<FragmentEntryLink> fragmentEntryLinks, Layout layout,
			LayoutStructure layoutStructure, double pageDefinitionVersion,
			PageElement pageElement, String parentItemId, int position,
			long segmentsExperienceId, Set<String> warningMessages)
		throws Exception {

		LayoutStructureItemImporter layoutStructureItemImporter =
			_layoutStructureItemImporterRegistry.getLayoutStructureItemImporter(
				pageElement.getType());

		LayoutStructureItem layoutStructureItem = null;

		if (layoutStructureItemImporter != null) {
			layoutStructureItem =
				layoutStructureItemImporter.addLayoutStructureItem(
					layoutStructure,
					new LayoutStructureItemImporterContext(
						layout, pageDefinitionVersion, parentItemId, position,
						segmentsExperienceId),
					pageElement, warningMessages);
		}
		else if (pageElement.getType() == PageElement.Type.ROOT) {
			layoutStructureItem = layoutStructure.getMainLayoutStructureItem();
		}
		else {
			return false;
		}

		if (layoutStructureItem == null) {
			return false;
		}

		if (layoutStructureItem instanceof FragmentStyledLayoutStructureItem) {
			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)layoutStructureItem;

			fragmentEntryLinks.add(
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentStyledLayoutStructureItem.
						getFragmentEntryLinkId()));
		}

		if (pageElement.getPageElements() == null) {
			return true;
		}

		int childPosition = 0;

		for (PageElement childPageElement : pageElement.getPageElements()) {
			if (_processPageElement(
					fragmentEntryLinks, layout, layoutStructure,
					pageDefinitionVersion, childPageElement,
					layoutStructureItem.getItemId(), childPosition,
					segmentsExperienceId, warningMessages)) {

				childPosition++;
			}
		}

		return true;
	}

	private void _processPageTemplateEntries(
			long groupId,
			LayoutPageTemplateCollection layoutPageTemplateCollection,
			Map<String, PageTemplateEntry> pageTemplateEntryMap,
			boolean overwrite, ZipFile zipFile)
		throws Exception {

		for (Map.Entry<String, PageTemplateEntry> entry :
				pageTemplateEntryMap.entrySet()) {

			PageTemplateEntry pageTemplateEntry = entry.getValue();

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(groupId, entry.getKey());

			Callable<Void> callable = new BasicLayoutsImporterCallable(
				groupId,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateEntry, overwrite, pageTemplateEntry, zipFile);

			try {
				TransactionInvokerUtil.invoke(_transactionConfig, callable);
			}
			catch (Throwable throwable) {
				if (_log.isWarnEnabled()) {
					_log.warn(throwable, throwable);
				}

				PageTemplate pageTemplate = pageTemplateEntry.getPageTemplate();

				_layoutsImporterResultEntries.add(
					new LayoutsImporterResultEntry(
						pageTemplate.getName(),
						LayoutPageTemplateEntryTypeConstants.TYPE_MASTER_LAYOUT,
						LayoutsImporterResultEntry.Status.INVALID,
						_getErrorMessage(
							groupId,
							"x-could-not-be-imported-because-of-invalid-" +
								"values-in-its-page-definition",
							new String[] {pageTemplate.getName()})));
			}
		}
	}

	private String _toTypeName(int layoutPageTemplateEntryType) {
		if (layoutPageTemplateEntryType ==
				LayoutPageTemplateEntryTypeConstants.TYPE_DISPLAY_PAGE) {

			return "display page template";
		}

		if (layoutPageTemplateEntryType ==
				LayoutPageTemplateEntryTypeConstants.TYPE_MASTER_LAYOUT) {

			return "master page";
		}

		if (layoutPageTemplateEntryType ==
				LayoutPageTemplateEntryTypeConstants.TYPE_BASIC) {

			return "page template";
		}

		return null;
	}

	private void _updateLayoutPageTemplateStructure(
			Layout layout, LayoutStructure layoutStructure)
		throws Exception {

		JSONObject jsonObject = layoutStructure.toJSONObject();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		if (layoutPageTemplateStructure != null) {
			_layoutPageTemplateStructureLocalService.
				deleteLayoutPageTemplateStructure(layoutPageTemplateStructure);
		}

		_layoutPageTemplateStructureLocalService.addLayoutPageTemplateStructure(
			layout.getUserId(), layout.getGroupId(), layout.getPlid(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()),
			jsonObject.toString(),
			ServiceContextThreadLocal.getServiceContext());

		for (FragmentEntryLink fragmentEntryLink :
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					layout.getGroupId(), layout.getPlid())) {

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					fragmentEntryLink);
			}
		}
	}

	private void _updateLayouts(long plid) throws Exception {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout = _layoutCopyHelper.copyLayout(layout, draftLayout);

		_layoutLocalService.updateStatus(
			draftLayout.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextThreadLocal.getServiceContext());
	}

	private void _updateLayoutSettings(Layout layout, Settings settings) {
		if (settings == null) {
			layout.setThemeId(null);

			layout.setColorSchemeId(null);

			_layoutLocalService.updateLayout(layout);

			return;
		}

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		Map<String, String> themeSettings =
			(Map<String, String>)settings.getThemeSettings();

		Set<Map.Entry<String, String>> entrySet = unicodeProperties.entrySet();

		entrySet.removeIf(
			entry -> {
				String key = entry.getKey();

				return key.startsWith("lfr-theme:");
			});

		if (themeSettings != null) {
			for (Map.Entry<String, String> entry : themeSettings.entrySet()) {
				unicodeProperties.put(entry.getKey(), entry.getValue());
			}

			layout.setTypeSettingsProperties(unicodeProperties);
		}

		if (Validator.isNotNull(settings.getThemeName())) {
			String themeId = _getThemeId(
				layout.getCompanyId(), settings.getThemeName());

			layout.setThemeId(themeId);
		}

		if (Validator.isNotNull(settings.getColorSchemeName())) {
			layout.setColorSchemeId(settings.getColorSchemeName());
		}

		if (Validator.isNotNull(settings.getCss())) {
			layout.setCss(settings.getCss());
		}

		MasterPage masterPage = settings.getMasterPage();

		if (masterPage != null) {
			LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						layout.getGroupId(), masterPage.getKey());

			if (masterLayoutPageTemplateEntry != null) {
				layout.setMasterLayoutPlid(
					masterLayoutPageTemplateEntry.getPlid());
			}
		}

		StyleBook styleBook = settings.getStyleBook();

		if (styleBook != null) {
			StyleBookEntry styleBookEntry =
				_styleBookEntryLocalService.fetchStyleBookEntry(
					layout.getGroupId(), styleBook.getKey());

			if (styleBookEntry != null) {
				layout.setStyleBookEntryId(
					styleBookEntry.getStyleBookEntryId());
			}
		}

		_layoutLocalService.updateLayout(layout);
	}

	private static final String _DISPLAY_PAGE_TEMPLATE_ENTRY_KEY_DEFAULT =
		"imported-display-page-template";

	private static final String _MASTER_PAGE_ENTRY_KEY_DEFAULT =
		"imported-master-page";

	private static final String _MESSAGE_KEY_IGNORED =
		"x-was-ignored-because-a-x-with-the-same-key-already-exists";

	private static final String _MESSAGE_KEY_INVALID =
		"x-could-not-be-imported-because-a-x-with-the-same-name-already-exists";

	private static final String _PAGE_TEMPLATE_COLLECTION_KEY_DEFAULT =
		"imported";

	private static final String _PAGE_TEMPLATE_ENTRY_KEY_DEFAULT = "imported";

	private static final String _THUMBNAIL_FILE_NAME = "thumbnail";

	private static final String[] _THUMBNAIL_VALID_EXTENSIONS = {
		".bmp", ".gif", ".jpeg", ".jpg", ".png", ".svg", ".tiff"
	};

	private static final String _UTILITY_PAGE_TEMPLATE_ENTRY_KEY_DEFAULT =
		"imported-utility-page";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutsImporterImpl.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper();
	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private LayoutCopyHelper _layoutCopyHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	private List<LayoutsImporterResultEntry> _layoutsImporterResultEntries;

	@Reference
	private LayoutStructureItemImporterRegistry
		_layoutStructureItemImporterRegistry;

	@Reference
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private ThemeLocalService _themeLocalService;

	private static class DisplayPageTemplateEntry {

		public DisplayPageTemplateEntry(
			DisplayPageTemplate displayPageTemplate, String key,
			PageDefinition pageDefinition, ZipEntry thumbnailZipEntry,
			String zipPath) {

			_displayPageTemplate = displayPageTemplate;
			_key = key;
			_pageDefinition = pageDefinition;
			_thumbnailZipEntry = thumbnailZipEntry;
			_zipPath = zipPath;
		}

		public DisplayPageTemplate getDisplayPageTemplate() {
			return _displayPageTemplate;
		}

		public String getKey() {
			return _key;
		}

		public PageDefinition getPageDefinition() {
			return _pageDefinition;
		}

		public ZipEntry getThumbnailZipEntry() {
			return _thumbnailZipEntry;
		}

		public String getZipPath() {
			return _zipPath;
		}

		private final DisplayPageTemplate _displayPageTemplate;
		private final String _key;
		private final PageDefinition _pageDefinition;
		private final ZipEntry _thumbnailZipEntry;
		private final String _zipPath;

	}

	private static class MasterPageEntry {

		public MasterPageEntry(
			String key, MasterPage masterPage, PageDefinition pageDefinition,
			ZipEntry thumbnailZipEntry, String zipPath) {

			_key = key;
			_masterPage = masterPage;
			_pageDefinition = pageDefinition;
			_thumbnailZipEntry = thumbnailZipEntry;
			_zipPath = zipPath;
		}

		public String getKey() {
			return _key;
		}

		public MasterPage getMasterPage() {
			return _masterPage;
		}

		public PageDefinition getPageDefinition() {
			return _pageDefinition;
		}

		public ZipEntry getThumbnailZipEntry() {
			return _thumbnailZipEntry;
		}

		public String getZipPath() {
			return _zipPath;
		}

		private final String _key;
		private final MasterPage _masterPage;
		private final PageDefinition _pageDefinition;
		private final ZipEntry _thumbnailZipEntry;
		private final String _zipPath;

	}

	private static class UtilityPageTemplateEntry {

		public UtilityPageTemplateEntry(
			UtilityPageTemplate utilityPageTemplate, String key,
			PageDefinition pageDefinition, ZipEntry thumbnailZipEntry,
			String zipPath) {

			_utilityPageTemplate = utilityPageTemplate;
			_key = key;
			_pageDefinition = pageDefinition;
			_thumbnailZipEntry = thumbnailZipEntry;
			_zipPath = zipPath;
		}

		public String getKey() {
			return _key;
		}

		public PageDefinition getPageDefinition() {
			return _pageDefinition;
		}

		public ZipEntry getThumbnailZipEntry() {
			return _thumbnailZipEntry;
		}

		public UtilityPageTemplate getUtilityPageTemplate() {
			return _utilityPageTemplate;
		}

		public String getZipPath() {
			return _zipPath;
		}

		private final String _key;
		private final PageDefinition _pageDefinition;
		private final ZipEntry _thumbnailZipEntry;
		private final UtilityPageTemplate _utilityPageTemplate;
		private final String _zipPath;

	}

	private class BasicLayoutsImporterCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			PageTemplate pageTemplate = _pageTemplateEntry.getPageTemplate();

			_processLayoutPageTemplateEntry(
				0, 0, _groupId, _layoutPageTemplateCollectionId,
				_layoutPageTemplateEntry, pageTemplate.getName(),
				_pageTemplateEntry.getPageDefinition(),
				LayoutPageTemplateEntryTypeConstants.TYPE_BASIC, _overwrite,
				_pageTemplateEntry.getThumbnailZipEntry(),
				_pageTemplateEntry.getZipPath(), _zipFile);

			return null;
		}

		private BasicLayoutsImporterCallable(
			long groupId, long layoutPageTemplateCollectionId,
			LayoutPageTemplateEntry layoutPageTemplateEntry, boolean overwrite,
			PageTemplateEntry pageTemplateEntry, ZipFile zipFile) {

			_groupId = groupId;
			_layoutPageTemplateCollectionId = layoutPageTemplateCollectionId;
			_layoutPageTemplateEntry = layoutPageTemplateEntry;
			_overwrite = overwrite;
			_pageTemplateEntry = pageTemplateEntry;
			_zipFile = zipFile;
		}

		private final long _groupId;
		private final long _layoutPageTemplateCollectionId;
		private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
		private final boolean _overwrite;
		private final PageTemplateEntry _pageTemplateEntry;
		private final ZipFile _zipFile;

	}

	private class DisplayPagesImporterCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			DisplayPageTemplate displayPageTemplate =
				_displayPageTemplateEntry.getDisplayPageTemplate();

			ContentType contentType = displayPageTemplate.getContentType();

			long classNameId = _portal.getClassNameId(
				contentType.getClassName());

			long classTypeId = _getClassTypeId(
				displayPageTemplate, classNameId);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						_groupId, _displayPageTemplateEntry.getKey());

			layoutPageTemplateEntry = _processLayoutPageTemplateEntry(
				classNameId, classTypeId, _groupId, 0, layoutPageTemplateEntry,
				displayPageTemplate.getName(),
				_displayPageTemplateEntry.getPageDefinition(),
				LayoutPageTemplateEntryTypeConstants.TYPE_DISPLAY_PAGE,
				_overwrite, _displayPageTemplateEntry.getThumbnailZipEntry(),
				_displayPageTemplateEntry.getZipPath(), _zipFile);

			boolean defaultTemplate = GetterUtil.getBoolean(
				displayPageTemplate.getDefaultTemplate());

			if ((layoutPageTemplateEntry != null) && defaultTemplate) {
				_layoutPageTemplateEntryLocalService.
					updateLayoutPageTemplateEntry(
						layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
						true);
			}

			return null;
		}

		private DisplayPagesImporterCallable(
			long groupId, DisplayPageTemplateEntry displayPageTemplateEntry,
			boolean overwrite, ZipFile zipFile) {

			_groupId = groupId;
			_displayPageTemplateEntry = displayPageTemplateEntry;
			_overwrite = overwrite;
			_zipFile = zipFile;
		}

		private long _getClassTypeId(
			DisplayPageTemplate displayPageTemplate, long classNameId) {

			ContentSubtype contentSubtype =
				displayPageTemplate.getContentSubtype();

			if (contentSubtype == null) {
				return 0;
			}

			Long subtypeId = contentSubtype.getSubtypeId();

			if (subtypeId != null) {
				return subtypeId;
			}

			String subtypeKey = contentSubtype.getSubtypeKey();

			if (Validator.isNull(subtypeKey)) {
				return 0;
			}

			InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFormVariationsProvider.class,
					_portal.getClassName(classNameId));

			if (infoItemFormVariationsProvider == null) {
				return 0;
			}

			InfoItemFormVariation infoItemFormVariation =
				infoItemFormVariationsProvider.getInfoItemFormVariation(
					_groupId, subtypeKey);

			if (infoItemFormVariation == null) {
				return 0;
			}

			return GetterUtil.getLong(infoItemFormVariation.getKey());
		}

		private final DisplayPageTemplateEntry _displayPageTemplateEntry;
		private final long _groupId;
		private final boolean _overwrite;
		private final ZipFile _zipFile;

	}

	private class MasterLayoutTemplatesImporterCallable
		implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			MasterPage masterPage = _masterPageEntry.getMasterPage();

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(
						_groupId, _masterPageEntry.getKey());

			_processLayoutPageTemplateEntry(
				0, 0, _groupId, 0, layoutPageTemplateEntry,
				masterPage.getName(), _masterPageEntry.getPageDefinition(),
				LayoutPageTemplateEntryTypeConstants.TYPE_MASTER_LAYOUT,
				_overwrite, _masterPageEntry.getThumbnailZipEntry(),
				_masterPageEntry.getZipPath(), _zipFile);

			return null;
		}

		private MasterLayoutTemplatesImporterCallable(
			long groupId, MasterPageEntry masterPageEntry, boolean overwrite,
			ZipFile zipFile) {

			_groupId = groupId;
			_masterPageEntry = masterPageEntry;
			_overwrite = overwrite;
			_zipFile = zipFile;
		}

		private final long _groupId;
		private final MasterPageEntry _masterPageEntry;
		private final boolean _overwrite;
		private final ZipFile _zipFile;

	}

	private class PageTemplateCollectionEntry {

		public PageTemplateCollectionEntry(
			String key, PageTemplateCollection pageTemplateCollection) {

			_key = key;
			_pageTemplateCollection = pageTemplateCollection;
		}

		public void addPageTemplateEntry(
			String key, PageTemplateEntry pageTemplateEntry) {

			_pageTemplateEntries.put(key, pageTemplateEntry);
		}

		public String getKey() {
			return _key;
		}

		public PageTemplateCollection getPageTemplateCollection() {
			return _pageTemplateCollection;
		}

		public Map<String, PageTemplateEntry> getPageTemplatesEntries() {
			return _pageTemplateEntries;
		}

		private final String _key;
		private final PageTemplateCollection _pageTemplateCollection;
		private final Map<String, PageTemplateEntry> _pageTemplateEntries =
			new HashMap<>();

	}

	private class PageTemplateEntry {

		public PageTemplateEntry(
			PageTemplate pageTemplate, PageDefinition pageDefinition,
			ZipEntry thumbnailZipEntry, String zipPath) {

			_pageTemplate = pageTemplate;
			_pageDefinition = pageDefinition;
			_thumbnailZipEntry = thumbnailZipEntry;
			_zipPath = zipPath;
		}

		public PageDefinition getPageDefinition() {
			return _pageDefinition;
		}

		public PageTemplate getPageTemplate() {
			return _pageTemplate;
		}

		public ZipEntry getThumbnailZipEntry() {
			return _thumbnailZipEntry;
		}

		public String getZipPath() {
			return _zipPath;
		}

		private final PageDefinition _pageDefinition;
		private final PageTemplate _pageTemplate;
		private final ZipEntry _thumbnailZipEntry;
		private final String _zipPath;

	}

	private class UtilityPageImporterCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			UtilityPageTemplate utilityPageTemplate =
				_utilityPageTemplateEntry.getUtilityPageTemplate();

			LayoutUtilityPageEntry layoutUtilityPageEntry =
				_layoutUtilityPageEntryLocalService.
					fetchLayoutUtilityPageEntryByExternalReferenceCode(
						utilityPageTemplate.getExternalReferenceCode(),
						_groupId);

			_processLayoutUtilityPageTemplateEntry(
				utilityPageTemplate.getExternalReferenceCode(), _groupId,
				layoutUtilityPageEntry, utilityPageTemplate.getName(),
				_utilityPageTemplateEntry.getPageDefinition(),
				UtilityPageTemplateUtil.convertToInternalValue(
					utilityPageTemplate.getTypeAsString()),
				_overwrite, _utilityPageTemplateEntry.getThumbnailZipEntry(),
				_utilityPageTemplateEntry.getZipPath(), _zipFile);

			return null;
		}

		private UtilityPageImporterCallable(
			long groupId, UtilityPageTemplateEntry utilityPageTemplateEntry,
			boolean overwrite, ZipFile zipFile) {

			_groupId = groupId;
			_utilityPageTemplateEntry = utilityPageTemplateEntry;
			_overwrite = overwrite;
			_zipFile = zipFile;
		}

		private final long _groupId;
		private final boolean _overwrite;
		private final UtilityPageTemplateEntry _utilityPageTemplateEntry;
		private final ZipFile _zipFile;

	}

}