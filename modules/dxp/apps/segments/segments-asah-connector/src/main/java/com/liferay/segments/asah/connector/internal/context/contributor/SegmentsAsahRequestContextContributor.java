/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.segments.asah.connector.internal.context.contributor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.Context;
import com.liferay.segments.context.contributor.RequestContextContributor;

import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"request.context.contributor.key=" + SegmentsAsahRequestContextContributor.KEY_SEGMENTS_ANONYMOUS_USER_ID,
		"request.context.contributor.type=id"
	},
	service = RequestContextContributor.class
)
public class SegmentsAsahRequestContextContributor
	implements RequestContextContributor {

	public static final String KEY_SEGMENTS_ANONYMOUS_USER_ID =
		"segmentsAnonymousUserId";

	@Override
	public void contribute(
		Context context, HttpServletRequest httpServletRequest) {

		String segmentsAnonymousUserId = _getSegmentsAnonymousUserId(
			httpServletRequest);

		httpServletRequest.setAttribute(
			SegmentsWebKeys.SEGMENTS_ANONYMOUS_USER_ID,
			segmentsAnonymousUserId);

		context.put(KEY_SEGMENTS_ANONYMOUS_USER_ID, segmentsAnonymousUserId);
	}

	private String _getSegmentsAnonymousUserId(
		HttpServletRequest httpServletRequest) {

		Cookie[] cookies = httpServletRequest.getCookies();

		if (ArrayUtil.isEmpty(cookies)) {
			return StringPool.BLANK;
		}

		for (Cookie cookie : cookies) {
			if (Objects.equals(
					cookie.getName(), _AC_CLIENT_USER_ID_COOKIE_NAME)) {

				return cookie.getValue();
			}
		}

		return StringPool.BLANK;
	}

	private static final String _AC_CLIENT_USER_ID_COOKIE_NAME =
		"ac_client_user_id";

}