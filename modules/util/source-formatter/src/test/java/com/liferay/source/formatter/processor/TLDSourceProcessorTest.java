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

package com.liferay.source.formatter.processor;

import org.junit.Test;

/**
 * @author David Zhang
 */
public class TLDSourceProcessorTest extends BaseSourceProcessorTestCase {

	@Test
	public void testIncorrectEmptyLines() throws Exception {
		test("IncorrectEmptyLines.testtld");
	}

	@Test
	public void testMissingCDATA() throws Exception {
		test(
			"MissingCDATA.testtld",
			new String[] {
				"Use CDATA to warp each '<code>' in the description",
				"Missing CDATA after 'replaced by' in the description"
			},
			new Integer[] {14, 19});
	}

	@Test
	public void testUnnecessaryCDATA() throws Exception {
		test("UnnecessaryCDATA.testtld");
	}

}