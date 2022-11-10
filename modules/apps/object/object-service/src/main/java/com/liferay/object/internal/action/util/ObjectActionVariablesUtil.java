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

package com.liferay.object.internal.action.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.system.SystemObjectDefinitionMetadata;
import com.liferay.object.system.SystemObjectDefinitionMetadataRegistry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class ObjectActionVariablesUtil {

	public static Map<String, Object> toVariables(
		DTOConverterRegistry dtoConverterRegistry,
		ObjectDefinition objectDefinition, JSONObject payloadJSONObject,
		SystemObjectDefinitionMetadataRegistry
			systemObjectDefinitionMetadataRegistry) {

		if (objectDefinition.isSystem()) {
			String contentType = _getContentType(
				dtoConverterRegistry, objectDefinition,
				systemObjectDefinitionMetadataRegistry);

			Object object = payloadJSONObject.get("modelDTO" + contentType);

			if (object == null) {
				return payloadJSONObject.toMap();
			}

			return HashMapBuilder.<String, Object>putAll(
				(Map<String, Object>)object
			).putAll(
				(Map<String, Object>)payloadJSONObject.get("extendedProperties")
			).put(
				"companyId", payloadJSONObject.getLong("companyId")
			).put(
				"creator", payloadJSONObject.get("userName")
			).put(
				"currentUserId", payloadJSONObject.getLong("userId")
			).put(
				"id", payloadJSONObject.getLong("classPK")
			).put(
				"objectDefinitionId",
				payloadJSONObject.getLong("objectDefinitionId")
			).put(
				"status", payloadJSONObject.get("status")
			).build();
		}

		Map<String, Object> variables = new HashMap<>(
			(Map)payloadJSONObject.get("objectEntry"));

		Object values = variables.get("values");

		if (values != null) {
			variables.putAll((Map<String, Object>)values);

			variables.remove("values");
		}

		variables.put("creator", variables.get("userName"));
		variables.put("currentUserId", payloadJSONObject.getLong("userId"));
		variables.put("id", payloadJSONObject.getLong("classPK"));

		return variables;
	}

	private static String _getContentType(
		DTOConverterRegistry dtoConverterRegistry,
		ObjectDefinition objectDefinition,
		SystemObjectDefinitionMetadataRegistry
			systemObjectDefinitionMetadataRegistry) {

		DTOConverter<?, ?> dtoConverter = dtoConverterRegistry.getDTOConverter(
			objectDefinition.getClassName());

		if (dtoConverter == null) {
			SystemObjectDefinitionMetadata systemObjectDefinitionMetadata =
				systemObjectDefinitionMetadataRegistry.
					getSystemObjectDefinitionMetadata(
						objectDefinition.getName());

			Class<?> modelClass =
				systemObjectDefinitionMetadata.getModelClass();

			return modelClass.getSimpleName();
		}

		return dtoConverter.getContentType();
	}

}