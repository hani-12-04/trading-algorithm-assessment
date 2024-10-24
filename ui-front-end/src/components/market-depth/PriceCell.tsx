import React, { useRef, useEffect } from "react";

interface PriceCellProps {
  price: number;
  type: 'bid' | 'ask';  // We can keep this prop to differentiate, but won't apply color
}

export const PriceCell = ({ price, type }: PriceCellProps) => {
  // Create a reference to store the last price
  const lastPriceRef = useRef(price);

  // Calculate the price difference by subtracting the last price from the current price
  const priceDifference = price - lastPriceRef.current;

  // useEffect to update the reference with the current price after each render
  useEffect(() => {
    lastPriceRef.current = price;  // Store the new price in the ref for the next update
  }, [price]);  // Only run the effect when the price changes

  return (
    <td className="price">
      {/* Display the price value */}
      {price}
      {/* If the price has changed, show an up or down arrow */}
      {priceDifference !== 0 && (
        <span className={`arrow ${priceDifference > 0 ? 'up' : 'down'}`}>
          {/* Show the appropriate arrow based on whether the price increased or decreased */}
          {priceDifference > 0 ? '↑' : '↓'}
        </span>
      )}
    </td>
  );
};