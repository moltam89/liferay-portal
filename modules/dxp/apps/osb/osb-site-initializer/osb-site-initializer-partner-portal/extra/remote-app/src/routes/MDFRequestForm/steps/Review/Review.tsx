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

import Button from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useFormikContext} from 'formik';

import PRMFormikPageProps from '../../../../common/components/PRMFormik/interfaces/prmFormikPageProps';
import ResumeCard from '../../../../common/components/ResumeCard';
import MDFRequest from '../../../../common/interfaces/mdfRequest';
import MDFRequestActivity from '../../../../common/interfaces/mdfRequestActivity';
import {Status} from '../../../../common/utils/constants/status';
import getIntlNumberFormat from '../../../../common/utils/getIntlNumberFormat';
import ActivityPanel from '../../components/ActivityPanel';
import {StepType} from '../../enums/stepType';
import MDFRequestStepProps from '../../interfaces/mdfRequestStepProps';
import Body from './components/Body';
import ActivityReviewEntry from './components/Body/components/ActivityReviewEntry';
import GoalsEntries from './components/Body/components/GoalsEntries';
import Header from './components/Header';

const Review = ({
	onCancel,
	onPrevious,
	onSaveAsDraft,
}: PRMFormikPageProps & MDFRequestStepProps) => {
	const {isSubmitting, values, ...formikHelpers} = useFormikContext<
		MDFRequest
	>();

	return (
		<div className="d-flex flex-column">
			<Header />

			<Body name="Goals" title="Campaign Information">
				<GoalsEntries mdfRequest={values} />
			</Body>

			<Body name="Activities" title="Insurance Industry Lead Gen">
				<div className="border mb-3"></div>

				{values?.activities.map(
					(activity: MDFRequestActivity, index: number) => (
						<ActivityPanel
							activity={activity}
							detail
							key={index}
							overallCampaignName={values.overallCampaignName}
						>
							<ActivityReviewEntry
								mdfRequestActivity={activity}
							/>
						</ActivityPanel>
					)
				)}
			</Body>

			<Body>
				<div>
					<div className="my-3">
						<ResumeCard
							leftContent="Total Budget"
							rightContent={getIntlNumberFormat(
								values.currency
							).format(values.totalCostOfExpense)}
						/>

						<ResumeCard
							className="mt-3"
							leftContent="Claim Percent"
							rightContent={`${0.5 * 100}%`}
						/>

						<ResumeCard
							className="mt-3"
							leftContent="Total MDF Requested Amount"
							rightContent={getIntlNumberFormat(
								values.currency
							).format(values.totalMDFRequestAmount)}
						/>
					</div>

					<div className="border mb-1"></div>

					<div className="border-neutral-2 d-md-flex p-2">
						<div className="d-flex justify-content-between mr-auto">
							<Button
								className="mr-4"
								displayType={null}
								onClick={() =>
									onPrevious?.(StepType.ACTIVITIES)
								}
							>
								Previous
							</Button>

							<Button
								className="inline-item inline-item-after pl-0"
								disabled={isSubmitting}
								displayType={null}
								onClick={() =>
									onSaveAsDraft?.(values, formikHelpers)
								}
							>
								Save as Draft
								{isSubmitting &&
									values.mdfRequestStatus ===
										Status.DRAFT && (
										<ClayLoadingIndicator className="inline-item inline-item-after ml-2" />
									)}
							</Button>
						</div>

						<div className="d-flex justify-content-between px-2 px-md-0">
							<Button
								className="mr-4"
								displayType="secondary"
								onClick={onCancel}
							>
								Cancel
							</Button>

							<Button
								className="inline-item inline-item-after"
								disabled={isSubmitting}
								type="submit"
							>
								Submit
								{isSubmitting &&
									values.mdfRequestStatus ===
										Status.PENDING && (
										<ClayLoadingIndicator className="inline-item inline-item-after ml-2" />
									)}
							</Button>
						</div>
					</div>
				</div>
			</Body>
		</div>
	);
};

export default Review;
