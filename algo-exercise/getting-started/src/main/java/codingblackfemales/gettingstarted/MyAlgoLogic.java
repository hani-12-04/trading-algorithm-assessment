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

/**
 * MyAlgoLogic class implements the AlgoLogic interface to provide a basic algorithm for
 * placing and cancelling buy and sell orders in response to market conditions.
 *
 * This algorithm operates based on a set of defined constraints such as price limits,
 * maximum active orders, and order quantity.
 *
 * The algorithm follows three main stages:
 * - Buy orders are placed when the ask price is below or equal to the defined price limit.
 * - Sell orders are placed when the bid price is above or equal to the sell limit.
 * - Active orders are cancelled when the maximum number of orders is reached.
 *
 *  Key features:
 * - Buy logic is triggered when conditions for placing a buy order (such as price limit and active order count) are met.
 * - Sell logic is executed based on sell conditions (such as bid price exceeding the sell limit).
 * - Orders are gradually cancelled when active orders exceed the predefined maximum.
 * - The algorithm is designed to log all key activities and handle null states or exceptions gracefully.
 */

public class MyAlgoLogic implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // Define constraints for trading parameters
    private static final long priceLimit = 115; // Max price to place a buy order
    private static final long sellLimit = 91; // Min price to place a sell order
    private static final int maxOrders = 10; // Maximum allowed active orders
    private static final int quantity = 50; // Quantity for each order
    private static boolean clearActiveOrders = false; // Flag to enable clearing active orders
    private static boolean shouldBuy = true; // Flag to enable placing buy orders


    @Override
    public Action evaluate(SimpleAlgoState state) {
        try {
            // Check if state is null before proceeding
            if (state == null) {
                logger.error("[MYALGO] Algo state is null!");
                return NoAction.NoAction;
            }

            // Log the current state of the order book
            var orderBookAsString = Util.orderBookToString(state);
            logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

            // Check for null child orders and log a warning
            if (state.getChildOrders() == null) {
                logger.warn("[MYALGO] Child orders are null!");
                return NoAction.NoAction;
            }

            // Retrieve the count of child orders
            var totalOrderCount = state.getChildOrders().size();
            logger.info("[MYALGO] Total child orders: " + totalOrderCount);

            // Retrieve active orders and check for potential null values
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

            // Retrieve bid and ask prices
            long bidPrice = bestBid.price;
            long askPrice = bestAsk.price;
            logger.info("[MYALGO] Best Bid price: " + bidPrice);
            logger.info("[MYALGO] Best Ask price: " + askPrice);


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
                            clearActiveOrders = false; // Disable clearing when last order is reached
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












