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

package com.liferay.gradle.plugins.defaults;

import com.liferay.gradle.plugins.LiferayAntPlugin;
import com.liferay.gradle.plugins.defaults.internal.LiferayRelengPlugin;
import com.liferay.gradle.plugins.defaults.internal.util.GradlePluginsDefaultsUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;
import com.liferay.gradle.plugins.defaults.task.CopyIvyDependenciesTask;
import com.liferay.gradle.plugins.defaults.task.ReplaceRegexTask;

import groovy.lang.Closure;

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.Upload;
import org.gradle.api.tasks.ant.AntTarget;

/**
 * @author Andrea Di Giorgi
 */
public class LiferayAntDefaultsPlugin implements Plugin<Project> {

	public static final String COPY_IVY_DEPENDENCIES_TASK_NAME =
		"copyIvyDependencies";

	@Override
	public void apply(Project project) {
		File ivyXmlFile = project.file("ivy.xml");

		if (ivyXmlFile.exists()) {
			File portalRootDir = GradleUtil.getRootDir(
				project.getRootProject(), "portal-impl");

			GradlePluginsDefaultsUtil.configureRepositories(
				project, portalRootDir);

			CopyIvyDependenciesTask copyIvyDependenciesTask =
				_addTaskCopyIvyDependencies(project, ivyXmlFile);

			copyIvyDependenciesTask.writeChecksumFile();
		}

		GradleUtil.applyPlugin(project, LiferayAntPlugin.class);

		_applyPlugins(project);

		// GRADLE-2427

		_addTaskInstall(project);

		_applyConfigScripts(project);

		final ReplaceRegexTask updateVersionTask = _addTaskUpdateVersion(
			project);

		_configureProject(project);

		GradleUtil.excludeTasksWithProperty(
			project, LiferayOSGiDefaultsPlugin.SNAPSHOT_IF_STALE_PROPERTY_NAME,
			true, MavenPlugin.INSTALL_TASK_NAME,
			BasePlugin.UPLOAD_ARCHIVES_TASK_NAME);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					GradlePluginsDefaultsUtil.setProjectSnapshotVersion(
						project);

					// setProjectSnapshotVersion must be called before
					// configureTaskUploadArchives, because the latter one needs
					// to know if we are publishing a snapshot or not.

					_configureTaskUploadArchives(project, updateVersionTask);
				}

			});
	}

	private CopyIvyDependenciesTask _addTaskCopyIvyDependencies(
		Project project, File inputFile) {

		final CopyIvyDependenciesTask copyIvyDependenciesTask =
			GradleUtil.addTask(
				project, COPY_IVY_DEPENDENCIES_TASK_NAME,
				CopyIvyDependenciesTask.class);

		copyIvyDependenciesTask.setDescription(
			"Copies the dependencies declared in the ivy.xml file.");

		File destinationDir = project.file("docroot");

		if (destinationDir.exists()) {
			destinationDir = new File(destinationDir, "WEB-INF/lib");
		}
		else {
			destinationDir = project.file("lib");
		}

		copyIvyDependenciesTask.setDestinationDir(destinationDir);

		copyIvyDependenciesTask.setInputFile(inputFile);

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			AntTarget.class,
			new Action<AntTarget>() {

				@Override
				public void execute(AntTarget antTarget) {
					antTarget.dependsOn(copyIvyDependenciesTask);
				}

			});

		return copyIvyDependenciesTask;
	}

	private Upload _addTaskInstall(Project project) {
		Upload upload = GradleUtil.addTask(
			project, MavenPlugin.INSTALL_TASK_NAME, Upload.class);

		Configuration configuration = GradleUtil.getConfiguration(
			project, Dependency.ARCHIVES_CONFIGURATION);

		upload.setConfiguration(configuration);
		upload.setDescription(
			"Installs the '" + configuration.getName() +
				"' artifacts into the local Maven repository.");

		return upload;
	}

	@SuppressWarnings("serial")
	private ReplaceRegexTask _addTaskUpdateVersion(Project project) {
		ReplaceRegexTask replaceRegexTask = GradleUtil.addTask(
			project, LiferayRelengPlugin.UPDATE_VERSION_TASK_NAME,
			ReplaceRegexTask.class);

		replaceRegexTask.match(
			"module-incremental-version=(\\d+)",
			"docroot/WEB-INF/liferay-plugin-package.properties");

		replaceRegexTask.setDescription(
			"Updates \"module-incremental-version\" in the " +
				"liferay-plugin-package.properties file.");

		replaceRegexTask.setReplacement(
			new Closure<String>(project) {

				@SuppressWarnings("unused")
				public String doCall(String group) {
					int moduleIncrementalVersion = Integer.parseInt(group);

					return String.valueOf(moduleIncrementalVersion + 1);
				}

			});

		return replaceRegexTask;
	}

	private void _applyConfigScripts(Project project) {
		GradleUtil.applyScript(
			project,
			"com/liferay/gradle/plugins/defaults/dependencies" +
				"/config-maven.gradle",
			project);
	}

	private void _applyPlugins(Project project) {
		GradleUtil.applyPlugin(project, MavenPlugin.class);
	}

	private void _configureProject(Project project) {
		project.setGroup(GradleUtil.getProjectGroup(project, _GROUP));
	}

	private void _configureTaskUploadArchives(
		Project project, Task updatePluginVersionTask) {

		if (GradlePluginsDefaultsUtil.isSnapshot(project)) {
			return;
		}

		Task uploadArchivesTask = GradleUtil.getTask(
			project, BasePlugin.UPLOAD_ARCHIVES_TASK_NAME);

		uploadArchivesTask.finalizedBy(updatePluginVersionTask);
	}

	private static final String _GROUP = "com.liferay.plugins";

}