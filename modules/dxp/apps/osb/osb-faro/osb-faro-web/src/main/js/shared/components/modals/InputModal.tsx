import Button from 'shared/components/Button';
import Input from 'shared/components/Input';
import Modal from 'shared/components/modal';
import React, {useRef} from 'react';
import {noop} from 'lodash';

interface IInputModalProps {
	className: string;
	onClose: () => void;
	onSubmit: (value: string | number) => void;
	placeholder: string;
	title: string;
}

const InputModal: React.FC<IInputModalProps> = ({
	className,
	onClose,
	onSubmit = noop,
	placeholder,
	title
}) => {
	const _inputRef = useRef<any>();

	return (
		<Modal className={className} size='sm'>
			<Modal.Header onClose={onClose} title={title} />

			<Modal.Body>
				<Input placeholder={placeholder} ref={_inputRef} />
			</Modal.Body>

			<Modal.Footer>
				<Button onClick={onClose}>
					{Liferay.Language.get('cancel')}
				</Button>

				<Button
					display='primary'
					onClick={() =>
						onSubmit(
							_inputRef?.current?._elementRef?.current?.value
						)
					}
				>
					{Liferay.Language.get('submit')}
				</Button>
			</Modal.Footer>
		</Modal>
	);
};

export default InputModal;
