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

package com.liferay.feature.flag.web.internal.constants;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.ArrayUtil;

/**
 * @author Drew Brokke
 */
public class FeatureFlagConstants {

	public static final String FEATURE_FLAG = "feature.flag";

	public static String getKey(String... parts) {
		if (ArrayUtil.isEmpty(parts)) {
			return FEATURE_FLAG;
		}

		return StringBundler.concat(
			FEATURE_FLAG, StringPool.PERIOD,
			StringUtil.merge(parts, StringPool.PERIOD));
	}

}