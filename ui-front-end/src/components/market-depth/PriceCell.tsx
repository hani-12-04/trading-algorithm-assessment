import React, { useRef, useEffect } from "react";

interface PriceCellProps {
  price: number;
  type: 'bid' | 'ask';  // We can keep this prop to differentiate, but won't apply color
}

export const PriceCell = ({ price, type }: PriceCellProps) => {
  const lastPriceRef = useRef(price);
  const priceDifference = price - lastPriceRef.current;

  useEffect(() => {
    lastPriceRef.current = price;
  }, [price]);

  return (
    <td className="price"> {/* We will remove the `type` class from here */}
      {price}
      {priceDifference !== 0 && (
        <span className={`arrow ${priceDifference > 0 ? 'up' : 'down'}`}>
          {priceDifference > 0 ? '↑' : '↓'}
        </span>
      )}
    </td>
  );
};
