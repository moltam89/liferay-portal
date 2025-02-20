import Button from 'shared/components/Button';
import EventChip from './EventChip';
import EventDropdown from './EventDropdown';
import React from 'react';
import {Align} from '@clayui/drop-down';
import {
	DeleteAllAttributes,
	withAttributesConsumer
} from '../context/attributes';
import {Event} from 'event-analysis/utils/types';

interface IEventSectionProps {
	deleteAllAttributes: DeleteAllAttributes;
	event: Event;
	onEventChange: (event: Event) => void;
}

const EventSection: React.FC<IEventSectionProps> = ({
	deleteAllAttributes,
	event,
	onEventChange
}) => {
	const handleEventChange = (event: Event): void => {
		onEventChange(event);

		deleteAllAttributes();
	};

	return (
		<div className='event-section-root d-flex align-items-center'>
			<div className='section-header'>
				{Liferay.Language.get('analyze')}
			</div>

			<div className='event-container d-flex justify-content-between'>
				<div className='event-list'>
					{event && (
						<EventChip
							event={event}
							onEventChange={handleEventChange}
						/>
					)}
				</div>

				{!event && (
					<EventDropdown
						alignmentPosition={Align.LeftTop}
						onEventChange={handleEventChange}
						trigger={
							<Button
								className='add-event-button'
								display='primary'
								icon='plus'
								iconAlignment='left'
								size='sm'
							/>
						}
					/>
				)}
			</div>
		</div>
	);
};

export default withAttributesConsumer(EventSection);
