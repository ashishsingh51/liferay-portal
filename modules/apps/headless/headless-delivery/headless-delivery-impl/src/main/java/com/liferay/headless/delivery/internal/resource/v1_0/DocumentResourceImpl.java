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

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.resource.SPIRatingResource;
import com.liferay.headless.common.spi.service.context.ServiceContextRequestUtil;
import com.liferay.headless.delivery.dto.v1_0.ContentField;
import com.liferay.headless.delivery.dto.v1_0.CustomField;
import com.liferay.headless.delivery.dto.v1_0.Document;
import com.liferay.headless.delivery.dto.v1_0.DocumentType;
import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.dto.v1_0.util.CustomFieldsUtil;
import com.liferay.headless.delivery.dto.v1_0.util.DDMFormValuesUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.converter.DocumentDTOConverter;
import com.liferay.headless.delivery.internal.dto.v1_0.util.DisplayPageRendererUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RatingUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.DocumentEntityModel;
import com.liferay.headless.delivery.resource.v1_0.DocumentResource;
import com.liferay.headless.delivery.search.aggregation.AggregationUtil;
import com.liferay.headless.delivery.search.filter.FilterUtil;
import com.liferay.headless.delivery.search.sort.SortUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portlet.documentlibrary.constants.DLConstants;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/document.properties",
	scope = ServiceScope.PROTOTYPE, service = DocumentResource.class
)
public class DocumentResourceImpl extends BaseDocumentResourceImpl {

	@Override
	public void deleteAssetLibraryDocumentByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		FileEntry fileEntry = _dlAppService.getFileEntryByExternalReferenceCode(
			assetLibraryId, externalReferenceCode);

