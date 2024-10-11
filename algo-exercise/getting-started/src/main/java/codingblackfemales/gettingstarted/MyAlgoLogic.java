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
    private static final double priceLimit = 98.00;
    private static final double sellLimit = 100.00; // sell when the price reaches £105
    private static final int maxOrders = 10; // max allowed orders in the orderbook
    private static final int quantity = 50; // quantity for each order
    private static boolean clearActiveOrders = false; // when enabled will clear all active orders
    private static boolean shouldBuy = true; // when enabled, place buy orders
    private static boolean shouldSell = false; // when enabled, place sell orders

    @Override
    public Action evaluate(SimpleAlgoState state) {
        logger.info("[MYALGO] In Algo Logic....");

        // log the current state of the order book
        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        // Retrieve the total Order count
        var totalOrderCount = state.getChildOrders().size();
        logger.info("[MYALGO] Total child orders: " + totalOrderCount);

        // Retrieve active orders count
        List<ChildOrder> activeOrders = state.getActiveChildOrders();
        int activeOrdersCount = activeOrders.size();
        logger.info("[MYALGO] Active child orders: " + activeOrdersCount);

        // Retrieve the best bid and ask prices
        BidLevel bestBid = state.getBidAt(0); // the highest bid price - the price buyers are willing to pay
        AskLevel bestAsk = state.getAskAt(0); // the lowest ask price - the price sellers are asking for
        logger.info("[MYALGO] Best Bid prices" + bestBid);
        logger.info("[MYALGO] Best Ask prices" + bestAsk);

        // Actual price
        long bidPrice = bestBid.price; // the highest bid price
        long askPrice = bestAsk.price; // the lowest ask price


        // ---- Setting shouldBuy Logic ---- //
        // Set shouldBuy flag if the ask price is below or equal to the priceLimit
        if (askPrice <= priceLimit && !shouldSell && !clearActiveOrders) {
            shouldBuy = true;
            shouldSell = false;  // Ensure mutual exclusivity
            logger.info("[MYALGO] Setting shouldBuy to true. Ask price is below or equal to the price limit.");
        } else {
            shouldBuy = false;
        }

        // ---- Setting shouldSell Logic ---- //
        // Set shouldSell flag if the bid price is greater than or equal to the sellLimit
        if (bidPrice >= sellLimit && !shouldBuy && !clearActiveOrders) {
            shouldSell = true;
            shouldBuy = false;  // Ensure mutual exclusivity
            logger.info("[MYALGO] Setting shouldSell to true. Bid price is greater than or equal to the sell limit.");
        } else {
            shouldSell = false;
        }

        // ---- Cancel Logic inside Buy ---- //
        if (shouldBuy && activeOrdersCount < maxOrders) {
            // Check if any buy orders need to be canceled
            for (ChildOrder order : activeOrders) {
                if (order.getSide() == Side.BUY && askPrice > priceLimit) {
                    logger.info("[MYALGO] Ask price exceeded limit. Cancelling buy order: " + order);
                    return new CancelChildOrder(order);  // Cancel one order at a time
                }
            }

            // If no cancellations, proceed with placing the buy order
//            if (askPrice <= priceLimit) {
            logger.info("[MYALGO] The current ask price is below or equal to the price limit. Placing buy order.");
            if (activeOrdersCount + 1 >= maxOrders) {
                logger.info("[MYALGO] Max orders reached. Switching to cancellation mode.");
                clearActiveOrders = true;  // Start clearing orders when max is reached
                shouldBuy = false;  // Stop buying once max orders reached
            }
            return new CreateChildOrder(Side.BUY, quantity, askPrice);  // Buy at ask price
//            }
        }
        // ---- Cancel Logic inside Sell ---- //
        if (shouldSell && activeOrdersCount < maxOrders) {
            // Check if any sell orders need to be canceled
            for (ChildOrder order : activeOrders) {
                if (order.getSide() == Side.SELL && bidPrice < sellLimit) {
                    logger.info("[MYALGO] Bid price fell below sell limit. Cancelling sell order: " + order);
                    return new CancelChildOrder(order);  // Cancel one order at a time
                }
            }
            // If no cancellations, proceed with placing the sell order
//            if (bidPrice >= sellLimit) {
            logger.info("[MYALGO] The current bid price is greater than or equal to the sell limit. Placing sell order.");
            if (activeOrdersCount + 1 >= maxOrders) {
                logger.info("[MYALGO] Max orders reached. Switching to cancellation mode.");
                clearActiveOrders = true;  // Start clearing orders when max is reached
                shouldSell = false;  // Stop selling once max orders reached
            }
            return new CreateChildOrder(Side.SELL, quantity, bidPrice);  // Sell at bid price
//            }
        }
        // ---- Handle Order Cancellations ---- //
        if (clearActiveOrders && activeOrdersCount > 0) {
            logger.info("[MYALGO] Clearing active orders.");
            for (ChildOrder order : activeOrders) {
                if (order != null) {
                    logger.info("[MYALGO] Cancelling order: " + order);
                    return new CancelChildOrder(order);  // Cancel one order at a time
                }
            }
        } else if (clearActiveOrders && activeOrdersCount == 0) {
            logger.info("[MYALGO] All orders cancelled. Continuing with the opposite action.");
            clearActiveOrders = false;
            // Immediately transition to the opposite mode after all orders are cancelled
            if (shouldBuy == false) {
                shouldSell = true;  // Start selling
            } else if (shouldSell == false) {
                shouldBuy = true;  // Start buying
            }
        }
        if (!shouldBuy && !shouldSell) {
            logger.info("[MYALGO] No conditions met. Take no action");
            return NoAction.NoAction;
        }
        return NoAction.NoAction;
    }
}











