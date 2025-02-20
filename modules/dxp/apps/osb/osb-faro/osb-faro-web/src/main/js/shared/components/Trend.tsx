import getCN from 'classnames';
import Icon from 'shared/components/Icon';
import React from 'react';

interface ITrendProps extends React.HTMLAttributes<HTMLDivElement> {
	color: string;
	icon?: string;
	label: string;
}

const Trend: React.FC<ITrendProps> = ({className, color, icon, label}) => (
	<div className={getCN('analytics-trend', className)} style={{color}}>
		{icon && <Icon symbol={icon} />}
		<span className='analytics-trend-percent mb-0'>{label}</span>
	</div>
);

export default Trend;
