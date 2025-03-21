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

import {array, date, number, object, string} from 'yup';

import {TypeActivityKey} from '../../../../../common/enums/TypeActivityKey';
import {TacticKeys} from '../../../../../common/enums/mdfRequestTactics';
import isObjectEmpty from '../../../../../common/utils/isObjectEmpty';
import getContentMarketingFieldsValidation from './fieldValidation/contentMarketingFields';
import getDigitalMarketingFieldsValidation from './fieldValidation/digitalMarketingFields';
import getEventFieldsValidation from './fieldValidation/eventFields';
import leadListFieldsValidation from './fieldValidation/leadListvalidation';
import getMiscellaneousMarketingFieldsValidation from './fieldValidation/miscellaneousMarketingFields';

const activitiesSchema = object({
	activities: array()
		.of(
			object({
				activityDescription: object().when(
					['typeActivity', 'tactic'],
					(typeActivity, tactic) => {
						let targetFields = {};

						switch (typeActivity.key) {
							case TypeActivityKey.EVENT:
								targetFields = getEventFieldsValidation(
									tactic.key as TacticKeys
								);
								break;
							case TypeActivityKey.DIGITAL_MARKETING:
								targetFields = getDigitalMarketingFieldsValidation(
									tactic.key as TacticKeys
								);
								break;
							case TypeActivityKey.CONTENT_MARKETING:
								targetFields = getContentMarketingFieldsValidation();
								break;
							default:
								targetFields = getMiscellaneousMarketingFieldsValidation(
									tactic.key as TacticKeys
								);
								break;
						}

						targetFields = {
							...targetFields,
							...leadListFieldsValidation,
						};

						return object(targetFields);
					}
				),
				budgets: array()
					.of(
						object({
							cost: number()
								.max(
									999999999,
									'The value cannot be greater than 9,999,999.99'
								)
								.moreThan(0, 'Required')
								.required('Required'),

							expense: object({
								key: string(),
								name: string(),
							}).test(
								'is-empty',
								'Required',
								(value) => !isObjectEmpty(value)
							),
						})
					)
					.min(1, 'Required'),
				endDate: date()
					.test(
						'end-date-six-month',
						'End date must be less than six month after start date',
						(endDate, testContext) => {
							if (endDate) {
								const startDate = testContext.parent.startDate;

								const futureDate = new Date(startDate);
								futureDate.setMonth(startDate.getMonth() + 6);

								if (
									endDate.getMonth() < futureDate.getMonth()
								) {
									return true;
								}

								return (
									endDate.getMonth() ===
										futureDate.getMonth() &&
									endDate.getDay() < futureDate.getDay()
								);
							}

							return false;
						}
					)
					.test(
						'end-date-less-end-date',
						'The end date cannot be before start date',
						(endDate, testContext) => {
							if (endDate && testContext.parent.startDate) {
								return testContext.parent.startDate < endDate;
							}

							return false;
						}
					)
					.test(
						'end-date-different-year',
						'The end date cannot be the year different the year of start date',
						(endDate, testContext) => {
							if (endDate && testContext.parent.startDate) {
								return (
									testContext.parent.startDate.getFullYear() ===
									endDate.getFullYear()
								);
							}

							return false;
						}
					)
					.test(
						'end-date-year-current-year',
						'The end date cannot exceed the current year',
						(endDate) => {
							const currentYear = new Date().getFullYear();

							if (endDate && currentYear) {
								return endDate.getFullYear() === currentYear;
							}

							return false;
						}
					)
					.required('Required'),
				mdfRequestAmount: number()
					.moreThan(0, 'Required')
					.required('Required')
					.test(
						'is-greater-than-the-percentage',
						'It is not possible to give a bigger discount than the Claim Percent',
						(mdfRequestAmount, testContext) => {
							if (mdfRequestAmount) {
								return !(
									+mdfRequestAmount >
									+testContext.parent.totalCostOfExpense * 0.5
								);
							}

							return false;
						}
					),
				name: string()
					.trim()
					.max(40, 'You have exceeded the character limit')
					.required('Required'),
				startDate: date()
					.test(
						'is-today',
						'Start date need to be after today',
						(startDate) => {
							if (startDate) {
								const currentDate = new Date();
								currentDate.setHours(0, 0, 0, 0);

								return currentDate < startDate;
							}

							return false;
						}
					)
					.test(
						'end-date-year-current-year',
						'The start date cannot exceed the current year',
						(startDate) => {
							const currentYear = new Date().getFullYear();

							if (startDate && currentYear) {
								return startDate.getFullYear() === currentYear;
							}

							return false;
						}
					)
					.required('Required'),
				tactic: object({
					id: number(),
					name: string(),
				}).test(
					'is-empty',
					'Required',
					(value) => !isObjectEmpty(value)
				),
				typeActivity: object({
					id: number(),
					name: string(),
					value: string(),
				}).test(
					'is-empty',
					'Required',
					(value) => !isObjectEmpty(value)
				),
			})
		)
		.min(1),
});

export default activitiesSchema;
