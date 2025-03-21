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

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.UtilityPageTemplate;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class UtilityPageTemplateSerDes {

	public static UtilityPageTemplate toDTO(String json) {
		UtilityPageTemplateJSONParser utilityPageTemplateJSONParser =
			new UtilityPageTemplateJSONParser();

		return utilityPageTemplateJSONParser.parseToDTO(json);
	}

	public static UtilityPageTemplate[] toDTOs(String json) {
		UtilityPageTemplateJSONParser utilityPageTemplateJSONParser =
			new UtilityPageTemplateJSONParser();

		return utilityPageTemplateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UtilityPageTemplate utilityPageTemplate) {
		if (utilityPageTemplate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (utilityPageTemplate.getDefaultTemplate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultTemplate\": ");

			sb.append(utilityPageTemplate.getDefaultTemplate());
		}

		if (utilityPageTemplate.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(utilityPageTemplate.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (utilityPageTemplate.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(utilityPageTemplate.getName()));

			sb.append("\"");
		}

		if (utilityPageTemplate.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(utilityPageTemplate.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UtilityPageTemplateJSONParser utilityPageTemplateJSONParser =
			new UtilityPageTemplateJSONParser();

		return utilityPageTemplateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		UtilityPageTemplate utilityPageTemplate) {

		if (utilityPageTemplate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (utilityPageTemplate.getDefaultTemplate() == null) {
			map.put("defaultTemplate", null);
		}
		else {
			map.put(
				"defaultTemplate",
				String.valueOf(utilityPageTemplate.getDefaultTemplate()));
		}

		if (utilityPageTemplate.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(utilityPageTemplate.getExternalReferenceCode()));
		}

		if (utilityPageTemplate.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(utilityPageTemplate.getName()));
		}

		if (utilityPageTemplate.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(utilityPageTemplate.getType()));
		}

		return map;
	}

	public static class UtilityPageTemplateJSONParser
		extends BaseJSONParser<UtilityPageTemplate> {

		@Override
		protected UtilityPageTemplate createDTO() {
			return new UtilityPageTemplate();
		}

		@Override
		protected UtilityPageTemplate[] createDTOArray(int size) {
			return new UtilityPageTemplate[size];
		}

		@Override
		protected void setField(
			UtilityPageTemplate utilityPageTemplate, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultTemplate")) {
				if (jsonParserFieldValue != null) {
					utilityPageTemplate.setDefaultTemplate(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					utilityPageTemplate.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					utilityPageTemplate.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					utilityPageTemplate.setType(
						UtilityPageTemplate.Type.create(
							(String)jsonParserFieldValue));
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			Class<?> valueClass = value.getClass();

			if (value instanceof Map) {
				sb.append(_toJSON((Map)value));
			}
			else if (valueClass.isArray()) {
				Object[] values = (Object[])value;

				sb.append("[");

				for (int i = 0; i < values.length; i++) {
					sb.append("\"");
					sb.append(_escape(values[i]));
					sb.append("\"");

					if ((i + 1) < values.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(entry.getValue()));
				sb.append("\"");
			}
			else {
				sb.append(String.valueOf(entry.getValue()));
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

}