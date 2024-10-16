import React from "react";

interface QuantityCellProps {
  quantity: number;
  type: 'bid' | 'ask';  // 'bid' or 'ask' to apply styles
}

export const QuantityCell = (props: QuantityCellProps) => {
  const { quantity, type } = props;

  return (
    <td className={`${type} quantity`}>
      {quantity}
    </td>
  );
};




