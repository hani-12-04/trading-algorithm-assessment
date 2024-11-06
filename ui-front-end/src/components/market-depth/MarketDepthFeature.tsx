<<<<<<< HEAD
import { useMarketDepthData } from "./useMarketDepthData";
import { schemas } from "../../data/algo-schemas";
import { MarketDepthPanel } from "./MarketDepthPanel";
import { PriceCell } from "./PriceCell";
import { Quantity } from "./Quantity";

export const MarketDepthFeature = () => {
  const data = useMarketDepthData(schemas.prices);
  if (!data) {
      return <div>Loading...</div>; // Display loading message until data is available
    }
     return (
         <div className="market-depth-panel">
            <MarketDepthPanel data={data} />
         </div>
     );
  }
=======
import React from 'react';
import { useMarketDepthData } from './useMarketDepthData';  // Import the hook
import { schemas } from '../../data/algo-schemas';  // Import the schema
import { MarketDepthPanel } from './MarketDepthPanel';
import './MarketDepthFeature.css';

// // Define the test data statically
// const testData = [
//   { symbolLevel: "1230", level: 0, bid: 1000, bidQuantity: 500, offer: 1010, offerQuantity: 700 },
//   { symbolLevel: "1231", level: 1, bid: 990, bidQuantity: 700, offer: 1012, offerQuantity: 400 },
//   { symbolLevel: "1232", level: 2, bid: 985, bidQuantity: 1200, offer: 1013, offerQuantity: 800 },
//   { symbolLevel: "1233", level: 3, bid: 984, bidQuantity: 1300, offer: 1018, offerQuantity: 750 },
//   { symbolLevel: "1234", level: 4, bid: 970, bidQuantity: 800, offer: 1021, offerQuantity: 900 },
//   { symbolLevel: "1235", level: 5, bid: 969, bidQuantity: 700, offer: 1026, offerQuantity: 1500 },
//   { symbolLevel: "1236", level: 6, bid: 950, bidQuantity: 750, offer: 1027, offerQuantity: 1500 },
//   { symbolLevel: "1237", level: 7, bid: 945, bidQuantity: 900, offer: 1029, offerQuantity: 2000 },
//   { symbolLevel: "1238", level: 8, bid: 943, bidQuantity: 500, offer: 1031, offerQuantity: 500 },
//   { symbolLevel: "1239", level: 9, bid: 940, bidQuantity: 200, offer: 1024, offerQuantity: 800 },
// ];
//
// export const MarketDepthFeature = () => {
//   // Pass the static test data to MarketDepthPanel for rendering
//   return (
//     <div className="MarketDepthFeature">
//       <MarketDepthPanel data={testData} />
//     </div>
//   );
// };

export interface MarketDepthRow {
    symbolLevel: string;  // Identifier for each row
    level: number;        // Level in the market depth (e.g., 0, 1, 2, etc.)
    bid: number;          // Bid price
    bidQuantity: number;  // Quantity for bid price
    offer: number;        // Offer price (also called ask price)
    offerQuantity: number; // Quantity for offer price
    }


 export const MarketDepthFeature = () => {
   // Fetch market depth data using the custom hook
   const { marketDepthData } = useMarketDepthData(schemas.prices);

   // Render the MarketDepthPanel directly without a loading message
   return (
     <div className="MarketDepthFeature">
       <MarketDepthPanel data={marketDepthData} />
     </div>
   );
 };
>>>>>>> c6e52acc9535d309ac42ea74ea215d0cd3d3e15d

