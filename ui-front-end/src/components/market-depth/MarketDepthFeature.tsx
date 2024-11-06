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

