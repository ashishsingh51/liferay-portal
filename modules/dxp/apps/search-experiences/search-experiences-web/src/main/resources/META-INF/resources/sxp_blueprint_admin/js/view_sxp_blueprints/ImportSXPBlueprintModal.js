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

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import {fetch, navigate} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

const VALID_EXTENSIONS = '.json';

const ImportSXPBlueprintModal = ({portletNamespace, redirectURL}) => {
	const [errorMessage, setErrorMessage] = useState();
	const [loadingResponse, setLoadingResponse] = useState(false);
	const [importFile, setImportFile] = useState();
	const [visible, setVisible] = useState(false);

	// Define componentId upon mount to prevent browser console error
	// about `Component with id is being registered twice`.

	const componentId = `${portletNamespace}importModal`;

	const {observer, onClose} = useModal({
		onClose: () => {
			setVisible(false);
		},
	});

	const _handleClose = (redirect) => {
		setErrorMessage('');
		setImportFile(null);

		onClose(false);

		if (redirect) {
			navigate(redirect);
		}
	};

	const _handleFormError = (error) => {
		setErrorMessage(
			error ||
				Liferay.Language.get(
					'an-unexpected-error-occurred-while-importing-your-file'
				)
		);

		setLoadingResponse(false);
	};

	const _handleInputChange = (event) => {
		setImportFile(event.target.files[0]);
	};

	const _handleSubmit = async () => {
		setLoadingResponse(true);

		const importText = await new Response(importFile).text();

		try {
			const isElement = !!JSON.parse(importText).elementDefinition;

			const fetchURL = isElement
				? '/o/search-experiences-rest/v1.0/sxp-elements'
				: '/o/search-experiences-rest/v1.0/sxp-blueprints';

			fetch(fetchURL, {
				body: importText,
				headers: new Headers({
					'Content-Type': 'application/json',
				}),
				method: 'POST',
			})
				.then((response) => {
					return response.json().then((data) => ({
						ok: response.ok,
						responseContent: data,
					}));
				})
				.then(({ok, responseContent}) => {
					if (!ok) {
						_handleFormError(
							isElement
								? Liferay.Language.get(
										'unable-to-import-because-the-element-configuration-is-invalid'
								  )
								: Liferay.Language.get(
										'unable-to-import-because-the-blueprint-configuration-is-invalid'
								  )
						);

						if (process.env.NODE_ENV === 'development') {
							console.error(responseContent.title);
						}
					}

					setLoadingResponse(false);

					if (ok) {
						_handleClose(redirectURL);
					}
				})
				.catch(() => {
					_handleFormError();
				});
		}
		catch {
			_handleFormError();
		}
	};

	useEffect(() => {
		Liferay.component(
			componentId,
			{
				open: () => {
					setVisible(true);
				},
			},
			{
				destroyOnNavigate: true,
			}
		);

		return () => Liferay.destroyComponent(componentId);
	}, [componentId, setVisible]);

	return visible ? (
		<ClayModal
			className="sxp-import-modal-root"
			observer={observer}
			size="full-screen"
		>
			<ClayModal.Header>
				{Liferay.Language.get('import')}
			</ClayModal.Header>

			<ClayModal.Body>
				{errorMessage && (
					<ClayAlert
						displayType="danger"
						onClose={() => setErrorMessage('')}
						title={Liferay.Language.get('error')}
					>
						{errorMessage}
					</ClayAlert>
				)}

				<p className="text-secondary">
					{Liferay.Language.get(
						'select-a-blueprint-or-element-json-file-to-import'
					)}
				</p>

				<div className="form-group">
					<label className="control-label" htmlFor="file">
						{Liferay.Language.get('select-file')}

						<span className="reference-mark">
							<ClayIcon symbol="asterisk" />
						</span>
					</label>

					<ClayInput
						accept={VALID_EXTENSIONS}
						name="file"
						onChange={_handleInputChange}
						required
						type="file"
					/>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={loadingResponse}
							displayType="secondary"
							onClick={_handleClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!importFile || loadingResponse}
							displayType="primary"
							onClick={_handleSubmit}
						>
							{loadingResponse && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									/>
								</span>
							)}

							{Liferay.Language.get('import')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	) : null;
};

export default ImportSXPBlueprintModal;
