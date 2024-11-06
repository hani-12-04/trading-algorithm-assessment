package codingblackfemales.gettingstarted;


import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MyAlgoLogic implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    private static final int maxOrders = 5; // The maximum number of active orders allowed
    private static final int quantity = 100; // The number of units per order

    private static final double VWAP_BUY_THRESHOLD = 0.995; // Buy if ask is slightly below VWAP
    private static final double VWAP_SELL_THRESHOLD = 0.95; // Sell if bid is close to VWAP

    private static final double BUY_LIMIT_PRICE = 115.0; // Buy limit price
    private static final double SELL_LIMIT_PRICE = 91.0; // Sell limit price

    private boolean firstTrade = true;  // Indicates if we’re placing the first trade
    private boolean vwapInitialised = false; // Flag to track if initial VWAP has been set

    private boolean clearActiveOrders = false;
    private static boolean shouldBuy = true;

    private int totalBuyOrders = 0; // Tracks total buy orders placed for fallback trigger

    private static final double DEFAULT_VWAP = 100.0; // Default VWAP if no market data is available

//    private double vwap = 0.0; // Stores calculated VWAP

    private double vwap = DEFAULT_VWAP;



    @Override
    public Action evaluate(SimpleAlgoState state) {
        try {
            if (state == null) {
                logger.error("[MYALGO] Algo state is null!");
                return NoAction.NoAction;
            }

            // log the current state of the order book
            var orderBookAsString = Util.orderBookToString(state);
            logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

            // Update or initialise VWAP based on the order book
            if (!vwapInitialised) {
                initialiseVWAP(state);
                vwapInitialised = true;
            } else {
                updateVWAP(state); // Update VWAP after each action
            }


//            // Calculate VWAP and handle the lack of market data
//            vwap = calculateVWAP(state, DEFAULT_VWAP);
//            if (vwap == DEFAULT_VWAP) {
//                logger.info("[MYALGO] Insufficient market data; using default VWAP: {}", DEFAULT_VWAP);
//                // Skip trading logic if we are only using default VWAP due to lack of data
//                return NoAction.NoAction;
//            }
//
//            logger.info("[MYALGO] VWAP after calculation: {}", vwap);

            // Retrieve the total Order count
            var totalOrderCount = state.getChildOrders().size();
            logger.info("[MYALGO] Total child orders: " + totalOrderCount);

            // Get active orders count
            List<ChildOrder> activeOrders = state.getActiveChildOrders();
            int activeOrdersCount = activeOrders.size();
            logger.info("[MYALGO] Active child orders: " + activeOrdersCount);

            // Get best bid and ask prices, handle potential null values
            BidLevel bestBid = state.getBidAt(0);
            AskLevel bestAsk = state.getAskAt(0);
            if (bestBid == null || bestAsk == null) {
                logger.warn("[MYALGO] Best bid or ask price is null!");
                return NoAction.NoAction;
            }

            long bidPrice = bestBid.price; // the highest bid price
            long askPrice = bestAsk.price; // the lowest ask price

            logger.info("[MYALGO] Best Bid: " + bidPrice + ", Best Ask: " + askPrice);
            logger.info("[MYALGO] Buy Limit Price: " + BUY_LIMIT_PRICE  + ", Sell Limit Price: " + SELL_LIMIT_PRICE);

            double buyThresholdPrice = vwap * VWAP_BUY_THRESHOLD;
            double sellThresholdPrice = vwap * VWAP_SELL_THRESHOLD;


            if (firstTrade) {
                if(askPrice <= buyThresholdPrice) {
                    logger.info("[MYALGO] Ask price is below midpoint. Placing BUY order.");
                    firstTrade = false;  // Switch off first trade flag after the initial buy
                    totalBuyOrders++;
                    return new CreateChildOrder(Side.BUY, quantity, askPrice);
                } if (bidPrice > sellThresholdPrice) {
                    logger.info("[MYALGO] Bid price is below midpoint. Placing SELL order.");
                    firstTrade = false;  // Switch off first trade flag after the initial sell
                    return new CreateChildOrder(Side.SELL, quantity, bidPrice);
                }
            } else {
                buyThresholdPrice = vwap * VWAP_BUY_THRESHOLD;
                sellThresholdPrice = vwap * VWAP_SELL_THRESHOLD;
            }

            logger.info("[MYALGO] VWAP: " + vwap + ", Buy Threshold: " + buyThresholdPrice + ", Sell Threshold: " + sellThresholdPrice);


            // --- Buy Logic ---
            if (shouldBuy && !clearActiveOrders && activeOrdersCount < maxOrders && askPrice <= BUY_LIMIT_PRICE && askPrice < buyThresholdPrice) {
                logger.info("[MYALGO] Ask price is below VWAP buy threshold and buy limit price. Placing BUY order.");
//                totalBuyOrders++;
//                 || totalBuyOrders >= 13
                if (activeOrdersCount + 1 >= maxOrders) {
                    clearActiveOrders = true; // Begin canceling orders
                    logger.info("[MYALGO] Reached buy order limit or fallback buy limit of 10. Starting cancellation.");
                }
                return new CreateChildOrder(Side.BUY, quantity, askPrice);
            }

            // ---- Sell Logic ---- //
            // && bidPrice >= SELL_LIMIT_PRICE
            if (!shouldBuy && !clearActiveOrders && activeOrdersCount < maxOrders && bidPrice >= SELL_LIMIT_PRICE) {
                logger.info("[MYALGO] Bid price is greater than or equal to the sell limit. Placing sell order.");
                if (activeOrdersCount >= maxOrders) {
                    logger.info("[MYALGO] Max orders reached.End Algo.");
                    return NoAction.NoAction;
                }
                return new CreateChildOrder(Side.SELL, quantity, bidPrice);  // Sell at bid price
            }

             //--- Cancel logic before switching to sell mode ---
            if (clearActiveOrders && activeOrdersCount > 0) {
                for (ChildOrder order : activeOrders) {
                    if (order != null) {
                        logger.info("[MYALGO] Cancelling order: " + order);
                        if (activeOrdersCount == 1) {  // Last active order to cancel
                            clearActiveOrders = false;
                            shouldBuy = false;  // Switch to sell mode after all cancellations
                        }
                        return new CancelChildOrder(order);  // Cancel one order at a time
                    }
                }
            }
            logger.info("[MYALGO] End of evaluation cycle.");
            return NoAction.NoAction;
        } catch (Exception e) {
            logger.error("[MYALGO] Error during algo evaluation: " + e.getMessage(), e);
            return NoAction.NoAction;
        }
    }
