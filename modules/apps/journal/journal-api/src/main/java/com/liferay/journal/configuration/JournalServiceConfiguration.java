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

package com.liferay.journal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Pavel Savinov
 */
@ExtendedObjectClassDefinition(
	category = "web-content",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.journal.configuration.JournalServiceConfiguration",
	localization = "content/Language",
	name = "journal-service-configuration-name"
)
@ProviderType
public interface JournalServiceConfiguration {

	@Meta.AD(deflt = "true", name = "add-default-structures", required = false)
	public boolean addDefaultStructures();

	@Meta.AD(
		deflt = "&|\\'|@|\\\\|]|}|:|=|>|/|<|[|{|%|+|#|`|?|\\\"|;|*|~",
		description = "specifcy-characters-that-are-not-allowed-in-journal-folder-names",
		name = "characters-blacklist", required = false
	)
	public String[] charactersblacklist();

	@Meta.AD(
		deflt = "${resource:com/liferay/journal/dependencies/error.ftl}",
		name = "error-template-ftl", required = false
	)
	public String errorTemplateFTL();

	@Meta.AD(deflt = "15", name = "check-interval", required = false)
	public int checkInterval();

	@Meta.AD(
		deflt = "", description = "journal-article-custom-token-names",
		name = "custom-token-names", required = false
	)
	public String[] customTokenNames();

	@Meta.AD(
		deflt = "", description = "journal-article-custom-token-values",
		name = "custom-token-values", required = false
	)
	public String[] customTokenValues();

	@Meta.AD(
		deflt = "true", description = "journal-article-comments",
		name = "article-comments-enabled", required = false
	)
	public boolean articleCommentsEnabled();

	@Meta.AD(
		deflt = "true",
		description = "journal-article-database-search-content-keywords",
		name = "database-content-keyword-search-enabled", required = false
	)
	public boolean databaseContentKeywordSearchEnabled();

	@Meta.AD(
		deflt = "true", description = "journal-article-expire-all-versions",
		name = "expire-all-article-versions-enabled", required = false
	)
	public boolean expireAllArticleVersionsEnabled();

	@Meta.AD(
		deflt = "true", description = "journal-article-index-all-versions",
		name = "index-all-article-versions-enabled", required = false
	)
	public boolean indexAllArticleVersionsEnabled();

	@Meta.AD(
		deflt = "true", description = "publish-to-live-by-default-help",
		name = "publish-to-live-by-default", required = false
	)
	public boolean publishToLiveByDefaultEnabled();

	@Meta.AD(
		deflt = "true", description = "version-history-by-default-enabled-help",
		name = "version-history-by-default-enabled", required = false
	)
	public boolean versionHistoryByDefaultEnabled();

	@Meta.AD(
		deflt = "false", description = "sync-content-search-on-startup-help",
		name = "sync-content-search-on-startup", required = false
	)
	public boolean syncContentSearchOnStartup();

	@Meta.AD(
		deflt = "true",
		description = "journal-article-export-import-processor-cache-enabled-help",
		name = "journal-article-export-import-processor-cache-enabled",
		required = false
	)
	public boolean journalArticleExportImportProcessorCacheEnabled();

	@Meta.AD(
		deflt = "@page_break@",
		description = "journal-article-token-page-break",
		name = "journal-article-page-break-token", required = false
	)
	public String journalArticlePageBreakToken();

	@Meta.AD(
		deflt = "json", description = "journal-article-storage-type-help",
		name = "journal-article-storage-type", required = false
	)
	public String journalArticleStorageType();

	@Meta.AD(
		deflt = "0", name = "journal-article-max-version-count",
		required = false
	)
	public int journalArticleMaxVersionCount();

	@Meta.AD(
		deflt = "false",
		description = "single-asset-publish-process-includes-version-history-help",
		name = "single-asset-publish-process-includes-version-history",
		required = false
	)
	public boolean singleAssetPublishIncludeVersionHistory();

	@Meta.AD(
		deflt = "false", name = "enable-content-transformer-listener",
		required = false
	)
	public boolean enableContentTransformerListener();

	@Meta.AD(
		deflt = "0", name = "terms-of-use-journal-article-group-id",
		required = false
	)
	public long termsOfUseJournalArticleGroupId();

	@Meta.AD(
		deflt = "", name = "terms-of-use-journal-article-id", required = false
	)
	public String termsOfUseJournalArticleId();

}