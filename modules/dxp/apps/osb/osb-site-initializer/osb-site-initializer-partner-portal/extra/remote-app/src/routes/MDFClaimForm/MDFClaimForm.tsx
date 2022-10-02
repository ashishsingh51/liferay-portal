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

import ClayLoadingIndicator from '@clayui/loading-indicator';

import PRMFormik from '../../common/components/PRMFormik';
import MDFRequestActivityDTO from '../../common/interfaces/dto/mdfRequestActivityDTO';
import MDFClaim from '../../common/interfaces/mdfClaim';
import useGetMDFRequestById from '../../common/services/liferay/object/mdf-requests/useGetMDFRequestById';
import MDFClaimPage from './components/MDFClaimPage';

const getInitialFormValues = (
	totalrequestedAmount?: number,
	activitiesDTO?: MDFRequestActivityDTO[]
): MDFClaim => {
	return {
		activities: activitiesDTO?.map((activity) => ({
			budgets: activity.activityToBudgets?.map((budget) => ({
				claimAmount: budget.cost,
				expenseName: budget.expense.name,
				invoices: [],
			})),
			documents: [],
			finished: false,
			id: activity.id,
			metrics: '',
			name: activity.name,
			totalCost: activity.totalCostOfExpense,
		})),
		totalClaimAmount: totalrequestedAmount,
	};
};

const MDFClaimForm = () => {
	const {data: mdfRequest, isValidating} = useGetMDFRequestById(46006);

	if (!mdfRequest || isValidating) {
		return <ClayLoadingIndicator />;
	}

	return (
		<PRMFormik
			initialValues={getInitialFormValues(
				mdfRequest.totalMDFRequestAmount,
				mdfRequest.mdfRequestToActivities
			)}
			onSubmit={(values, formikHelpers) =>
				// eslint-disable-next-line no-console
				console.log(values, formikHelpers)
			}
		>
			<MDFClaimPage
				mdfRequest={mdfRequest}
				onCancel={() => console.log('canceled')}
				onSaveAsDraft={(values, formik) => console.log(values, formik)}
			/>
		</PRMFormik>
	);
};

export default MDFClaimForm;
