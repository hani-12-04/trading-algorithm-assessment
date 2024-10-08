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
    private static boolean shouldBuy = false; // when enabled, place buy orders
    private static boolean shouldSell = false; // when enabled, sell orders

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


//        if (clearActiveOrders == true && activeOrdersCount > 0) {
        // go over order book and cancel when a cancellable order is found
//            for (ChildOrder order : activeOrders) {
//                // cancel order if it's above the price limit
//                if (bidPrice >= priceLimit) {
//                    logger.info("[MYALGO] Price exceeded limit. Cancelling all buy orders");
//                    if (order != null) {
//                        logger.info("[ADDCANCELALGO] Cancelling order:" + order);
//                        return new CancelChildOrder(order);
//                   }
//                }
//            }
//        } else if (clearActiveOrders == true && activeOrdersCount == 0) {
//            // Stop cancelling orders and now place sell orders
//            clearActiveOrders = false;
//            shouldBuy = false;
//        }

        // Buy Logic
        if (bidPrice >= sellLimit) {
            shouldBuy = true;
            logger.info("[MYALGO] The current price is greater than sell limit. Placing buy order");
        }
        if (askPrice >= priceLimit) {
            shouldSell = true;
            logger.info("[MYALGO] The current ask price is greater the price limit. Placing sell order");
        }
        if (shouldBuy == true && activeOrdersCount < maxOrders) {
            logger.info("[MYALGO] The current price is below the price limit. Placing buy order");
            if (activeOrdersCount + 1 >= maxOrders) {
                clearActiveOrders = true;
            }
            return new CreateChildOrder(Side.BUY, quantity, bidPrice);
        }
        // sell if the price exceeds the sell limit
        if (shouldSell == true && activeOrdersCount < maxOrders) {
            // FOR TESTING
            logger.info("[MYALGO] The current price is below the price limit. Placing sell order");
//            logger.info("[MYALGO]. Placing buy order");
            if (activeOrdersCount + 1 >= maxOrders) {
                clearActiveOrders = true;
            }
            return new CreateChildOrder(Side.SELL, quantity, bidPrice);
        }
//            if (activeOrdersCount >= maxOrders) {
//                logger.info("[MYALGO] Max orders created. End Algo");
//                return NoAction.NoAction;
//            }
//            logger.info("[MYALGO] The price exceeds the sell limit. Placing sell order");
//            return new CreateChildOrder(Side.SELL, quantity, bidPrice);
//        }
//        else {
            logger.info("[MYALGO] No conditions met. Take no action");
            return NoAction.NoAction;
//        }
    }
}








