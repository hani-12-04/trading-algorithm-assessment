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

    // constraints for the price limit
    private static final long priceLimit = 115;
    private static final long sellLimit = 91; // sell when the price reaches £105
    private static final int maxOrders = 10; // max allowed orders in the orderbook
    private static final int quantity = 50; // quantity for each order
    private static boolean clearActiveOrders = false; // when enabled will clear all active orders
    private static boolean shouldBuy = true; // when enabled, place buy orders

    @Override
    public Action evaluate(SimpleAlgoState state) {
        try {
            // Check if state is null before proceeding
            if (state == null) {
                logger.error("[MYALGO] Algo state is null!");
                return NoAction.NoAction;
            }
            // log the current state of the order book
            var orderBookAsString = Util.orderBookToString(state);
            logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

            // Get total order count, with a simple null check
            if (state.getChildOrders() == null) {
                logger.warn("[MYALGO] Child orders are null!");
                return NoAction.NoAction;
            }

            // Retrieve the total Order count
            var totalOrderCount = state.getChildOrders().size();
            logger.info("[MYALGO] Total child orders: " + totalOrderCount);

            // Get active orders count, simple null check
            List<ChildOrder> activeOrders = state.getActiveChildOrders();
            if (activeOrders == null) {
                logger.warn("[MYALGO] Active orders list is null!");
                return NoAction.NoAction;
            }
            int activeOrdersCount = activeOrders.size();
            logger.info("[MYALGO] Active child orders: " + activeOrdersCount);

            // Get best bid and ask prices, handle potential null values
            BidLevel bestBid = state.getBidAt(0);
            AskLevel bestAsk = state.getAskAt(0);
            if (bestBid == null || bestAsk == null) {
                logger.warn("[MYALGO] Best bid or ask price is null!");
                return NoAction.NoAction;
            }

            logger.info("[MYALGO] Best Bid prices" + bestBid);
            logger.info("[MYALGO] Best Ask prices" + bestAsk);

            long bidPrice = bestBid.price; // the highest bid price
            long askPrice = bestAsk.price; // the lowest ask price

            // ---- Buy Logic ---- //
            if (shouldBuy && !clearActiveOrders && activeOrdersCount < maxOrders && askPrice <= priceLimit) {
                logger.info("[MYALGO] The current ask price is below or equal to the price limit. Placing buy order.");
                if (activeOrdersCount + 1 >= maxOrders) {
                    logger.info("[MYALGO] Max orders reached. Switching to cancellation mode.");
                    clearActiveOrders = true;  // Start clearing orders when max is reached
                    shouldBuy = false;  // Stop buying once max orders reached
                }
                return new CreateChildOrder(Side.BUY, quantity, askPrice);  // Buy at ask price
            }
            // ---- Sell Logic ---- //
            if (!shouldBuy && !clearActiveOrders && activeOrdersCount < maxOrders && bidPrice >= sellLimit) {
                logger.info("[MYALGO] The current bid price is greater than or equal to the sell limit. Placing sell order.");
                if (activeOrdersCount >= maxOrders) {
                    logger.info("[MYALGO] Max orders reached.End Algo.");
                    return NoAction.NoAction;
                }
                return new CreateChildOrder(Side.SELL, quantity, bidPrice);  // Sell at bid price
            }
            // ---- Cancel logic ---- //
            if (clearActiveOrders && activeOrdersCount > 0) {
                logger.info("[MYALGO] Clearing active orders.");
                for (ChildOrder order : activeOrders) {
                    if (order != null) {
                        if (activeOrdersCount == 1) {
                            clearActiveOrders = false;
                        }
                        logger.info("[MYALGO] Cancelling order: " + order);
                        return new CancelChildOrder(order);  // Cancel one order at a time
                    }
                }
            }
            logger.info("[MYALGO] End algo");
            return NoAction.NoAction;

        } catch (Exception e) {
            logger.error("[MYALGO] Error during algo evaluation: " + e.getMessage(), e);
            return NoAction.NoAction;
        }
    }
}










