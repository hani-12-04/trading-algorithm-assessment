import React from "react";

// Define the structure of the QuantityCell props
interface QuantityCellProps {
  quantity: number;  // The quantity value to display
  type: 'bid' | 'ask';  // Type of quantity, either 'bid' or 'ask', to apply specific styles
}

export const QuantityCell = (props: QuantityCellProps) => {
  const { quantity, type } = props;  // Destructure the props to get quantity and type

  return (
    // Use the type (bid or ask) to assign the appropriate CSS class for styling
    <td className={`${type} quantity`}>
      {quantity}  {/* Display the quantity value */}
    </td>
  );
};




