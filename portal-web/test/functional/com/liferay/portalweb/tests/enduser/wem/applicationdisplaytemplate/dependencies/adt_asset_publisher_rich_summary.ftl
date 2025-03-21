<#if !entries?has_content>
	<#if !themeDisplay.isSignedIn()>
		${renderRequest.setAttribute("PORTLET_CONFIGURATOR_VISIBILITY", true)}
	</#if>

	<div class="alert alert-info">
		<@liferay_ui["message"] key="there-are-no-results" />
	</div>
</#if>

<#list entries as entry>
	<#assign
		entry = entry

		assetRenderer = entry.getAssetRenderer()

		entryTitle = htmlUtil.escape(assetRenderer.getTitle(locale))

		viewURL = assetPublisherHelper.getAssetViewURL(renderRequest, renderResponse, entry)
	/>

	<#if !stringUtil.equals(assetLinkBehavior, "showFullContent")>
		<#assign viewURL = assetPublisherHelper.getAssetViewURL(renderRequest, renderResponse, entry, true) />
	</#if>

	<div class="asset-abstract">
		<div class="asset-actions c-mt-3">
			<@getPrintIcon />

			<@getFlagsIcon />

			<@getEditIcon />
		</div>

		<h3 class="asset-title">
			<a href="${viewURL}">
				${entryTitle}
			</a>
		</h3>

		<@getMetadataField fieldName="tags" />

		<@getMetadataField fieldName="create-date" />

		<@getMetadataField fieldName="view-count" />

		<div class="asset-content">
			<@getSocialBookmarks />

			<div class="asset-summary">
				<@getMetadataField fieldName="author" />

				${htmlUtil.escape(assetRenderer.getSummary(renderRequest, renderResponse))}

				<a href="${viewURL}"><@liferay.language key="read-more" /><span class="hide-accessible"><@liferay.language key="about" />${entryTitle}</span> &raquo;</a>
			</div>

			<@getRatings />

			<@getRelatedAssets />

			<@getDiscussion />
		</div>
	</div>
</#list>

<#macro getDiscussion>
	<#if getterUtil.getBoolean(enableComments) && assetRenderer.isCommentable()>
		<br />

		<@liferay_comment["discussion"]
			className=entry.getClassName()
			classPK=entry.getClassPK()
			formName="fm" + entry.getClassPK()
			ratingsEnabled=getterUtil.getBoolean(enableCommentRatings)
			redirect=currentURL
			userId=assetRenderer.getUserId()
		/>
	</#if>
</#macro>

<#macro getEditIcon>
	<#if assetRenderer.hasEditPermission(themeDisplay.getPermissionChecker())>
		<#assign editPortletURL = assetRenderer.getURLEdit(renderRequest, renderResponse, windowStateFactory.getWindowState("NORMAL"), themeDisplay.getURLCurrent())!"" />

		<#if validator.isNotNull(editPortletURL)>
			<#assign title = languageUtil.format(locale, "edit-x", entryTitle, false) />

			<@liferay_ui["icon"]
				icon="pencil"
				message=title
				url="javascript:Liferay.Util.openWindow({id:'" + renderResponse.getNamespace() + "editAsset', title: '" + title + "', uri:'" + htmlUtil.escapeURL(editPortletURL.toString()) + "'});"
			/>
		</#if>
	</#if>
</#macro>

<#macro getFlagsIcon>
	<#if getterUtil.getBoolean(enableFlags)>
		<@liferay_flags["flags"]
			className=entry.getClassName()
			classPK=entry.getClassPK()
			contentTitle=entry.getTitle(locale)
			label=false
			reportedUserId=entry.getUserId()
		/>
	</#if>
</#macro>

<#macro getMetadataField
	fieldName
>
	<#if stringUtil.split(metadataFields)?seq_contains(fieldName)>
		<span class="metadata-entry metadata-${fieldName}">
			<#assign dateFormat = "dd MMM yyyy - HH:mm:ss" />

			<#if stringUtil.equals(fieldName, "author")>
				<@liferay.language key="by" /> ${portalUtil.getUserName(assetRenderer.getUserId(), assetRenderer.getUserName())}
			<#elseif stringUtil.equals(fieldName, "categories")>
				<@liferay_asset["asset-categories-summary"]
					className=entry.getClassName()
					classPK=entry.getClassPK()
					portletURL=renderResponse.createRenderURL()
				/>
			<#elseif stringUtil.equals(fieldName, "create-date")>
				${dateUtil.getDate(entry.getCreateDate(), dateFormat, locale)}
			<#elseif stringUtil.equals(fieldName, "expiration-date")>
				${dateUtil.getDate(entry.getExpirationDate(), dateFormat, locale)}
			<#elseif stringUtil.equals(fieldName, "modified-date")>
				${dateUtil.getDate(entry.getModifiedDate(), dateFormat, locale)}
			<#elseif stringUtil.equals(fieldName, "priority")>
				${entry.getPriority()}
			<#elseif stringUtil.equals(fieldName, "publish-date")>
				${dateUtil.getDate(entry.getPublishDate(), dateFormat, locale)}
			<#elseif stringUtil.equals(fieldName, "tags")>
				<@liferay_asset["asset-tags-summary"]
					className=entry.getClassName()
					classPK=entry.getClassPK()
					portletURL=renderResponse.createRenderURL()
				/>
			<#elseif stringUtil.equals(fieldName, "view-count")>
				${entry.getViewCount()} <@liferay.language key="views" />
			</#if>
		</span>
	</#if>
</#macro>

<#macro getPrintIcon>
	<#if getterUtil.getBoolean(enablePrint)>
		<#assign printURL = renderResponse.createRenderURL() />

		${printURL.setParameter("mvcPath", "/view_content.jsp")}
		${printURL.setParameter("assetEntryId", entry.getEntryId()?string)}
		${printURL.setParameter("viewMode", "print")}
		${printURL.setParameter("type", entry.getAssetRendererFactory().getType())}
		${printURL.setWindowState("pop_up")}

		<@liferay_ui["icon"]
			icon="print"
			message="print"
			url="javascript:Liferay.Util.openWindow({id:'" + renderResponse.getNamespace() + "printAsset', title: '" + languageUtil.format(locale, "print-x-x", ["hide-accessible", entryTitle], false) + "', uri: '" + htmlUtil.escapeURL(printURL.toString()) + "'});"
		/>
	</#if>
</#macro>

<#macro getRatings>
	<#if getterUtil.getBoolean(enableRatings) && assetRenderer.isRatable()>
		<div class="asset-ratings">
			<@liferay_ratings["ratings"]
				className=entry.getClassName()
				classPK=entry.getClassPK()
			/>
		</div>
	</#if>
</#macro>

<#macro getRelatedAssets>
	<#if getterUtil.getBoolean(enableRelatedAssets)>
		<@liferay_asset["asset-links"]
			assetEntryId=entry.getEntryId()
			viewInContext=!stringUtil.equals(assetLinkBehavior, "showFullContent")
		/>
	</#if>
</#macro>

<#macro getSocialBookmarks>
	<@liferay_social_bookmarks["bookmarks"]
		className=entry.getClassName()
		classPK=entry.getClassPK()
		displayStyle="${socialBookmarksDisplayStyle}"
		target="_blank"
		title=entry.getTitle(locale)
		types="${socialBookmarksTypes}"
		url=viewURL
	/>
</#macro>