		_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());
	}

	@Override
	public void deleteDocument(Long documentId) throws Exception {
		_dlAppService.deleteFileEntry(documentId);
	}

	@Override
	public void deleteDocumentMyRating(Long documentId) throws Exception {
		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		spiRatingResource.deleteRating(documentId);
	}

	@Override
	public void deleteSiteDocumentByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		FileEntry fileEntry = _dlAppService.getFileEntryByExternalReferenceCode(
			siteId, externalReferenceCode);

		_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());
	}

	@Override
	public Document getAssetLibraryDocumentByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode)
		throws Exception {

		return _toDocument(
			_dlAppService.getFileEntryByExternalReferenceCode(
				assetLibraryId, externalReferenceCode));
	}

	@Override
	public Page<Document> getAssetLibraryDocumentsPage(
			Long assetLibraryId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getDocumentsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_DOCUMENT, "postAssetLibraryDocument",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_DOCUMENT, "postAssetLibraryDocumentBatch",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteDocumentBatch",
					DLConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getAssetLibraryDocumentsPage",
					DLConstants.RESOURCE_NAME, assetLibraryId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putDocumentBatch",
					DLConstants.RESOURCE_NAME, null)
			).build(),
			_createDocumentsPageBooleanQueryUnsafeConsumer(
				assetLibraryId, flatten),
			search, aggregation, filter, pagination, sorts);
	}

	@Override
	public Document getDocument(Long documentId) throws Exception {
		return _toDocument(_dlAppService.getFileEntry(documentId));
	}

	@Override
	public Page<Document> getDocumentFolderDocumentsPage(
			Long documentFolderId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		Folder folder = _dlAppService.getFolder(documentFolderId);

		return _getDocumentsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_DOCUMENT, folder.getFolderId(),
					"postDocumentFolderDocument", folder.getUserId(),
					DLConstants.RESOURCE_NAME, folder.getGroupId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, folder.getFolderId(),
					"getDocumentFolderDocumentsPage", folder.getUserId(),
					DLConstants.RESOURCE_NAME, folder.getGroupId())
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				String field = Field.FOLDER_ID;

				if (GetterUtil.getBoolean(flatten)) {
					field = Field.TREE_PATH;
				}

				booleanFilter.add(
					new TermFilter(field, String.valueOf(documentFolderId)),
					BooleanClauseOccur.MUST);
			},
			search, aggregation, filter, pagination, sorts);
	}

	@Override
	public Rating getDocumentMyRating(Long documentId) throws Exception {
		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.getRating(documentId);
	}

	@Override
	public String getDocumentRenderedContentByDisplayPageDisplayPageKey(
			Long documentId, String displayPageKey)
		throws Exception {

		FileEntry fileEntry = _dlAppService.getFileEntry(documentId);

		return DisplayPageRendererUtil.toHTML(
			FileEntry.class.getName(), _getDDMStructureId(fileEntry),
			displayPageKey, fileEntry.getGroupId(), contextHttpServletRequest,
			contextHttpServletResponse, fileEntry, _infoItemServiceRegistry,
			_layoutDisplayPageProviderRegistry, _layoutLocalService,
			_layoutPageTemplateEntryService);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new DocumentEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(DLFileEntry.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public Document getSiteDocumentByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		return _toDocument(
			_dlAppService.getFileEntryByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	public Page<Document> getSiteDocumentsPage(
			Long siteId, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getDocumentsPage(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_DOCUMENT, "postSiteDocument",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.ADD_DOCUMENT, "postSiteDocumentBatch",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"deleteBatch",
				addAction(
					ActionKeys.DELETE, "deleteDocumentBatch",
					DLConstants.RESOURCE_NAME, null)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getSiteDocumentsPage",
					DLConstants.RESOURCE_NAME, siteId)
			).put(
				"updateBatch",
				addAction(
					ActionKeys.UPDATE, "putDocumentBatch",
					DLConstants.RESOURCE_NAME, null)
			).build(),
			_createDocumentsPageBooleanQueryUnsafeConsumer(siteId, flatten),
			search, aggregation, filter, pagination, sorts);
	}

	@Override
	public Document patchDocument(Long documentId, MultipartBody multipartBody)
		throws Exception {

		FileEntry existingFileEntry = _dlAppService.getFileEntry(documentId);

		BinaryFile binaryFile = Optional.ofNullable(
			multipartBody.getBinaryFile("file")
		).orElse(
			new BinaryFile(
				existingFileEntry.getMimeType(),
				existingFileEntry.getFileName(),
				existingFileEntry.getContentStream(),
				existingFileEntry.getSize())
		);

		Document document = multipartBody.getValueAsNullableInstance(
			"document", Document.class);

		existingFileEntry = _moveDocument(
			documentId, document, existingFileEntry);

		String title = null;
		String description = null;

		if (document != null) {
			title = document.getTitle();
			description = document.getDescription();
		}

		if (title == null) {
			title = existingFileEntry.getTitle();
		}

		if (description == null) {
			existingFileEntry.getDescription();
		}

		return _toDocument(
			_dlAppService.updateFileEntry(
				documentId, binaryFile.getFileName(),
				binaryFile.getContentType(), title, null, description, null,
				DLVersionNumberIncrease.AUTOMATIC, binaryFile.getInputStream(),
				binaryFile.getSize(), existingFileEntry.getExpirationDate(),
				existingFileEntry.getReviewDate(),
				_createServiceContext(
					Constants.UPDATE,
					() -> ArrayUtil.toArray(
						_assetCategoryLocalService.getCategoryIds(
							DLFileEntry.class.getName(), documentId)),
					() -> _assetTagLocalService.getTagNames(
						DLFileEntry.class.getName(), documentId),
					existingFileEntry.getFolderId(), document,
					existingFileEntry.getGroupId())));
	}

	@Override
	public Document postAssetLibraryDocument(
			Long assetLibraryId, MultipartBody multipartBody)
		throws Exception {

		return postSiteDocument(assetLibraryId, multipartBody);
	}

	@Override
	public Document postDocumentFolderDocument(
			Long documentFolderId, MultipartBody multipartBody)
		throws Exception {

		Folder folder = _dlAppService.getFolder(documentFolderId);

		return _addDocument(
			null, folder.getGroupId(), folder.getRepositoryId(),
			documentFolderId, multipartBody);
	}

	@Override
	public Rating postDocumentMyRating(Long documentId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), documentId);
	}

	@Override
	public Document postSiteDocument(Long siteId, MultipartBody multipartBody)
		throws Exception {

		return _addDocument(null, siteId, siteId, null, multipartBody);
	}

	@Override
	public Document putAssetLibraryDocumentByExternalReferenceCode(
			Long assetLibraryId, String externalReferenceCode,
			MultipartBody multipartBody)
		throws Exception {

		FileEntry fileEntry =
			_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				assetLibraryId, externalReferenceCode);

		if (fileEntry != null) {
			return _updateDocument(fileEntry, multipartBody);
		}

		return _addDocument(
			externalReferenceCode, assetLibraryId, assetLibraryId, null,
			multipartBody);
	}

	@Override
	public Document putDocument(Long documentId, MultipartBody multipartBody)
		throws Exception {

		return _updateDocument(
			_dlAppService.getFileEntry(documentId), multipartBody);
	}

	@Override
	public Rating putDocumentMyRating(Long documentId, Rating rating)
		throws Exception {

		SPIRatingResource<Rating> spiRatingResource = _getSPIRatingResource();

		return spiRatingResource.addOrUpdateRating(
			rating.getRatingValue(), documentId);
	}

	@Override
	public Document putSiteDocumentByExternalReferenceCode(
			Long siteId, String externalReferenceCode,
			MultipartBody multipartBody)
		throws Exception {

		FileEntry fileEntry =
			_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				siteId, externalReferenceCode);

		if (fileEntry != null) {
			return _updateDocument(fileEntry, multipartBody);
		}

		return _addDocument(
			externalReferenceCode, siteId, siteId, null, multipartBody);
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		FileEntry fileEntry = _dlAppService.getFileEntry((Long)id);

		return fileEntry.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return DLConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return DLFileEntry.class.getName();
	}

	private Document _addDocument(
			String externalReferenceCode, Long groupId, Long repositoryId,
			Long documentFolderId, MultipartBody multipartBody)
		throws Exception {

		Document document = multipartBody.getValueAsNullableInstance(
			"document", Document.class);

		if ((externalReferenceCode == null) && (document != null)) {
			externalReferenceCode = document.getExternalReferenceCode();
		}

		if (documentFolderId == null) {
			documentFolderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile == null) {
			throw new BadRequestException("No file found in body");
		}

		String title = null;
		String description = null;

		if (document != null) {
			title = document.getTitle();
			description = document.getDescription();
		}

		if (title == null) {
			title = binaryFile.getFileName();
		}

		return _toDocument(
			_dlAppService.addFileEntry(
				externalReferenceCode, repositoryId, documentFolderId,
				binaryFile.getFileName(), binaryFile.getContentType(), title,
				null, description, null, binaryFile.getInputStream(),
				binaryFile.getSize(), null, null,
				_createServiceContext(
					Constants.ADD, () -> new Long[0], () -> new String[0],
					documentFolderId, document, groupId)));
	}

	private UnsafeConsumer<BooleanQuery, Exception>
		_createDocumentsPageBooleanQueryUnsafeConsumer(
			Long siteId, Boolean flatten) {

		return booleanQuery -> {
			BooleanFilter booleanFilter = booleanQuery.getPreBooleanFilter();

			if (!GetterUtil.getBoolean(flatten)) {
				booleanFilter.add(
					new TermFilter(
						Field.FOLDER_ID,
						String.valueOf(
							DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)),
					BooleanClauseOccur.MUST);
			}

			if (siteId != null) {
				booleanFilter.add(
					new TermFilter(Field.GROUP_ID, String.valueOf(siteId)),
					BooleanClauseOccur.MUST);
			}
		};
	}

	private ServiceContext _createServiceContext(
			String command, Supplier<Long[]> defaultCategoriesSupplier,
			Supplier<String[]> defaultKeywordsSupplier, Long documentFolderId,
			Document document, Long groupId)
		throws Exception {

		Long[] assetCategoryIds = null;
		String[] assetTagNames = null;
		String viewableBy = null;
		CustomField[] customFields = null;

		if (document != null) {
			assetCategoryIds = document.getTaxonomyCategoryIds();
			assetTagNames = document.getKeywords();
			viewableBy = document.getViewableByAsString();
			customFields = document.getCustomFields();
		}

		if (assetCategoryIds == null) {
			assetCategoryIds = defaultCategoriesSupplier.get();
		}

		if (assetTagNames == null) {
			assetTagNames = defaultKeywordsSupplier.get();
		}

		if (viewableBy == null) {
			viewableBy = Document.ViewableBy.OWNER.getValue();
		}

		ServiceContext serviceContext =
			ServiceContextRequestUtil.createServiceContext(
				assetCategoryIds, assetTagNames,
				CustomFieldsUtil.toMap(
					DLFileEntry.class.getName(), contextCompany.getCompanyId(),
					customFields, contextAcceptLanguage.getPreferredLocale()),
				groupId, contextHttpServletRequest, viewableBy);

		serviceContext.setCommand(command);
		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setPlid(
			_portal.getControlPanelPlid(contextCompany.getCompanyId()));
		serviceContext.setRequest(contextHttpServletRequest);
		serviceContext.setUserId(contextUser.getUserId());

		if (contextHttpServletRequest != null) {
			_initThemeDisplay(
				groupId, contextHttpServletRequest, contextHttpServletResponse);
		}

		DLFileEntryType dlFileEntryType = _getDLFileEntryType(
			documentFolderId, document, groupId);

		if (dlFileEntryType != null) {
			serviceContext.setAttribute(
				"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

			List<DDMStructure> ddmStructures =
				dlFileEntryType.getDDMStructures();

			DocumentType documentType = document.getDocumentType();

			ContentField[] contentFields = documentType.getContentFields();

			for (DDMStructure ddmStructure : ddmStructures) {
				com.liferay.dynamic.data.mapping.model.DDMStructure
					modelDDMStructure = _ddmStructureService.getStructure(
						ddmStructure.getStructureId());

				DDMForm ddmForm = modelDDMStructure.getDDMForm();

				com.liferay.dynamic.data.mapping.storage.DDMFormValues
					ddmFormValues = DDMFormValuesUtil.toDDMFormValues(
						ddmForm.getAvailableLocales(), contentFields, ddmForm,
						_dlAppService, groupId, _journalArticleService,
						_layoutLocalService,
						contextAcceptLanguage.getPreferredLocale(),
						transform(
							ddmStructure.getRootFieldNames(),
							modelDDMStructure::getDDMFormField));

				serviceContext.setAttribute(
					DDMFormValues.class.getName() + StringPool.POUND +
						ddmStructure.getStructureId(),
					_ddmBeanTranslator.translate(ddmFormValues));
			}
		}

		return serviceContext;
	}

	private long _getDDMStructureId(FileEntry fileEntry) throws Exception {
		if (!(fileEntry.getModel() instanceof DLFileEntry)) {
			return 0;
		}

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				dlFileEntry.getFileEntryTypeId());

		if ((dlFileEntryType == null) ||
			(dlFileEntryType.getDataDefinitionId() == 0)) {

			return 0;
		}

		com.liferay.dynamic.data.mapping.model.DDMStructure ddmStructure =
			_ddmStructureService.getStructure(
				dlFileEntryType.getDataDefinitionId());

		return ddmStructure.getStructureId();
	}

	private DLFileEntryType _getDLFileEntryType(
		long documentFolderId, Document document, Long groupId) {

		if (document == null) {
			return null;
		}

		DocumentType documentType = document.getDocumentType();

		if (documentType == null) {
			return null;
		}

		String name = documentType.getName();

		if (name == null) {
			return null;
		}

		try {
			for (DLFileEntryType dlFileEntryType :
					_dlFileEntryTypeLocalService.getFolderFileEntryTypes(
						new long[] {groupId}, documentFolderId, true)) {

				if (name.equals(
						dlFileEntryType.getName(
							contextAcceptLanguage.getPreferredLocale()))) {

					return dlFileEntryType;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private Page<Document> _getDocumentsPage(
			Map<String, Map<String, String>> actions,
			UnsafeConsumer<BooleanQuery, Exception> booleanQueryUnsafeConsumer,
			String keywords, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions, booleanQueryUnsafeConsumer,
			FilterUtil.processFilter(_ddmIndexer, filter), _indexer, keywords,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());

				SearchRequestBuilder searchRequestBuilder =
					_searchRequestBuilderFactory.builder(searchContext);

				AggregationUtil.processVulcanAggregation(
					_aggregations, _ddmIndexer, _queries, searchRequestBuilder,
					aggregation);

				SortUtil.processSorts(
					_ddmIndexer, searchRequestBuilder, searchContext.getSorts(),
					_queries, _sorts);
			},
			sorts,
			document -> _toDocument(
				_dlAppService.getFileEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	private SPIRatingResource<Rating> _getSPIRatingResource() {
		return new SPIRatingResource<>(
			DLFileEntry.class.getName(), _ratingsEntryLocalService,
			ratingsEntry -> {
				FileEntry fileEntry = _dlAppService.getFileEntry(
					ratingsEntry.getClassPK());

				return RatingUtil.toRating(
					HashMapBuilder.put(
						"create",
						addAction(
							ActionKeys.VIEW, fileEntry.getPrimaryKey(),
							"postDocumentMyRating", fileEntry.getUserId(),
							DLFileEntry.class.getName(), fileEntry.getGroupId())
					).put(
						"delete",
						addAction(
							ActionKeys.VIEW, fileEntry.getPrimaryKey(),
							"deleteDocumentMyRating", fileEntry.getUserId(),
							DLFileEntry.class.getName(), fileEntry.getGroupId())
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, fileEntry.getPrimaryKey(),
							"getDocumentMyRating", fileEntry.getUserId(),
							DLFileEntry.class.getName(), fileEntry.getGroupId())
					).put(
						"replace",
						addAction(
							ActionKeys.VIEW, fileEntry.getPrimaryKey(),
							"putDocumentMyRating", fileEntry.getUserId(),
							DLFileEntry.class.getName(), fileEntry.getGroupId())
					).build(),
					_portal, ratingsEntry, _userLocalService);
			},
			contextUser);
	}

	private void _initThemeDisplay(
			long groupId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ServicePreAction servicePreAction = new ServicePreAction();

		servicePreAction.servicePre(
			httpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(httpServletRequest, httpServletResponse);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setScopeGroupId(groupId);
		themeDisplay.setSiteGroupId(groupId);
	}

	private FileEntry _moveDocument(
			Long documentId, Document document, FileEntry existingFileEntry)
		throws Exception {

		Long folderId = null;

		if ((document != null) && (document.getDocumentFolderId() != null) &&
			(document.getDocumentFolderId() !=
				existingFileEntry.getFolderId())) {

			folderId = document.getDocumentFolderId();
		}

		if (folderId != null) {
			return _dlAppService.moveFileEntry(
				documentId, folderId, new ServiceContext());
		}

		return existingFileEntry;
	}

	private Document _toDocument(FileEntry fileEntry) throws Exception {
		return _documentDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, fileEntry.getPrimaryKey(),
						"deleteDocument", fileEntry.getUserId(),
						DLFileEntry.class.getName(), fileEntry.getGroupId())
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, fileEntry.getPrimaryKey(),
						"getDocument", fileEntry.getUserId(),
						DLFileEntry.class.getName(), fileEntry.getGroupId())
				).put(
					"get-rendered-content-by-display-page",
					addAction(
						ActionKeys.VIEW, fileEntry.getPrimaryKey(),
						"getDocumentRenderedContentByDisplayPageDisplayPageKey",
						fileEntry.getUserId(), DLFileEntry.class.getName(),
						fileEntry.getGroupId())
				).put(
					"replace",
					addAction(
						ActionKeys.UPDATE, fileEntry.getPrimaryKey(),
						"putDocument", fileEntry.getUserId(),
						DLFileEntry.class.getName(), fileEntry.getGroupId())
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, fileEntry.getPrimaryKey(),
						"patchDocument", fileEntry.getUserId(),
						DLFileEntry.class.getName(), fileEntry.getGroupId())
				).build(),
				_dtoConverterRegistry, fileEntry.getFileEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private Document _updateDocument(
			FileEntry fileEntry, MultipartBody multipartBody)
		throws Exception {

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		Document document = multipartBody.getValueAsNullableInstance(
			"document", Document.class);

		if ((binaryFile == null) && (document == null)) {
			throw new BadRequestException(
				"Document and file are not found in body");
		}

		if (binaryFile == null) {
			binaryFile = new BinaryFile(
				fileEntry.getMimeType(), fileEntry.getFileName(),
				fileEntry.getContentStream(), fileEntry.getSize());
		}

		fileEntry = _moveDocument(
			fileEntry.getFileEntryId(), document, fileEntry);

		String title = null;

		String description = null;

		if (document != null) {
			title = document.getTitle();
			description = document.getDescription();
		}

		if (title == null) {
			title = fileEntry.getTitle();
		}

		return _toDocument(
			_dlAppService.updateFileEntry(
				fileEntry.getFileEntryId(), binaryFile.getFileName(),
				binaryFile.getContentType(), title, null, description, null,
				DLVersionNumberIncrease.AUTOMATIC, binaryFile.getInputStream(),
				binaryFile.getSize(), fileEntry.getExpirationDate(),
				fileEntry.getReviewDate(),
				_createServiceContext(
					Constants.UPDATE, () -> new Long[0], () -> new String[0],
					fileEntry.getFolderId(), document,
					fileEntry.getGroupId())));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentResourceImpl.class);

	@Reference
	private Aggregations _aggregations;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DDMBeanTranslator _ddmBeanTranslator;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DocumentDTOConverter _documentDTOConverter;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference(
		target = "(indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	private Indexer<?> _indexer;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private Queries _queries;

	@Reference
	private RatingsEntryLocalService _ratingsEntryLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

	@Reference
	private UserLocalService _userLocalService;

}