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

package com.liferay.headless.commerce.admin.catalog.internal.resource.v1_0;

import com.liferay.portal.vulcan.resource.OpenAPIResource;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zoltán Takács
 * @generated
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/openapi.properties",
	service = OpenAPIResourceImpl.class
)
@Generated("")
@OpenAPIDefinition(
	info = @Info(description = "Liferay Commerce Admin Catalog API. A Java client JAR is available for use with the group ID 'com.liferay', artifact ID 'com.liferay.headless.commerce.admin.catalog.client', and version '4.0.24'.", license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), title = "Liferay Commerce Admin Catalog API", version = "v1.0")
)
@Path("/v1.0")
public class OpenAPIResourceImpl {

	@GET
	@Path("/openapi.{type:json|yaml}")
	@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
	public Response getOpenAPI(@PathParam("type") String type)
		throws Exception {

		Class<? extends OpenAPIResource> clazz = _openAPIResource.getClass();

		try {
			Method method = clazz.getMethod(
				"getOpenAPI", HttpServletRequest.class, Set.class, String.class,
				UriInfo.class);

			return (Response)method.invoke(
				_openAPIResource, _httpServletRequest, _resourceClasses, type,
				_uriInfo);
		}
		catch (NoSuchMethodException noSuchMethodException1) {
			try {
				Method method = clazz.getMethod(
					"getOpenAPI", Set.class, String.class, UriInfo.class);

				return (Response)method.invoke(
					_openAPIResource, _resourceClasses, type, _uriInfo);
			}
			catch (NoSuchMethodException noSuchMethodException2) {
				return _openAPIResource.getOpenAPI(_resourceClasses, type);
			}
		}
	}

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private OpenAPIResource _openAPIResource;

	@Context
	private UriInfo _uriInfo;

	private final Set<Class<?>> _resourceClasses = new HashSet<Class<?>>() {
		{
			add(AttachmentResourceImpl.class);

			add(CatalogResourceImpl.class);

			add(CategoryResourceImpl.class);

			add(DiagramResourceImpl.class);

			add(GroupedProductResourceImpl.class);

			add(MappedProductResourceImpl.class);

			add(OptionResourceImpl.class);

			add(OptionCategoryResourceImpl.class);

			add(OptionValueResourceImpl.class);

			add(PinResourceImpl.class);

			add(ProductResourceImpl.class);

			add(ProductAccountGroupResourceImpl.class);

			add(ProductChannelResourceImpl.class);

			add(ProductConfigurationResourceImpl.class);

			add(ProductGroupResourceImpl.class);

			add(ProductGroupProductResourceImpl.class);

			add(ProductOptionResourceImpl.class);

			add(ProductOptionValueResourceImpl.class);

			add(ProductShippingConfigurationResourceImpl.class);

			add(ProductSpecificationResourceImpl.class);

			add(ProductSubscriptionConfigurationResourceImpl.class);

			add(ProductTaxConfigurationResourceImpl.class);

			add(RelatedProductResourceImpl.class);

			add(SkuResourceImpl.class);

			add(SpecificationResourceImpl.class);

			add(OpenAPIResourceImpl.class);
		}
	};

}