//    // New calculateVWAP method that uses default VWAP if no data is available
//    private double calculateVWAP(SimpleAlgoState state, double defaultVWAP) {
//        long totalBidQuantity = 0;
//        long totalAskQuantity = 0;
//        long bidPriceQuantitySum = 0;
//        long askPriceQuantitySum = 0;
//
//        int bidLevels = state.getBidLevels();
//        for (int i = 0; i < bidLevels; i++) {
//            BidLevel bid = state.getBidAt(i);
//            if (bid != null) {
//                totalBidQuantity += bid.getQuantity();
//                bidPriceQuantitySum += bid.getPrice() * bid.getQuantity();
//            }
//        }
//
//        int askLevels = state.getAskLevels();
//        for (int i = 0; i < askLevels; i++) {
//            AskLevel ask = state.getAskAt(i);
//            if (ask != null) {
//                totalAskQuantity += ask.getQuantity();
//                askPriceQuantitySum += ask.getPrice() * ask.getQuantity();
//            }
//        }
//
//        long totalQuantity = totalBidQuantity + totalAskQuantity;
//        return totalQuantity > 0 ? (double) (bidPriceQuantitySum + askPriceQuantitySum) / totalQuantity : defaultVWAP;
//    }
//}
// Initialise VWAP based on best bid and best ask for entry point calculation
private void initialiseVWAP(SimpleAlgoState state) {
    // Retrieve the best bid and ask levels
    BidLevel bestBid = state.getBidAt(0);
    AskLevel bestAsk = state.getAskAt(0);

    // Ensure both best bid and ask are available
    if (bestBid != null && bestAsk != null) {
        long bidPrice = bestBid.getPrice();
        long askPrice = bestAsk.getPrice();

        // Calculate VWAP as the midpoint between best bid and ask prices
        vwap = (double) (bidPrice + askPrice) / 2.0;
        logger.info("[MYALGO] VWAP initialized using the midpoint of best bid and ask: " + vwap);
    } else {
        // Use default VWAP if either best bid or ask is unavailable
        vwap = DEFAULT_VWAP;
        logger.info("[MYALGO] No sufficient market data for best bid or ask. Using default VWAP: " + vwap);
    }
}


    // Update VWAP based on current market data
    private void updateVWAP(SimpleAlgoState state) {
        long totalBidQuantity = 0;
        long totalAskQuantity = 0;
        long bidPriceQuantitySum = 0;
        long askPriceQuantitySum = 0;

        int bidLevels = state.getBidLevels();
        for (int i = 0; i < bidLevels; i++) {
            BidLevel bid = state.getBidAt(i);
            if (bid != null) {
                totalBidQuantity += bid.getQuantity();
                bidPriceQuantitySum += bid.getPrice() * bid.getQuantity();
            }
        }
        int askLevels = state.getAskLevels();
        for (int i = 0; i < askLevels; i++) {
            AskLevel ask = state.getAskAt(i);
            if (ask != null) {
                totalAskQuantity += ask.getQuantity();
                askPriceQuantitySum += ask.getPrice() * ask.getQuantity();
            }
        }
        long totalQuantity = totalBidQuantity + totalAskQuantity;
        if (totalQuantity > 0) {
            vwap = (double) (bidPriceQuantitySum + askPriceQuantitySum) / totalQuantity;
            logger.info("[MYALGO] VWAP updated based on order book: " + vwap);
        } else {
            logger.warn("[MYALGO] No sufficient market data for VWAP calculation.");
        }
    }
}






