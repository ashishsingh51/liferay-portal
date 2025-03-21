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

package com.liferay.asset.categories.internal.struts;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 */
@Component(
	property = "path=/asset/get_categories", service = StrutsAction.class
)
public class GetCategoriesStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<AssetCategory> categories = getCategories(httpServletRequest);

		for (AssetCategory category : categories) {
			List<AssetCategory> childCategories =
				_assetCategoryService.getChildCategories(
					category.getCategoryId());

			jsonArray.put(
				JSONUtil.put(
					"categoryId", category.getCategoryId()
				).put(
					"childrenCount", childCategories.size()
				).put(
					"hasChildren", !childCategories.isEmpty()
				).put(
					"name", category.getName()
				).put(
					"parentCategoryId", category.getParentCategoryId()
				).put(
					"titleCurrentValue", category.getTitleCurrentValue()
				));
		}

		ServletResponseUtil.write(httpServletResponse, jsonArray.toString());

		return null;
	}

	protected List<AssetCategory> getCategories(
			HttpServletRequest httpServletRequest)
		throws Exception {

		long scopeGroupId = ParamUtil.getLong(
			httpServletRequest, "scopeGroupId");
		long categoryId = ParamUtil.getLong(httpServletRequest, "categoryId");
		long vocabularyId = ParamUtil.getLong(
			httpServletRequest, "vocabularyId");
		int start = ParamUtil.getInteger(
			httpServletRequest, "start", QueryUtil.ALL_POS);
		int end = ParamUtil.getInteger(
			httpServletRequest, "end", QueryUtil.ALL_POS);

		List<AssetCategory> categories = Collections.emptyList();

		if (categoryId > 0) {
			if (scopeGroupId > 0) {
				categories = _assetCategoryService.getVocabularyCategories(
					scopeGroupId, categoryId, vocabularyId, start, end, null);
			}
			else {
				categories = _assetCategoryService.getChildCategories(
					categoryId, start, end, null);
			}
		}
		else if (vocabularyId > 0) {
			long parentCategoryId = ParamUtil.getLong(
				httpServletRequest, "parentCategoryId",
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

			if (scopeGroupId > 0) {
				categories = _assetCategoryService.getVocabularyCategories(
					scopeGroupId, parentCategoryId, vocabularyId, start, end,
					null);
			}
			else {
				categories = _assetCategoryService.getVocabularyCategories(
					parentCategoryId, vocabularyId, start, end, null);
			}
		}

		return categories;
	}

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private JSONFactory _jsonFactory;

}