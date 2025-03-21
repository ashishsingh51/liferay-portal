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

package com.liferay.portal.search.internal.searcher;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.asset.SearchableAssetClassNamesProvider;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.internal.expando.helper.ExpandoQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.AddSearchKeywordsQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.PostProcessSearchQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.PreFilterContributorHelper;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.searcher.helper.IndexSearcherHelper;
import com.liferay.portal.search.internal.test.util.DocumentFixture;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class FacetedSearcherImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_documentFixture = new DocumentFixture();

		_documentFixture.setUp();

		facetedSearcher = createFacetedSearcher();
	}

	@After
	public void tearDown() throws Exception {
		_documentFixture.tearDown();
	}

	@Test
	public void testEmptySearchDisabledBlank() throws Exception {
		SearchContext searchContext = new SearchContext();

		searchContext.setKeywords(StringPool.BLANK);

		_assertSearchSkipped(searchContext);
	}

	@Test
	public void testEmptySearchDisabledByDefault() throws Exception {
		_assertSearchSkipped(new SearchContext());
	}

	@Test
	public void testEmptySearchDisabledSpaces() throws Exception {
		SearchContext searchContext = new SearchContext();

		searchContext.setKeywords(StringPool.FOUR_SPACES);

		_assertSearchSkipped(searchContext);
	}

	@Test
	public void testEmptySearchEnabled() throws Exception {
		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH, Boolean.TRUE);
		searchContext.setEntryClassNames(
			new String[] {RandomTestUtil.randomString()});

		Hits hits = facetedSearcher.search(searchContext);

		Assert.assertNull(hits);

		Mockito.verify(
			indexSearcherHelper
		).search(
			Mockito.same(searchContext), Mockito.any()
		);
	}

	protected FacetedSearcherImpl createFacetedSearcher() {
		return new FacetedSearcherImpl(
			addSearchKeywordsQueryContributorHelper,
			expandoQueryContributorHelper, indexerRegistry, indexSearcherHelper,
			postProcessSearchQueryContributorHelper, preFilterContributorHelper,
			searchableAssetClassNamesProvider,
			_createSearchRequestBuilderFactory());
	}

	protected AddSearchKeywordsQueryContributorHelper
		addSearchKeywordsQueryContributorHelper = Mockito.mock(
			AddSearchKeywordsQueryContributorHelper.class);
	protected ExpandoQueryContributorHelper expandoQueryContributorHelper =
		Mockito.mock(ExpandoQueryContributorHelper.class);
	protected FacetedSearcher facetedSearcher;
	protected IndexerRegistry indexerRegistry = Mockito.mock(
		IndexerRegistry.class);
	protected IndexSearcherHelper indexSearcherHelper = Mockito.mock(
		IndexSearcherHelper.class);
	protected PostProcessSearchQueryContributorHelper
		postProcessSearchQueryContributorHelper = Mockito.mock(
			PostProcessSearchQueryContributorHelper.class);
	protected PreFilterContributorHelper preFilterContributorHelper =
		Mockito.mock(PreFilterContributorHelper.class);
	protected SearchableAssetClassNamesProvider
		searchableAssetClassNamesProvider = Mockito.mock(
			SearchableAssetClassNamesProvider.class);

	private void _assertSearchSkipped(SearchContext searchContext)
		throws Exception {

		Hits hits = facetedSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		Mockito.verifyNoInteractions(indexSearcherHelper);
	}

	private SearchRequestBuilderFactory _createSearchRequestBuilderFactory() {
		SearchRequestBuilderFactoryImpl searchRequestBuilderFactoryImpl =
			new SearchRequestBuilderFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			searchRequestBuilderFactoryImpl, "_searchRequestBuilderFactory",
			new com.liferay.portal.search.internal.searcher.
				SearchRequestBuilderFactoryImpl());

		return searchRequestBuilderFactoryImpl;
	}

	private DocumentFixture _documentFixture;

}