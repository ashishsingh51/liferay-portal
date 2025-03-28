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

package com.liferay.oauth.service.impl;

import com.liferay.oauth.exception.OAuthApplicationCallbackURIException;
import com.liferay.oauth.exception.OAuthApplicationNameException;
import com.liferay.oauth.exception.OAuthApplicationWebsiteURLException;
import com.liferay.oauth.model.OAuthApplication;
import com.liferay.oauth.model.OAuthUser;
import com.liferay.oauth.service.base.OAuthApplicationLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUID;

import java.io.InputStream;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
@Component(
	property = "model.class.name=com.liferay.oauth.model.OAuthApplication",
	service = AopService.class
)
public class OAuthApplicationLocalServiceImpl
	extends OAuthApplicationLocalServiceBaseImpl {

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addOAuthApplication(long, String, String, String, int
	 *             boolean, String, String, ServiceContext)}
	 */
	@Deprecated
	@Override
	public OAuthApplication addOAuthApplication(
			long userId, String name, String description, int accessLevel,
			boolean shareableAccessToken, String callbackURI, String websiteURL,
			ServiceContext serviceContext)
		throws PortalException {

		String consumerKey = serviceContext.getUuid();

		if (Validator.isNull(consumerKey)) {
			consumerKey = _portalUUID.generate();
		}

		return oAuthApplicationLocalService.addOAuthApplication(
			userId, name, description,
			DigesterUtil.digestHex(
				Digester.MD5, consumerKey, PwdGenerator.getPassword()),
			accessLevel, shareableAccessToken, callbackURI, websiteURL,
			serviceContext);
	}

	@Override
	public OAuthApplication addOAuthApplication(
			long userId, String name, String description, String token,
			int accessLevel, boolean shareableAccessToken, String callbackURI,
			String websiteURL, ServiceContext serviceContext)
		throws PortalException {

		// OAuth application

		User user = userLocalService.getUser(userId);
		Date date = new Date();

		validate(name, callbackURI, websiteURL);

		long oAuthApplicationId = counterLocalService.increment();

		OAuthApplication oAuthApplication = oAuthApplicationPersistence.create(
			oAuthApplicationId);

		oAuthApplication.setCompanyId(user.getCompanyId());
		oAuthApplication.setUserId(user.getUserId());
		oAuthApplication.setUserName(user.getFullName());
		oAuthApplication.setCreateDate(serviceContext.getCreateDate(date));
		oAuthApplication.setModifiedDate(serviceContext.getModifiedDate(date));
		oAuthApplication.setName(name);
		oAuthApplication.setDescription(description);

		String consumerKey = serviceContext.getUuid();

		if (Validator.isNull(consumerKey)) {
			consumerKey = _portalUUID.generate();
		}

		oAuthApplication.setConsumerKey(consumerKey);
		oAuthApplication.setConsumerSecret(token);
		oAuthApplication.setAccessLevel(accessLevel);
		oAuthApplication.setShareableAccessToken(shareableAccessToken);
		oAuthApplication.setCallbackURI(callbackURI);
		oAuthApplication.setWebsiteURL(websiteURL);

		oAuthApplication = oAuthApplicationPersistence.update(oAuthApplication);

		// Resources

		resourceLocalService.addModelResources(
			oAuthApplication, serviceContext);

		return oAuthApplication;
	}

	@Override
	public void deleteLogo(long oAuthApplicationId) throws PortalException {
		OAuthApplication oAuthApplication =
			oAuthApplicationPersistence.findByPrimaryKey(oAuthApplicationId);

		long logoId = oAuthApplication.getLogoId();

		if (logoId > 0) {
			oAuthApplication.setLogoId(0);

			oAuthApplicationPersistence.update(oAuthApplication);

			_imageLocalService.deleteImage(logoId);
		}
	}

	@Override
	public OAuthApplication deleteOAuthApplication(long oAuthApplicationId)
		throws PortalException {

		OAuthApplication oAuthApplication =
			oAuthApplicationPersistence.findByPrimaryKey(oAuthApplicationId);

		return deleteOAuthApplication(oAuthApplication);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public OAuthApplication deleteOAuthApplication(
			OAuthApplication oAuthApplication)
		throws PortalException {

		// OAuth application

		oAuthApplicationPersistence.remove(oAuthApplication);

		// OAuth users

		List<OAuthUser> oAuthUsers =
			oAuthUserPersistence.findByOAuthApplicationId(
				oAuthApplication.getOAuthApplicationId());

		for (OAuthUser oAuthUser : oAuthUsers) {
			oAuthUserPersistence.remove(oAuthUser);
		}

		// Resources

		resourceLocalService.deleteResource(
			oAuthApplication, ResourceConstants.SCOPE_INDIVIDUAL);

		// Image

		_imageLocalService.deleteImage(oAuthApplication.getLogoId());

		return oAuthApplication;
	}

	@Override
	public OAuthApplication fetchOAuthApplication(String consumerKey) {
		return oAuthApplicationPersistence.fetchByConsumerKey(consumerKey);
	}

	@Override
	public OAuthApplication getOAuthApplication(String consumerKey)
		throws PortalException {

		return oAuthApplicationPersistence.findByConsumerKey(consumerKey);
	}

	@Override
	public List<OAuthApplication> getOAuthApplications(
		long companyId, int start, int end,
		OrderByComparator<OAuthApplication> orderByComparator) {

		return oAuthApplicationPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getOAuthApplicationsCount(long companyId) {
		return oAuthApplicationPersistence.countByCompanyId(companyId);
	}

	@Override
	public List<OAuthApplication> search(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end,
		OrderByComparator<OAuthApplication> orderByComparator) {

		keywords = _customSQL.keywords(keywords)[0];

		if ((params != null) && params.containsKey("userId")) {
			long userId = (Long)params.get("userId");

			if (Validator.isNotNull(keywords)) {
				return oAuthApplicationPersistence.findByU_LikeN(
					userId, keywords, start, end, orderByComparator);
			}

			return oAuthApplicationPersistence.findByUserId(
				userId, start, end, orderByComparator);
		}

		if (Validator.isNotNull(keywords)) {
			return oAuthApplicationPersistence.findByC_LikeN(
				companyId, keywords, start, end, orderByComparator);
		}

		return oAuthApplicationPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int searchCount(
		long companyId, String keywords, LinkedHashMap<String, Object> params) {

		keywords = _customSQL.keywords(keywords)[0];

		if ((params != null) && params.containsKey("userId")) {
			long userId = (Long)params.get("userId");

			if (Validator.isNotNull(keywords)) {
				return oAuthApplicationPersistence.countByU_LikeN(
					userId, keywords);
			}

			return oAuthApplicationPersistence.countByUserId(userId);
		}

		if (Validator.isNotNull(keywords)) {
			return oAuthApplicationPersistence.countByC_LikeN(
				companyId, keywords);
		}

		return oAuthApplicationPersistence.countByCompanyId(companyId);
	}

	@Override
	public OAuthApplication updateLogo(
			long oAuthApplicationId, InputStream inputStream)
		throws PortalException {

		OAuthApplication oAuthApplication =
			oAuthApplicationPersistence.findByPrimaryKey(oAuthApplicationId);

		long logoId = oAuthApplication.getLogoId();

		if (logoId <= 0) {
			logoId = counterLocalService.increment();

			oAuthApplication.setLogoId(logoId);

			oAuthApplication = oAuthApplicationPersistence.update(
				oAuthApplication);
		}

		_imageLocalService.updateImage(
			oAuthApplication.getCompanyId(), logoId, inputStream);

		return oAuthApplication;
	}

	@Override
	public OAuthApplication updateOAuthApplication(
			long oAuthApplicationId, String name, String description,
			boolean shareableAccessToken, String callbackURI, String websiteURL,
			ServiceContext serviceContext)
		throws PortalException {

		validate(name, callbackURI, websiteURL);

		OAuthApplication oAuthApplication =
			oAuthApplicationPersistence.findByPrimaryKey(oAuthApplicationId);

		oAuthApplication.setModifiedDate(serviceContext.getModifiedDate(null));
		oAuthApplication.setName(name);
		oAuthApplication.setDescription(description);
		oAuthApplication.setShareableAccessToken(shareableAccessToken);
		oAuthApplication.setCallbackURI(callbackURI);
		oAuthApplication.setWebsiteURL(websiteURL);

		return oAuthApplicationPersistence.update(oAuthApplication);
	}

	protected void validate(String name, String callbackURI, String websiteURL)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new OAuthApplicationNameException();
		}

		if (!Validator.isUri(callbackURI)) {
			throw new OAuthApplicationCallbackURIException();
		}

		if (!Validator.isUrl(websiteURL)) {
			throw new OAuthApplicationWebsiteURLException();
		}
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private PortalUUID _portalUUID;

}