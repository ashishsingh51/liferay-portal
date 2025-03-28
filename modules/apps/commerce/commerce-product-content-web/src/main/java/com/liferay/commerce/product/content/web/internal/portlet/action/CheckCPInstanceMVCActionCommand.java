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

package com.liferay.commerce.product.content.web.internal.portlet.action;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.liferay.commerce.configuration.CommercePriceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.frontend.util.ProductHelper;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.util.CPContentHelper;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.util.CPContentContributor;
import com.liferay.commerce.product.util.CPContentContributorRegistry;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"javax.portlet.name=" + CPPortletKeys.CP_CONTENT_WEB,
		"mvc.command.name=/cp_content_web/check_cp_instance"
	},
	service = MVCActionCommand.class
)
public class CheckCPInstanceMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");
		String ddmFormValues = ParamUtil.getString(
			actionRequest, "ddmFormValues");
		int quantity = ParamUtil.getInteger(actionRequest, "quantity", 1);

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_commerceProductViewPermission.check(
			themeDisplay.getPermissionChecker(),
			CommerceUtil.getCommerceAccountId(commerceContext),
			commerceContext.getCommerceChannelGroupId(), cpDefinitionId);

		hideDefaultErrorMessage(actionRequest);
		hideDefaultSuccessMessage(actionRequest);

		try {
			CPInstance cpInstance = _cpInstanceHelper.fetchCPInstance(
				cpDefinitionId, ddmFormValues);

			JSONObject jsonObject = JSONUtil.put(
				"cpInstanceExist",
				() -> {
					if (cpInstance == null) {
						return false;
					}

					return true;
				}
			).put(
				"cpInstanceId",
				() -> {
					if (cpInstance == null) {
						return null;
					}

					return cpInstance.getCPInstanceId();
				}
			).put(
				"displayDiscountLevels",
				() -> {
					if (cpInstance == null) {
						return null;
					}

					CommercePriceConfiguration commercePriceConfiguration =
						_configurationProvider.getConfiguration(
							CommercePriceConfiguration.class,
							new SystemSettingsLocator(
								CommerceConstants.SERVICE_NAME_COMMERCE_PRICE));

					return commercePriceConfiguration.displayDiscountLevels();
				}
			).put(
				"gtin",
				() -> {
					if (cpInstance == null) {
						return null;
					}

					return cpInstance.getGtin();
				}
			).put(
				"incomingQuantityLabel",
				() -> {
					if (cpInstance == null) {
						return null;
					}

					return _cpContentHelper.getIncomingQuantityLabel(
						themeDisplay.getRequest(), cpInstance.getSku());
				}
			).put(
				"manufacturerPartNumber",
				() -> {
					if (cpInstance == null) {
						return null;
					}

					return cpInstance.getManufacturerPartNumber();
				}
			).put(
				"prices",
				() -> {
					if (cpInstance == null) {
						return null;
					}

					return _jsonFactory.createJSONObject(
						_OBJECT_MAPPER.writeValueAsString(
							_productHelper.getPriceModel(
								cpInstance.getCPInstanceId(), quantity,
								commerceContext, ddmFormValues,
								themeDisplay.getLocale())));
				}
			).put(
				"sku",
				() -> {
					if (cpInstance == null) {
						return null;
					}

					return cpInstance.getSku();
				}
			).put(
				"success", true
			);

			if (cpInstance != null) {
				List<CPContentContributor> cpContentContributors =
					_cpContentContributorRegistry.getCPContentContributors();

				for (CPContentContributor cpContentContributor :
						cpContentContributors) {

					JSONObject valueJSONObject = cpContentContributor.getValue(
						cpInstance,
						_portal.getHttpServletRequest(actionRequest));

					Iterator<String> iterator = valueJSONObject.keys();

					while (iterator.hasNext()) {
						String key = iterator.next();

						jsonObject.put(key, valueJSONObject.get(key));
					}
				}
			}

			writeJSON(actionResponse, jsonObject);
		}
		catch (Exception exception) {
			_log.error(exception);

			writeJSON(
				actionResponse,
				JSONUtil.put(
					"error", exception.getMessage()
				).put(
					"success", false
				));
		}
	}

	protected void writeJSON(ActionResponse actionResponse, Object object)
		throws IOException {

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(httpServletResponse, object.toString());

		httpServletResponse.flushBuffer();
	}

	private static final ObjectMapper _OBJECT_MAPPER = new ObjectMapper() {
		{
			configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
			disable(SerializationFeature.INDENT_OUTPUT);
		}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CheckCPInstanceMVCActionCommand.class);

	@Reference
	private CommerceOptionValueHelper _commerceOptionValueHelper;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPContentContributorRegistry _cpContentContributorRegistry;

	@Reference
	private CPContentHelper _cpContentHelper;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private ProductHelper _productHelper;

}