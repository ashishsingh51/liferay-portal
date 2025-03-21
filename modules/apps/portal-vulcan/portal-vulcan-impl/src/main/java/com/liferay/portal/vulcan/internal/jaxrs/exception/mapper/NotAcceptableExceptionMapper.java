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

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.Response;

/**
 * Converts any {@code NotAcceptableException} to a {@code 400} error.
 *
 * @author Javier de Arcos
 */
public class NotAcceptableExceptionMapper
	extends BaseExceptionMapper<NotAcceptableException> {

	@Override
	protected Problem getProblem(
		NotAcceptableException notAcceptableException) {

		_log.error(notAcceptableException);

		return new Problem(
			Response.Status.NOT_ACCEPTABLE,
			notAcceptableException.getMessage());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotAcceptableExceptionMapper.class);

}