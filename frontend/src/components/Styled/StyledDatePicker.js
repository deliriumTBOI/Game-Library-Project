import styled from 'styled-components';
import { DatePicker } from 'antd';

const StyledDatePicker = styled(DatePicker)`
  background-color: #2a475e !important;
  border-color: #2a475e !important;
  color: #c7d5e0 !important;
  width: 100% !important;

  .ant-picker-input > input {
    color: #c7d5e0 !important;
  }

  .ant-picker-suffix {
    color: #c7d5e0 !important;
  }

  .ant-picker-clear {
    color: #c7d5e0 !important;
  }
`;

export default StyledDatePicker;
