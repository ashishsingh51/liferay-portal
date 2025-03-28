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

package com.liferay.friendly.url.internal.xstream.configurator;

import com.liferay.exportimport.kernel.xstream.XStreamAlias;
import com.liferay.exportimport.kernel.xstream.XStreamConverter;
import com.liferay.exportimport.kernel.xstream.XStreamType;
import com.liferay.friendly.url.model.impl.FriendlyURLEntryImpl;
import com.liferay.xstream.configurator.XStreamConfigurator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(service = XStreamConfigurator.class)
public class FriendlyURLEntryXStreamConfigurator
	implements XStreamConfigurator {

	@Override
	public List<XStreamType> getAllowedXStreamTypes() {
		return _xStreamTypes;
	}

	@Override
	public List<XStreamAlias> getXStreamAliases() {
		return _xStreamAliases;
	}

	@Override
	public List<XStreamConverter> getXStreamConverters() {
		return Collections.emptyList();
	}

	private final List<XStreamAlias> _xStreamAliases = Arrays.asList(
		new XStreamAlias(FriendlyURLEntryImpl.class, "FriendlyURLEntry"));
	private final List<XStreamType> _xStreamTypes = Arrays.asList(
		new XStreamType(FriendlyURLEntryImpl.class));

}