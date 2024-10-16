import React from "react";
import { PriceCell } from './PriceCell';
import { QuantityCell } from './QuantityCell';
import "./MarketDepthPanel.css";

interface MarketDepthRow {
  symbolLevel: string;
  level: number;
  bid: number;
  bidQuantity: number;
  offer: number;
  offerQuantity: number;
}

interface MarketDepthPanelProps {
  data: MarketDepthRow[];
}

export const MarketDepthPanel = ({ data }: MarketDepthPanelProps) => {
  return (
    <table className="MarketDepthPanel">
      <thead>
        <tr>
          <th>Level</th>
          <th colSpan={2}>Bid</th>
          <th colSpan={2}>Ask</th>
        </tr>
        <tr>
          <th></th>
          <th>Quantity</th>
          <th>Price</th>
          <th>Price</th>
          <th>Quantity</th>
        </tr>
      </thead>
      <tbody>
        {data.map((row, index) => (
          <tr key={row.symbolLevel}>
            <td>{row.level}</td>
            <QuantityCell quantity={row.bidQuantity} type="bid" />
            <PriceCell price={row.bid} type="bid" />
            <PriceCell price={row.offer} type="ask" />
            <QuantityCell quantity={row.offerQuantity} type="ask" />
          </tr>
        ))}
      </tbody>
    </table>
  );
};
