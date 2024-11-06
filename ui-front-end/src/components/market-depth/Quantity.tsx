import React, { useEffect, useRef } from "react";
import "./Quantity.css";

interface QuantityProps {
  quantity: number;
  type: "bid" | "ask"; // Determines color and direction
}

export const Quantity = ({ quantity, type }: QuantityProps) => {
  const lastQuantityRef = useRef(quantity);
  const quantityDiff = quantity - lastQuantityRef.current;

  useEffect(() => {
    lastQuantityRef.current = quantity; // Update the last quantity after each change
  }, [quantity]);

  // Calculate dynamic scaling factor based on quantity
  const scaleFactor = Math.min(quantity / 5000, 1); // Adjust divisor as needed for appropriate scaling

  return (
    <div
      className={`quantity-bar ${type === "bid" ? "bid-quantity" : "ask-quantity"}`}
      style={{ transform: `scaleX(${scaleFactor})` }}
    >
      <span className="quantity-text">{quantity}</span> {/* Static quantity text */}
    </div>
  );
};










