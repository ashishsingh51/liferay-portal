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

package com.liferay.object.rest.resource.v1_0;

import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@ProviderType
public interface ObjectEntryResource {

	public Page<ObjectEntry> getObjectEntriesPage(
			Boolean flatten, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception;

	public ObjectEntry postObjectEntry(ObjectEntry objectEntry)
		throws Exception;

	public Response postObjectEntryBatch(String callbackURL, Object object)
		throws Exception;

	public ObjectEntry
			putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				String currentExternalReferenceCode,
				String objectRelationshipName,
				String relatedExternalReferenceCode)
		throws Exception;

	public void deleteByExternalReferenceCode(String externalReferenceCode)
		throws Exception;

	public ObjectEntry getByExternalReferenceCode(String externalReferenceCode)
		throws Exception;

	public ObjectEntry putByExternalReferenceCode(
			String externalReferenceCode, ObjectEntry objectEntry)
		throws Exception;

	public void
			putByExternalReferenceCodeObjectEntryExternalReferenceCodeObjectActionObjectActionName(
				String objectEntryExternalReferenceCode,
				String objectActionName)
		throws Exception;

	public void deleteScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception;

	public ObjectEntry getScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception;

	public ObjectEntry putScopeScopeKeyByExternalReferenceCode(
			String scopeKey, String externalReferenceCode,
			ObjectEntry objectEntry)
		throws Exception;

	public void
			putScopeScopeKeyByExternalReferenceCodeObjectActionObjectActionName(
				String scopeKey, String externalReferenceCode,
				String objectActionName)
		throws Exception;

	public void deleteObjectEntry(Long objectEntryId) throws Exception;

	public Response deleteObjectEntryBatch(String callbackURL, Object object)
		throws Exception;

	public ObjectEntry getObjectEntry(Long objectEntryId) throws Exception;

	public ObjectEntry patchObjectEntry(
			Long objectEntryId, ObjectEntry objectEntry)
		throws Exception;

	public ObjectEntry putObjectEntry(
			Long objectEntryId, ObjectEntry objectEntry)
		throws Exception;

	public Response putObjectEntryBatch(String callbackURL, Object object)
		throws Exception;

	public void putObjectEntryObjectActionObjectActionName(
			Long objectEntryId, String objectActionName)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			getObjectEntryPermissionsPage(Long objectEntryId, String roleNames)
		throws Exception;

	public Page<com.liferay.portal.vulcan.permission.Permission>
			putObjectEntryPermissionsPage(
				Long objectEntryId,
				com.liferay.portal.vulcan.permission.Permission[] permissions)
		throws Exception;

	public Page<ObjectEntry> getScopeScopeKeyPage(
			String scopeKey, Boolean flatten, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception;

	public ObjectEntry postScopeScopeKey(
			String scopeKey, ObjectEntry objectEntry)
		throws Exception;

	public default void setContextAcceptLanguage(
		AcceptLanguage contextAcceptLanguage) {
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany);

	public default void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {
	}

	public default void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {
	}

	public default void setContextUriInfo(UriInfo contextUriInfo) {
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser);

	public void setExpressionConvert(
		ExpressionConvert<Filter> expressionConvert);

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider);

	public void setGroupLocalService(GroupLocalService groupLocalService);

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService);

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService);

	public void setRoleLocalService(RoleLocalService roleLocalService);

	public void setSortParserProvider(SortParserProvider sortParserProvider);

	public void setVulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource);

	public default Filter toFilter(String filterString) {
		return toFilter(
			filterString, Collections.<String, List<String>>emptyMap());
	}

	public default Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		return null;
	}

	public default Sort[] toSorts(String sortsString) {
		return new Sort[0];
	}

	@ProviderType
	public interface Builder {

		public ObjectEntryResource build();

		public Builder checkPermissions(boolean checkPermissions);

		public Builder httpServletRequest(
			HttpServletRequest httpServletRequest);

		public Builder httpServletResponse(
			HttpServletResponse httpServletResponse);

		public Builder preferredLocale(Locale preferredLocale);

		public Builder user(com.liferay.portal.kernel.model.User user);

	}

	@ProviderType
	public interface Factory {

		public Builder create();

	}

}