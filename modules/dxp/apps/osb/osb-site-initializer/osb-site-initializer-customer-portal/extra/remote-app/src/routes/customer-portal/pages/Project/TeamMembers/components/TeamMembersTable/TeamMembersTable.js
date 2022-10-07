/* eslint-disable no-console */
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import ClayIcon from '@clayui/icon';
import {useState} from 'react';
import StatusTag from '../../../../../../../common/components/StatusTag';
import Table from '../../../../../../../common/components/Table';
import {useAppPropertiesContext} from '../../../../../../../common/contexts/AppPropertiesContext';
import {STATUS_TAG_TYPES} from '../../../../../utils/constants/statusTag';
import NameColumn from './components/columns/NameColumn';
import OptionsColumn from './components/columns/OptionsColumn';
import RolesColumn from './components/columns/RolesColumn/RolesColumn';
import useAccountRolesByAccountExternalReferenceCode from './hooks/useAccountRolesByAccountExternalReferenceCode';
import useMyUserAccountByAccountExternalReferenceCode from './hooks/useMyUserAccountByAccountExternalReferenceCode';
import useUserAccountsByAccountExternalReferenceCode from './hooks/useUserAccountsByAccountExternalReferenceCode';
import getAccountBriefByExternalReferenceCode from './utils/getAccountBriefByExternalReferenceCode';
import {getColumns} from './utils/getColumns';
import hasAccountSupportSeatRole from './utils/hasAccountSupportSeatRole';

const TeamMembersTable = ({
	koroneikiAccount,
	loading: koroneikiAccountLoading,
}) => {
	const {articleAccountSupportURL, gravatarAPI} = useAppPropertiesContext();

	const [currentIndexEditing, setCurrentIndexEditing] = useState();

	const [
		hasAccountAdministratorRole,
		{loading: myUserAccountLoading},
	] = useMyUserAccountByAccountExternalReferenceCode(
		koroneikiAccount?.accountKey,
		koroneikiAccountLoading
	);

	const [
		accountAdministratorsCount,
		{data: userAccountsData, loading: userAccountsLoading},
	] = useUserAccountsByAccountExternalReferenceCode(
		koroneikiAccount?.accountKey,
		koroneikiAccountLoading
	);

	const {
		data: accountRolesData,
		loading: accountRolesLoading,
	} = useAccountRolesByAccountExternalReferenceCode(
		koroneikiAccount,
		koroneikiAccountLoading
	);

	const userAccounts =
		userAccountsData?.accountUserAccountsByExternalReferenceCode.items;

	const availableAccountRoles =
		accountRolesData?.accountAccountRolesByExternalReferenceCode.items;

	const loading =
		myUserAccountLoading || userAccountsLoading || accountRolesLoading;

	console.log(accountAdministratorsCount);

	return (
		<div className="cp-team-members-table-wrapper overflow-auto">
			<Table
				className="border-0 cp-team-members-table"
				columns={getColumns(
					hasAccountAdministratorRole,
					articleAccountSupportURL
				)}
				isLoading={loading}
				rows={userAccounts?.map((userAccount, index) => ({
					email: (
						<p className="m-0 text-truncate">
							{userAccount.emailAddress}
						</p>
					),
					name: (
						<NameColumn
							gravatarAPI={gravatarAPI}
							userAccount={userAccount}
						/>
					),
					options: (
						<OptionsColumn
							edit={index === currentIndexEditing}
							onCancel={() => setCurrentIndexEditing()}
							onEdit={() => setCurrentIndexEditing(index)}
							onRemove={() => console.log('Remove it')}
							onSave={() => console.log('Save it')}
						/>
					),
					role: (
						<RolesColumn
							accountRoles={availableAccountRoles}
							currentRoleBriefs={
								getAccountBriefByExternalReferenceCode(
									userAccount.accountBriefs,
									koroneikiAccount?.accountKey
								).roleBriefs
							}
							edit={index === currentIndexEditing}
						/>
					),
					status: (
						<StatusTag
							currentStatus={
								userAccount?.lastLoginDate
									? STATUS_TAG_TYPES.active
									: STATUS_TAG_TYPES.invited
							}
						/>
					),
					supportSeat: hasAccountSupportSeatRole(
						userAccount?.accountBriefs,
						koroneikiAccount?.accountKey
					) && (
						<ClayIcon
							className="cp-team-members-support-seat-icon"
							symbol="check-circle-full"
						/>
					),
				}))}
			/>
		</div>
	);
};

export default TeamMembersTable;
