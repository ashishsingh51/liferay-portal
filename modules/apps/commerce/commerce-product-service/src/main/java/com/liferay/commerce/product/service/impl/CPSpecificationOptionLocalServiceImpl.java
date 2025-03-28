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

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.exception.CPSpecificationOptionKeyException;
import com.liferay.commerce.product.exception.CPSpecificationOptionTitleException;
import com.liferay.commerce.product.exception.DuplicateCPSpecificationOptionKeyException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.base.CPSpecificationOptionLocalServiceBaseImpl;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPSpecificationOption",
	service = AopService.class
)
public class CPSpecificationOptionLocalServiceImpl
	extends CPSpecificationOptionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPSpecificationOption addCPSpecificationOption(
			long userId, long cpOptionCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, boolean facetable, String key,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		key = StringUtil.replace(key, CharPool.UNDERLINE, CharPool.DASH);

		key = _friendlyURLNormalizer.normalize(key);

		_validate(0, user.getCompanyId(), titleMap, key);

		long cpSpecificationOptionId = counterLocalService.increment();

		CPSpecificationOption cpSpecificationOption =
			cpSpecificationOptionPersistence.create(cpSpecificationOptionId);

		cpSpecificationOption.setCompanyId(user.getCompanyId());
		cpSpecificationOption.setUserId(user.getUserId());
		cpSpecificationOption.setUserName(user.getFullName());
		cpSpecificationOption.setCPOptionCategoryId(cpOptionCategoryId);
		cpSpecificationOption.setTitleMap(titleMap);
		cpSpecificationOption.setDescriptionMap(descriptionMap);
		cpSpecificationOption.setFacetable(facetable);
		cpSpecificationOption.setKey(key);
		cpSpecificationOption.setExpandoBridgeAttributes(serviceContext);

		cpSpecificationOption = cpSpecificationOptionPersistence.update(
			cpSpecificationOption);

		// Resources

		_resourceLocalService.addModelResources(
			cpSpecificationOption, serviceContext);

		return cpSpecificationOption;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPSpecificationOption deleteCPSpecificationOption(
			CPSpecificationOption cpSpecificationOption)
		throws PortalException {

		// Commerce product specification option

		cpSpecificationOptionPersistence.remove(cpSpecificationOption);

		// Commerce product definition specification option values

		_cpDefinitionSpecificationOptionValueLocalService.
			deleteCPSpecificationOptionDefinitionValues(
				cpSpecificationOption.getCPSpecificationOptionId());

		// Resources

		_resourceLocalService.deleteResource(
			cpSpecificationOption, ResourceConstants.SCOPE_INDIVIDUAL);

		// Expando

		_expandoRowLocalService.deleteRows(
			cpSpecificationOption.getCPSpecificationOptionId());

		return cpSpecificationOption;
	}

	@Override
	public CPSpecificationOption deleteCPSpecificationOption(
			long cpSpecificationOptionId)
		throws PortalException {

		CPSpecificationOption cpSpecificationOption =
			cpSpecificationOptionPersistence.findByPrimaryKey(
				cpSpecificationOptionId);

		return cpSpecificationOptionLocalService.deleteCPSpecificationOption(
			cpSpecificationOption);
	}

	@Override
	public void deleteCPSpecificationOptions(long companyId)
		throws PortalException {

		List<CPSpecificationOption> cpSpecificationOptions =
			cpSpecificationOptionPersistence.findByCompanyId(companyId);

		for (CPSpecificationOption cpSpecificationOption :
				cpSpecificationOptions) {

			cpSpecificationOptionLocalService.deleteCPSpecificationOption(
				cpSpecificationOption);
		}
	}

	@Override
	public CPSpecificationOption fetchCPSpecificationOption(
		long companyId, String key) {

		return cpSpecificationOptionPersistence.fetchByC_K(
			companyId, _friendlyURLNormalizer.normalize(key));
	}

	@Override
	public CPSpecificationOption getCPSpecificationOption(
			long companyId, String key)
		throws PortalException {

		return cpSpecificationOptionPersistence.findByC_K(
			companyId, _friendlyURLNormalizer.normalize(key));
	}

	@Override
	public BaseModelSearchResult<CPSpecificationOption>
			searchCPSpecificationOptions(
				long companyId, Boolean facetable, String keywords, int start,
				int end, Sort sort)
		throws PortalException {

		SearchContext searchContext = _buildSearchContext(
			companyId, facetable, keywords, start, end, sort);

		return _searchCPSpecificationOptions(searchContext);
	}

	@Override
	public CPSpecificationOption updateCPOptionCategoryId(
			long cpSpecificationOptionId, long cpOptionCategoryId)
		throws PortalException {

		CPSpecificationOption cpSpecificationOption =
			cpSpecificationOptionPersistence.findByPrimaryKey(
				cpSpecificationOptionId);

		cpSpecificationOption.setCPOptionCategoryId(cpOptionCategoryId);

		return cpSpecificationOptionPersistence.update(cpSpecificationOption);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPSpecificationOption updateCPSpecificationOption(
			long cpSpecificationOptionId, long cpOptionCategoryId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			boolean facetable, String key, ServiceContext serviceContext)
		throws PortalException {

		CPSpecificationOption cpSpecificationOption =
			cpSpecificationOptionPersistence.findByPrimaryKey(
				cpSpecificationOptionId);

		key = StringUtil.replace(key, CharPool.UNDERLINE, CharPool.DASH);

		key = _friendlyURLNormalizer.normalize(key);

		_validate(
			cpSpecificationOption.getCPSpecificationOptionId(),
			cpSpecificationOption.getCompanyId(), titleMap, key);

		cpSpecificationOption.setCPOptionCategoryId(cpOptionCategoryId);
		cpSpecificationOption.setTitleMap(titleMap);
		cpSpecificationOption.setDescriptionMap(descriptionMap);
		cpSpecificationOption.setFacetable(facetable);
		cpSpecificationOption.setKey(key);
		cpSpecificationOption.setExpandoBridgeAttributes(serviceContext);

		cpSpecificationOption = cpSpecificationOptionPersistence.update(
			cpSpecificationOption);

		_reindexCPDefinitions1(
			cpSpecificationOption.getCompanyId(), cpSpecificationOptionId);

		return cpSpecificationOption;
	}

	private SearchContext _buildSearchContext(
		long companyId, Boolean facetable, String keywords, int start, int end,
		Sort sort) {

		SearchContext searchContext = new SearchContext();

		LinkedHashMap<String, Object> params =
			LinkedHashMapBuilder.<String, Object>put(
				"keywords", keywords
			).build();

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				CPField.CP_OPTION_CATEGORY_ID, keywords
			).put(
				CPField.CP_OPTION_CATEGORY_TITLE, keywords
			).put(
				CPField.FACETABLE, () -> facetable
			).put(
				CPField.KEY, keywords
			).put(
				Field.CONTENT, keywords
			).put(
				Field.DESCRIPTION, keywords
			).put(
				Field.ENTRY_CLASS_PK, keywords
			).put(
				Field.TITLE, keywords
			).put(
				"params", params
			).build());

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private List<CPSpecificationOption> _getCPSpecificationOptions(Hits hits)
		throws PortalException {

		List<Document> documents = hits.toList();

		List<CPSpecificationOption> cpSpecificationOptions = new ArrayList<>(
			documents.size());

		for (Document document : documents) {
			long cpSpecificationOptionId = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			CPSpecificationOption cpSpecificationOption =
				fetchCPSpecificationOption(cpSpecificationOptionId);

			if (cpSpecificationOption == null) {
				cpSpecificationOptions = null;

				Indexer<CPSpecificationOption> indexer =
					IndexerRegistryUtil.getIndexer(CPSpecificationOption.class);

				long companyId = GetterUtil.getLong(
					document.get(Field.COMPANY_ID));

				indexer.delete(companyId, document.getUID());
			}
			else if (cpSpecificationOptions != null) {
				cpSpecificationOptions.add(cpSpecificationOption);
			}
		}

		return cpSpecificationOptions;
	}

	private void _reindexCPDefinitions1(
		long companyId, long cpSpecificationOptionId) {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				_reindexCPDefinitions2(companyId, cpSpecificationOptionId);

				return null;
			});
	}

	private void _reindexCPDefinitions2(
			long companyId, long cpSpecificationOptionId)
		throws Exception {

		Indexer<CPDefinition> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			CPDefinition.class);

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_cpDefinitionSpecificationOptionValueLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"CPSpecificationOptionId", cpSpecificationOptionId)));
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue) -> {

				try {
					indexableActionableDynamicQuery.addDocuments(
						indexer.getDocument(
							cpDefinitionSpecificationOptionValue.
								getCPDefinition()));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						CPDefinition cpDefinition =
							cpDefinitionSpecificationOptionValue.
								getCPDefinition();

						_log.warn(
							"Unable to index commerce product definition " +
								cpDefinition,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private BaseModelSearchResult<CPSpecificationOption>
			_searchCPSpecificationOptions(SearchContext searchContext)
		throws PortalException {

		Indexer<CPSpecificationOption> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CPSpecificationOption.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<CPSpecificationOption> cpSpecificationOptions =
				_getCPSpecificationOptions(hits);

			if (cpSpecificationOptions != null) {
				return new BaseModelSearchResult<>(
					cpSpecificationOptions, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private void _validate(
			long cpSpecificationOptionId, long companyId,
			Map<Locale, String> titleMap, String key)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		String title = titleMap.get(locale);

		if (Validator.isNull(title)) {
			throw new CPSpecificationOptionTitleException();
		}

		if (Validator.isNull(key)) {
			throw new CPSpecificationOptionKeyException.MustNotBeNull();
		}

		CPSpecificationOption cpSpecificationOption =
			cpSpecificationOptionPersistence.fetchByC_K(companyId, key);

		if ((cpSpecificationOption != null) &&
			(cpSpecificationOption.getCPSpecificationOptionId() !=
				cpSpecificationOptionId)) {

			throw new DuplicateCPSpecificationOptionKeyException();
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID, Field.UID
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionLocalServiceImpl.class);

	@Reference
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}