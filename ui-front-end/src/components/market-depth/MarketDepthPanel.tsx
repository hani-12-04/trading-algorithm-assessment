import React from 'react';
import './MarketDepthPanel.css';  // Import CSS specific to the MarketDepthPanel
import { PriceCell } from './PriceCell';  // Import PriceCell component to display prices
import { QuantityCell } from './QuantityCell';  // Import QuantityCell component to display quantities

// Define the structure of the data rows that will be passed as props
interface MarketDepthRow {
  symbolLevel: string;  // Identifier for each row
  level: number;        // Level in the market depth (e.g., 0, 1, 2, etc.)
  bid: number;          // Bid price
  bidQuantity: number;  // Quantity for bid price
  offer: number;        // Offer price (also called ask price)
  offerQuantity: number; // Quantity for offer price
}

interface MarketDepthPanelProps {
  data: MarketDepthRow[];
}

// Define the props structure for MarketDepthPanel
interface MarketDepthPanelProps {
  data: MarketDepthRow[];  // The data passed to the panel, an array of rows
}

export const MarketDepthPanel = (props: MarketDepthPanelProps) => {
  return (
    <table className="MarketDepthPanel">
      <thead>
        <tr>
          <th>Level</th>
          <th>Bid Quantity</th>
          <th>Bid Price</th>
          <th>Offer Price</th>
          <th>Offer Quantity</th>
        </tr>
      </thead>
      <tbody>
        {/* Map through the data array and create a row for each market depth entry */}
        {props.data.map((row) => (
          <tr key={row.symbolLevel}>
            <td>{row.level}</td>
            {/* Use QuantityCell for displaying bid quantity */}
            <QuantityCell quantity={row.bidQuantity} type="bid" />
            {/* Use PriceCell for displaying bid price */}
            <PriceCell price={row.bid} type="bid" />
            {/* Use PriceCell for displaying offer price */}
            <PriceCell price={row.offer} type="ask" />
            {/* Use QuantityCell for displaying offer quantity */}
            <QuantityCell quantity={row.offerQuantity} type="ask" />
          </tr>
        ))}
      </tbody>
    </table>
  );
};

