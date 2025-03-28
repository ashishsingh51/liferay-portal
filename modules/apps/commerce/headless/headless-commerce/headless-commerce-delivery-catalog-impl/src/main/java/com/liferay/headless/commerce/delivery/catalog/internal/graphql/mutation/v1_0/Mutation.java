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

package com.liferay.headless.commerce.delivery.catalog.internal.graphql.mutation.v1_0;

import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishListItem;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.WishListItemResource;
import com.liferay.headless.commerce.delivery.catalog.resource.v1_0.WishListResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setWishListResourceComponentServiceObjects(
		ComponentServiceObjects<WishListResource>
			wishListResourceComponentServiceObjects) {

		_wishListResourceComponentServiceObjects =
			wishListResourceComponentServiceObjects;
	}

	public static void setWishListItemResourceComponentServiceObjects(
		ComponentServiceObjects<WishListItemResource>
			wishListItemResourceComponentServiceObjects) {

		_wishListItemResourceComponentServiceObjects =
			wishListItemResourceComponentServiceObjects;
	}

	@GraphQLField
	public WishList createChannelWishList(
			@GraphQLName("channelId") Long channelId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("wishList") WishList wishList)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListResource -> wishListResource.postChannelWishList(
				channelId, accountId, wishList));
	}

	@GraphQLField(description = "Deletes a wishlist by wishListId.")
	public boolean deleteWishList(@GraphQLName("wishListId") Long wishListId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wishListResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListResource -> wishListResource.deleteWishList(wishListId));

		return true;
	}

	@GraphQLField
	public Response deleteWishListBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListResource -> wishListResource.deleteWishListBatch(
				callbackURL, object));
	}

	@GraphQLField
	public WishList patchChannelWishList(
			@GraphQLName("wishListId") Long wishListId,
			@GraphQLName("wishList") WishList wishList)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListResource -> wishListResource.patchChannelWishList(
				wishListId, wishList));
	}

	@GraphQLField(description = "Deletes a wishlist item by wishListItemId.")
	public boolean deleteWishListItem(
			@GraphQLName("wishListItemId") Long wishListItemId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wishListItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListItemResource -> wishListItemResource.deleteWishListItem(
				wishListItemId));

		return true;
	}

	@GraphQLField
	public Response deleteWishListItemBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListItemResource ->
				wishListItemResource.deleteWishListItemBatch(
					callbackURL, object));
	}

	@GraphQLField
	public WishListItem createChannelWishListItem(
			@GraphQLName("wishListId") Long wishListId,
			@GraphQLName("accountId") Long accountId,
			@GraphQLName("wishListItem") WishListItem wishListItem)
		throws Exception {

		return _applyComponentServiceObjects(
			_wishListItemResourceComponentServiceObjects,
			this::_populateResourceContext,
			wishListItemResource ->
				wishListItemResource.postChannelWishListItem(
					wishListId, accountId, wishListItem));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(WishListResource wishListResource)
		throws Exception {

		wishListResource.setContextAcceptLanguage(_acceptLanguage);
		wishListResource.setContextCompany(_company);
		wishListResource.setContextHttpServletRequest(_httpServletRequest);
		wishListResource.setContextHttpServletResponse(_httpServletResponse);
		wishListResource.setContextUriInfo(_uriInfo);
		wishListResource.setContextUser(_user);
		wishListResource.setGroupLocalService(_groupLocalService);
		wishListResource.setRoleLocalService(_roleLocalService);

		wishListResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			WishListItemResource wishListItemResource)
		throws Exception {

		wishListItemResource.setContextAcceptLanguage(_acceptLanguage);
		wishListItemResource.setContextCompany(_company);
		wishListItemResource.setContextHttpServletRequest(_httpServletRequest);
		wishListItemResource.setContextHttpServletResponse(
			_httpServletResponse);
		wishListItemResource.setContextUriInfo(_uriInfo);
		wishListItemResource.setContextUser(_user);
		wishListItemResource.setGroupLocalService(_groupLocalService);
		wishListItemResource.setRoleLocalService(_roleLocalService);

		wishListItemResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<WishListResource>
		_wishListResourceComponentServiceObjects;
	private static ComponentServiceObjects<WishListItemResource>
		_wishListItemResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}