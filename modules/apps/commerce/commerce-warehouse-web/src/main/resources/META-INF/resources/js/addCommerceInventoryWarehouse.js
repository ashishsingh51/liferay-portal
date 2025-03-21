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

import ServiceProvider from 'commerce-frontend-js/ServiceProvider/index';
import {CLOSE_MODAL} from 'commerce-frontend-js/utilities/eventsDefinitions';
import {createPortletURL, openToast} from 'frontend-js-web';

export default function ({
	defaultLanguageId,
	editCommerceInventoryWarehousePortletURL,
	namespace,
}) {
	const CommerceInventoryWarehouseResource = ServiceProvider.AdminInventoryAPI(
		'v1'
	);

	const form = document.getElementById(`${namespace}fm`);

	form.addEventListener('submit', (event) => {
		event.preventDefault();

		const name = form.querySelector(`#${namespace}name`).value;

		if (!name) {
			openToast({
				message: Liferay.Language.get('please-enter-a-valid-name'),
				title: Liferay.Language.get('error'),
				type: 'danger',
			});

			return;
		}

		const commerceInventoryWarehouseData = {
			active: false,
			name: {[defaultLanguageId]: name},
		};

		return CommerceInventoryWarehouseResource.addWarehouse(
			commerceInventoryWarehouseData
		)
			.then((payload) => {
				const redirectURL = createPortletURL(
					editCommerceInventoryWarehousePortletURL
				);

				redirectURL.searchParams.append(
					`${namespace}commerceInventoryWarehouseId`,
					payload.id
				);
				redirectURL.searchParams.append('p_auth', Liferay.authToken);

				window.parent.Liferay.fire(CLOSE_MODAL, {
					redirectURL: redirectURL.toString(),
					successNotification: {
						message: Liferay.Language.get(
							'your-request-completed-successfully'
						),
						showSuccessNotification: true,
					},
				});
			})
			.catch((error) => {
				const errorsMap = {
					'please-enter-a-valid-name': Liferay.Language.get(
						'please-enter-a-valid-name'
					),
				};

				openToast({
					message:
						errorsMap[error.message] ||
						Liferay.Language.get('an-unexpected-error-occurred'),
					title: Liferay.Language.get('error'),
					type: 'danger',
				});
			});
	});
}
