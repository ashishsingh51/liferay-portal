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

package com.liferay.gradle.plugins.node.task;

import com.liferay.gradle.plugins.node.internal.util.FileUtil;
import com.liferay.gradle.plugins.node.internal.util.GradleUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

/**
 * @author Andrea Di Giorgi
 */
@CacheableTask
public class ExecuteNodeScriptTask extends ExecuteNodeTask {

	@Override
	public void executeNode() throws Exception {
		List<Object> args = getArgs();

		try {
			setArgs(getCompleteArgs());

			super.executeNode();
		}
		finally {
			setArgs(args);
		}
	}

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getScriptFile() {
		File file = GradleUtil.toFile(getProject(), _scriptFile);

		if (file == null) {
			return null;
		}

		return file;
	}

	public void setScriptFile(Object scriptFile) {
		_scriptFile = scriptFile;
	}

	@Internal
	protected List<String> getCompleteArgs() {
		File scriptFile = getScriptFile();

		List<String> args = GradleUtil.toStringList(getArgs());

		if (scriptFile == null) {
			return args;
		}

		List<String> completeArgs = new ArrayList<>();

		completeArgs.add(FileUtil.getAbsolutePath(scriptFile));

		completeArgs.addAll(args);

		return completeArgs;
	}

	private Object _scriptFile;

}