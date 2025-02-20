import accountLogo from '../../assets/icons/mainAppLogo.svg';
import {AccountHeaderButton} from './AccountHeaderButton';

import './AccountDetailsPage.scss';

import {useEffect, useState} from 'react';

import creditCartIcon from '../../assets/icons/credit-card.svg';
import downloadIcon from '../../assets/icons/download-icon.svg';
import locationIcon from '../../assets/icons/location-on-icon.svg';
import phoneIcon from '../../assets/icons/phone-icon.svg';
import userIcon from '../../assets/icons/user-icon.svg';
import {DetailedCard} from '../../components/DetailedCard/DetailedCard';
import {getAccountPostalAddressesByAccountId} from '../../utils/api';
import {DashboardListItems} from '../DashBoardPage/DashboardPage';

interface AccountDetailsPageProps {
	commerceAccount?: CommerceAccount;
	dashboardNavigationItems: DashboardListItems[];
	selectedAccount: Account;
	setDashboardNavigationItems: (values: DashboardListItems[]) => void;
	totalApps: number;
	totalMembers: number;
}

export function AccountDetailsPage({
	commerceAccount,
	dashboardNavigationItems,
	selectedAccount,
	setDashboardNavigationItems,
	totalApps,
	totalMembers,
}: AccountDetailsPageProps) {
	const [selectedAccountAddress, setSelectedAccountAddress] =
		useState<AccountPostalAddresses[]>();

	const getCustomFieldValue = (customFieldName: string) => {
		const customField = selectedAccount.customFields?.find(
			(customField) => customField.name === customFieldName
		);

		if (customField) {
			const {
				customValue: {data},
			} = customField;

			return data;
		}

		return '';
	};

	const maskDigits = (str: string) => {
		const first3Digits = str.slice(0, 3);
		const lastDigits = str.slice(3);
		const maskedDigits = lastDigits.replaceAll(/\S/g, '*');

		return first3Digits + maskedDigits;
	};

	const updateDashboardNavigationItems = (itemName: string) => {
		const newDashboardNavigationItems = dashboardNavigationItems.map(
			(navigationItem) => {
				if (navigationItem.itemName === itemName) {
					return {
						...navigationItem,
						itemSelected: true,
					};
				}

				return {
					...navigationItem,
					itemSelected: false,
				};
			}
		);

		setDashboardNavigationItems(newDashboardNavigationItems);
	};

	useEffect(() => {
		const makeFetch = async () => {
			const {items} = await getAccountPostalAddressesByAccountId(
				selectedAccount.id
			);

			setSelectedAccountAddress(items);
		};

		makeFetch();
	}, [selectedAccount]);

	return (
		<>
			<div className="account-details-container">
				<div className="account-details-header-container">
					<div className="account-details-header-left-content-container">
						<img
							alt="Account Image"
							className="account-details-header-left-content-image"
							src={commerceAccount?.logoURL ?? accountLogo}
						/>

						<div className="account-details-header-left-content-text-container">
							<span className="account-details-header-left-content-title">
								{selectedAccount.name}
							</span>

							<span className="account-details-header-left-content-description">
								{selectedAccount.type} account
							</span>
						</div>
					</div>

					<div className="account-details-header-right-container">
						<AccountHeaderButton
							boldText={`${totalApps}`}
							name="apps"
							onClick={(itemName) =>
								updateDashboardNavigationItems(itemName)
							}
							text="Apps"
							title="Apps"
						/>

						<AccountHeaderButton
							boldText={`${totalMembers}`}
							name="members"
							onClick={(itemName) =>
								updateDashboardNavigationItems(itemName)
							}
							text="Items"
							title="Members"
						/>

						<AccountHeaderButton
							boldText="0"
							name="solutions"
							text="Items"
							title="Solutions"
						/>
					</div>
				</div>

				<div className="account-details-body-container">
					<DetailedCard
						cardIcon={userIcon}
						cardIconAltText="Profile Icon"
						cardTitle="Profile"
					>
						<table className="account-details-body-table">
							<tr className="account-details-body-table-row">
								<th>Entity Type</th>

								<td className="account-details-body-table-description">
									{selectedAccount.type}
								</td>
							</tr>

							<tr className="account-details-body-table-row">
								<th>Publisher Name</th>

								<td className="account-details-body-table-description">
									{selectedAccount.name}
								</td>
							</tr>

							<tr className="account-details-body-table-row">
								<th>Publisher ID</th>

								<td className="account-details-body-table-description">
									{selectedAccount.id}
								</td>
							</tr>

							<tr className="account-details-body-table-row">
								<th>Github Username</th>

								<td className="account-details-body-table-description">
									{getCustomFieldValue('Github Username')}
								</td>
							</tr>

							<tr className="account-details-body-table-row">
								<th>Description</th>

								<td className="account-details-body-table-description">
									{selectedAccount.description}
								</td>
							</tr>
						</table>
					</DetailedCard>

					<DetailedCard
						cardIcon={phoneIcon}
						cardIconAltText="Contact Icon"
						cardTitle="Contact"
					>
						<table className="account-details-body-table">
							<tr className="account-details-body-table-row">
								<th>Phone</th>

								<td className="account-details-body-table-description">
									{getCustomFieldValue('Contact Phone')}
								</td>
							</tr>

							<tr className="account-details-body-table-row">
								<th>Email</th>

								<td className="account-details-body-table-description">
									{getCustomFieldValue('Contact Email')}
								</td>
							</tr>

							<tr className="account-details-body-table-row">
								<th>Website</th>

								<td className="account-details-body-table-description">
									<a
										href={getCustomFieldValue(
											'Homepage URL'
										)}
										target="__blank"
									>
										{getCustomFieldValue('Homepage URL')}
									</a>
								</td>
							</tr>
						</table>
					</DetailedCard>

					<DetailedCard
						cardIcon={locationIcon}
						cardIconAltText="Address Icon"
						cardTitle="Address"
					>
						<table className="account-details-body-table">
							{selectedAccountAddress?.map((address) => (
								<tr className="account-details-body-table-row">
									<th>Business Address</th>

									<td className="account-details-body-table-description">
										{address.streetAddressLine1}

										{', '}

										{address.addressLocality}

										{', '}

										{address.addressRegion}{' '}

										{address.postalCode}

										{', '}

										{address.addressCountry}
									</td>
								</tr>
							))}
						</table>
					</DetailedCard>

					<DetailedCard
						cardIconAltText="Agreements Icon"
						cardTitle="Agreements"
						clayIcon="info-book"
					>
						<table className="account-details-body-table">
							<tr>
								<th>
									Liferay Publisher Program License agreement
								</th>

								<td className="account-details-body-table-description">
									<img src={downloadIcon} />
								</td>
							</tr>

							<tr>
								<th>Liferay Publisher agreement</th>

								<td className="account-details-body-table-description">
									<img src={downloadIcon} />
								</td>
							</tr>
						</table>
					</DetailedCard>

					<DetailedCard
						cardIcon={creditCartIcon}
						cardIconAltText="Payment  Icon"
						cardTitle="Payment "
					>
						{getCustomFieldValue('Paypal Email Address') ? (
							<table className="account-details-body-table">
								<tr className="account-details-body-table-row">
									<th>Paypal Account</th>

									<td className="account-details-body-table-description">
										{maskDigits(
											getCustomFieldValue(
												'Paypal Email Address'
											)
										)}
									</td>
								</tr>

								<tr className="account-details-body-table-row">
									<th>Tax ID</th>

									<td className="account-details-body-table-description">
										{maskDigits(
											commerceAccount?.taxId ?? ''
										)}
									</td>
								</tr>
							</table>
						) : (
							<div className="account-details-body-empty-payment">
								Edit your publisher account to provide payment
								information for sales in the Marketplace
							</div>
						)}
					</DetailedCard>
				</div>
			</div>
		</>
	);
}
