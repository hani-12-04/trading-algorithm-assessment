import React from "react";
import "./MarketDepthPanel.css";
import { Quantity } from "./Quantity";
import { PriceCell } from "./PriceCell";

export const MarketDepthPanel = ({ data }) => {
  return (
    <div className="market-depth-container">
      <table className="market-depth-table">
        <thead>
          <tr>
            <th></th>
            <th colSpan={2}>Bid</th>
            <th colSpan={2}>Ask</th>
          </tr>
          <tr>
            <th>Level</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Price</th>
            <th>Quantity</th>
          </tr>
        </thead>
        <tbody>
          {data.map((row, index) => (
            <tr key={index}>
              <td className="level-cell">{row.level}</td>
              <td>
                <Quantity quantity={row.bidQuantity} type="bid" />
              </td>
              <td>
                <PriceCell price={row.bid} />
              </td>
              <td>
                <PriceCell price={row.offer} />
              </td>
              <td>
                <Quantity quantity={row.offerQuantity} type="ask" />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};






