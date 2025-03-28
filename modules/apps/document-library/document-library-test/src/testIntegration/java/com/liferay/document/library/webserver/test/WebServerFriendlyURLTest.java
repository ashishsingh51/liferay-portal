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

package com.liferay.document.library.webserver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.BaseWebServerTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.webserver.WebServerServlet;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class WebServerFriendlyURLTest extends BaseWebServerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testHasFilesWithFileEntryFriendlyURLSeparator()
		throws Exception {

		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					_getFileEntryFriendlyURL(RandomTestUtil.randomString()))));

		String urlTitle = RandomTestUtil.randomString();

		_addFileEntry(RandomTestUtil.randomString(), urlTitle);

		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					_getFileEntryFriendlyURL(urlTitle))));
	}

	@Test
	public void testHasFilesWithFileEntryNameHasSpecialChars()
		throws Exception {

		String fileName = RandomTestUtil.randomString() + "+ .txt";

		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileName))));

		_addFileEntry(fileName, RandomTestUtil.randomString());

		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format("/%s/0/%s", group.getGroupId(), fileName))));
	}

	@Test
	public void testHasFilesWithGroupIdUUIDFriendlyURL() throws Exception {
		Assert.assertFalse(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format(
						"/%s/%s", group.getGroupId(),
						RandomTestUtil.randomString()))));

		String urlTitle = RandomTestUtil.randomString();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), urlTitle);

		Assert.assertTrue(
			WebServerServlet.hasFiles(
				_createMockHttpServletRequest(
					String.format(
						"/%s/%s", group.getGroupId(), fileEntry.getUuid()))));
	}

	@Test
	public void testServiceWithFileEntryFriendlyURLSeparator()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse = service(
			Method.GET, _getFileEntryFriendlyURL(RandomTestUtil.randomString()),
			Collections.emptyMap(), Collections.emptyMap(),
			TestPropsValues.getUser(), null);

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());

		String urlTitle = RandomTestUtil.randomString();

		_addFileEntry(RandomTestUtil.randomString(), urlTitle);

		mockHttpServletResponse = service(
			Method.GET, _getFileEntryFriendlyURL(urlTitle),
			Collections.emptyMap(), Collections.emptyMap(),
			TestPropsValues.getUser(), null);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	private FileEntry _addFileEntry(String fileName, String urlTitle)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), urlTitle,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			(byte[])null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private MockHttpServletRequest _createMockHttpServletRequest(String path)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(Method.GET, "/documents" + path);

		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());
		mockHttpServletRequest.setContextPath("/documents");
		mockHttpServletRequest.setPathInfo(path);
		mockHttpServletRequest.setServletPath(StringPool.BLANK);

		return mockHttpServletRequest;
	}

	private String _getFileEntryFriendlyURL(String urlTitle) {
		return String.format(
			"%s%s/%s", FriendlyURLResolverConstants.URL_SEPARATOR_X_FILE_ENTRY,
			group.getFriendlyURL(), urlTitle);
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Inject
	private Portal _portal;

}