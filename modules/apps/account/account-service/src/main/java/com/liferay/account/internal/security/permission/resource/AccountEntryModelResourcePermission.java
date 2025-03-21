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

package com.liferay.account.internal.security.permission.resource;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.permission.OrganizationPermission;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "model.class.name=com.liferay.account.model.AccountEntry",
	service = ModelResourcePermission.class
)
public class AccountEntryModelResourcePermission
	implements ModelResourcePermission<AccountEntry> {

	@Override
	public void check(
			PermissionChecker permissionChecker, AccountEntry accountEntry,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, accountEntry, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountEntry.class.getName(),
				accountEntry.getAccountEntryId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long accountEntryId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, accountEntryId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AccountEntry.class.getName(), accountEntryId,
				actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, AccountEntry accountEntry,
			String actionId)
		throws PortalException {

		return contains(
			permissionChecker, accountEntry.getAccountEntryId(), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long accountEntryId,
			String actionId)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			accountEntryId);

		if ((accountEntry != null) &&
			permissionChecker.hasOwnerPermission(
				permissionChecker.getCompanyId(), AccountEntry.class.getName(),
				accountEntryId, accountEntry.getUserId(), actionId)) {

			return true;
		}

		List<AccountEntryOrganizationRel> accountEntryOrganizationRels =
			_accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRels(accountEntryId);

		long[] userOrganizationIds =
			_organizationLocalService.getUserOrganizationIds(
				permissionChecker.getUserId(), true);

		for (AccountEntryOrganizationRel accountEntryOrganizationRel :
				accountEntryOrganizationRels) {

			Organization organization =
				_organizationLocalService.fetchOrganization(
					accountEntryOrganizationRel.getOrganizationId());

			Organization originalOrganization = organization;

			while (organization != null) {
				boolean organizationMember = ArrayUtil.contains(
					userOrganizationIds, organization.getOrganizationId());

				if (!Objects.equals(
						actionId, AccountActionKeys.MANAGE_ORGANIZATIONS) &&
					organizationMember &&
					_organizationPermission.contains(
						permissionChecker, organization.getOrganizationId(),
						AccountActionKeys.MANAGE_AVAILABLE_ACCOUNTS)) {

					return true;
				}

				if (Objects.equals(organization, originalOrganization) &&
					permissionChecker.hasPermission(
						organization.getGroupId(), AccountEntry.class.getName(),
						accountEntryId, actionId)) {

					return true;
				}

				if (!Objects.equals(organization, originalOrganization) &&
					_organizationPermission.contains(
						permissionChecker, organization,
						AccountActionKeys.MANAGE_SUBORGANIZATIONS_ACCOUNTS) &&
					((organizationMember &&
					  Objects.equals(actionId, ActionKeys.VIEW)) ||
					 permissionChecker.hasPermission(
						 organization.getGroupId(),
						 AccountEntry.class.getName(), accountEntryId,
						 actionId))) {

					return true;
				}

				organization = organization.getParentOrganization();
			}
		}

		long accountEntryGroupId = 0;

		if (accountEntry != null) {
			accountEntryGroupId = accountEntry.getAccountEntryGroupId();
		}

		return permissionChecker.hasPermission(
			accountEntryGroupId, AccountEntry.class.getName(), accountEntryId,
			actionId);
	}

	@Override
	public String getModelName() {
		return AccountEntry.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _portletResourcePermission;
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private OrganizationPermission _organizationPermission;

	@Reference(
		target = "(resource.name=" + AccountConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}