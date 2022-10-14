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

package com.liferay.portal.search.web.internal.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.search.facet.util.FacetFactory;
import com.liferay.portal.kernel.util.CalendarFactory;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactory;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.facet.modified.ModifiedFacetFactory;
import com.liferay.portal.search.web.facet.BaseJSPSearchFacet;
import com.liferay.portal.search.web.facet.SearchFacet;
import com.liferay.portal.search.web.internal.modified.facet.builder.DateRangeFactory;

import javax.portlet.ActionRequest;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(immediate = true, service = SearchFacet.class)
public class ModifiedSearchFacet extends BaseJSPSearchFacet {

	@Override
	public String getConfigurationJspPath() {
		return "/facets/configuration/modified.jsp";
	}

	@Override
	public FacetConfiguration getDefaultConfiguration(long companyId) {
		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setClassName(getFacetClassName());

		JSONArray jsonArray = jsonFactory.createJSONArray();

		for (int i = 0; i < _LABELS.length; i++) {
			jsonArray.put(
				JSONUtil.put(
					"label", _LABELS[i]
				).put(
					"range", _RANGES[i]
				));
		}

		facetConfiguration.setDataJSONObject(
			JSONUtil.put(
				"frequencyThreshold", 0
			).put(
				"ranges", jsonArray
			));
		facetConfiguration.setFieldName(getFieldName());
		facetConfiguration.setLabel(getLabel());
		facetConfiguration.setOrder(getOrder());
		facetConfiguration.setStatic(false);
		facetConfiguration.setWeight(1.0);

		return facetConfiguration;
	}

	@Override
	public String getDisplayJspPath() {
		return "/facets/view/modified.jsp";
	}

	@Override
	public Facet getFacet() {
		Facet facet = super.getFacet();

		FacetConfiguration facetConfiguration = facet.getFacetConfiguration();

		JSONObject jsonObject = facetConfiguration.getData();

		jsonObject.put(
			"ranges", _replaceAliases(jsonObject.getJSONArray("ranges")));

		return facet;
	}

	@Override
	public String getFacetClassName() {
		return modifiedFacetFactory.getFacetClassName();
	}

	@Override
	public String getFieldName() {
		Facet facet = modifiedFacetFactory.newInstance(null);

		return facet.getFieldName();
	}

	@Override
	public JSONObject getJSONData(ActionRequest actionRequest) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		String[] rangesIndexes = StringUtil.split(
			ParamUtil.getString(
				actionRequest, getClassName() + "rangesIndexes"));

		for (String rangesIndex : rangesIndexes) {
			jsonArray.put(
				JSONUtil.put(
					"label",
					ParamUtil.getString(
						actionRequest, getClassName() + "label_" + rangesIndex)
				).put(
					"range",
					ParamUtil.getString(
						actionRequest, getClassName() + "range_" + rangesIndex)
				));
		}

		return JSONUtil.put(
			"frequencyThreshold",
			ParamUtil.getInteger(
				actionRequest, getClassName() + "frequencyThreshold", 1)
		).put(
			"ranges", jsonArray
		);
	}

	@Override
	public String getLabel() {
		return "any-time";
	}

	@Override
	public String getTitle() {
		return "modified-date";
	}

	@Override
	protected FacetFactory getFacetFactory() {
		return modifiedFacetFactory;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	protected CalendarFactory calendarFactory;
	protected DateFormatFactory dateFormatFactory;
	protected JSONFactory jsonFactory;

	@Reference
	protected ModifiedFacetFactory modifiedFacetFactory;

	private CalendarFactory _getCalendarFactory() {

		// See LPS-72507 and LPS-76500

		if (calendarFactory != null) {
			return calendarFactory;
		}

		return CalendarFactoryUtil.getCalendarFactory();
	}

	private DateFormatFactory _getDateFormatFactory() {

		// See LPS-72507 and LPS-76500

		if (dateFormatFactory != null) {
			return dateFormatFactory;
		}

		return DateFormatFactoryUtil.getDateFormatFactory();
	}

	private JSONArray _replaceAliases(JSONArray rangesJSONArray) {
		DateRangeFactory dateRangeFactory = new DateRangeFactory(
			_getDateFormatFactory());

		CalendarFactory calendarFactory = _getCalendarFactory();

		return dateRangeFactory.replaceAliases(
			rangesJSONArray, calendarFactory.getCalendar(), _jsonFactory);
	}

	private static final String[] _LABELS = {
		"past-hour", "past-24-hours", "past-week", "past-month", "past-year"
	};

	private static final String[] _RANGES = {
		"[past-hour TO *]", "[past-24-hours TO *]", "[past-week TO *]",
		"[past-month TO *]", "[past-year TO *]"
	};

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.portal.search.web)")
	private ServletContext _servletContext;

}