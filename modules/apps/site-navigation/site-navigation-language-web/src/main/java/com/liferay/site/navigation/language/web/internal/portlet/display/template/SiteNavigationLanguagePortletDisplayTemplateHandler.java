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

package com.liferay.site.navigation.language.web.internal.portlet.display.template;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portletdisplaytemplate.BasePortletDisplayTemplateHandler;
import com.liferay.portal.kernel.servlet.taglib.ui.LanguageEntry;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.site.navigation.language.constants.SiteNavigationLanguagePortletKeys;
import com.liferay.site.navigation.language.web.internal.configuration.SiteNavigationLanguageWebTemplateConfiguration;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	configurationPid = "com.liferay.site.navigation.language.web.internal.configuration.SiteNavigationLanguageWebTemplateConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL,
	property = "javax.portlet.name=" + SiteNavigationLanguagePortletKeys.SITE_NAVIGATION_LANGUAGE,
	service = TemplateHandler.class
)
public class SiteNavigationLanguagePortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return LanguageEntry.class.getName();
	}

	@Override
	public String getDefaultTemplateKey() {
		return _siteNavigationLanguageWebTemplateConfiguration.ddmTemplateKey();
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			SiteNavigationLanguagePortletKeys.SITE_NAVIGATION_LANGUAGE, locale);

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return SiteNavigationLanguagePortletKeys.SITE_NAVIGATION_LANGUAGE;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		TemplateVariableGroup templateVariableGroup =
			templateVariableGroups.get("fields");

		templateVariableGroup.empty();

		templateVariableGroup.addCollectionVariable(
			"languages", List.class, PortletDisplayTemplateConstants.ENTRIES,
			"language", LanguageEntry.class, "curLanguage", "longDisplayName");

		return templateVariableGroups;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_siteNavigationLanguageWebTemplateConfiguration =
			ConfigurableUtil.createConfigurable(
				SiteNavigationLanguageWebTemplateConfiguration.class,
				properties);
	}

	@Override
	protected String getTemplatesConfigPath() {
		return "com/liferay/site/navigation/language/web/portlet/display" +
			"/template/dependencies/portlet-display-templates.xml";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private volatile SiteNavigationLanguageWebTemplateConfiguration
		_siteNavigationLanguageWebTemplateConfiguration;

}