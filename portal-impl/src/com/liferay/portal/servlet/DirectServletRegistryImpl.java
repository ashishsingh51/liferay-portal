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

package com.liferay.portal.servlet;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.DirectServletRegistry;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * @author Shuyang Zhou
 */
public class DirectServletRegistryImpl implements DirectServletRegistry {

	@Override
	public void clearServlets() {
		_servletInfos.clear();
	}

	@Override
	public Servlet getServlet(String path) {
		ServletInfo servletInfo = _servletInfos.get(path);

		if (servletInfo == null) {
			return null;
		}

		Servlet servlet = servletInfo.getServlet();

		if (PropsValues.DIRECT_SERVLET_CONTEXT_RELOAD) {
			long lastModified = getFileLastModified(path, servlet);

			if ((lastModified == 0) ||
				(lastModified != servletInfo.getLastModified())) {

				_servletInfos.remove(path);

				servlet = null;

				if (_log.isDebugEnabled()) {
					_log.debug("Reload " + path);
				}
			}
			else {
				servlet = reloadDependants(path, servlet, servletInfo);
			}
		}

		return servlet;
	}

	@Override
	public void putServlet(String path, Servlet servlet) {
		if (path.startsWith(PathModulePrefixHolder._PATH_MODULE_PREFIX) ||
			_servletInfos.containsKey(path)) {

			return;
		}

		long lastModified = 1;

		if (PropsValues.DIRECT_SERVLET_CONTEXT_RELOAD) {
			lastModified = getFileLastModified(path, servlet);
		}

		if (lastModified > 0) {
			ServletInfo servletInfo = new ServletInfo();

			servletInfo.setLastModified(lastModified);
			servletInfo.setServlet(servlet);

			_servletInfos.put(path, servletInfo);
		}
	}

	protected long getFileLastModified(String path, Servlet servlet) {
		File file = _getFile(path, servlet);

		if (file.exists()) {
			return file.lastModified();
		}

		return -1;
	}

	protected Servlet reloadDependants(
		String path, Servlet servlet, ServletInfo servletInfo) {

		if (!_reloadDependants) {
			return servlet;
		}

		try {
			Method method = ReflectionUtil.getDeclaredMethod(
				servlet.getClass(), "getDependants");

			Collection<String> dependants = null;

			if (JasperVersionDetector.hasJspServletDependantsMap()) {
				Map<String, ?> dependantsMap = (Map<String, ?>)method.invoke(
					servlet);

				if (dependantsMap != null) {
					dependants = dependantsMap.keySet();
				}
			}
			else {
				dependants = (List<String>)method.invoke(servlet);
			}

			if (dependants == null) {
				return servlet;
			}

			boolean reloadServlet = false;

			for (String dependant : dependants) {
				long lastModified = getFileLastModified(dependant, servlet);

				Long previousLastModified = _dependantTimestamps.get(dependant);

				if (previousLastModified == null) {
					_dependantTimestamps.put(dependant, lastModified);

					previousLastModified = lastModified;
				}

				if ((lastModified == 0) ||
					(lastModified != previousLastModified.longValue())) {

					reloadServlet = true;

					_dependantTimestamps.put(dependant, lastModified);

					if (_log.isDebugEnabled()) {
						_log.debug("Reload dependant " + dependant);
					}
				}
			}

			if (reloadServlet) {
				_servletInfos.remove(path);

				updateFileLastModified(path, servlet);

				servlet = null;
			}
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Reloading of dependant JSP is disabled because your " +
						"Servlet container is not a variant of Jasper",
					noSuchMethodException);
			}

			_reloadDependants = false;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return servlet;
	}

	protected void updateFileLastModified(String path, Servlet servlet) {
		File file = _getFile(path, servlet);

		file.setLastModified(System.currentTimeMillis());
	}

	private File _getFile(String path, Servlet servlet) {
		ServletConfig servletConfig = servlet.getServletConfig();

		ServletContext servletContext = servletConfig.getServletContext();

		String contextPath = servletContext.getContextPath();

		if (!Validator.isBlank(contextPath) && path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		return new File(servletContext.getRealPath(StringPool.BLANK), path);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DirectServletRegistryImpl.class);

	private final Map<String, Long> _dependantTimestamps =
		new ConcurrentHashMap<>();
	private boolean _reloadDependants = true;
	private final Map<String, ServletInfo> _servletInfos =
		new ConcurrentHashMap<>();

	private static class PathModulePrefixHolder {

		private static final String _PATH_MODULE_PREFIX = StringBundler.concat(
			PortalUtil.getPathProxy(), PortalUtil.getPathContext(),
			Portal.PATH_MODULE, StringPool.SLASH);

	}

	private static class ServletInfo {

		public long getLastModified() {
			return _lastModified;
		}

		public Servlet getServlet() {
			return _servlet;
		}

		public void setLastModified(long lastModified) {
			_lastModified = lastModified;
		}

		public void setServlet(Servlet servlet) {
			_servlet = servlet;
		}

		private long _lastModified;
		private Servlet _servlet;

	}

}