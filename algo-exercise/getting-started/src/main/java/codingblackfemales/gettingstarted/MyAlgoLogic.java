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

import java.util.ArrayList;
import java.util.List;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // constraints for the price limit
    private static final double priceLimit = 99.00;
    private static final double sellLimit = 105.00; // sell when the price reaches £105
    private static final int maxOrders = 10; // max allowed orders in the orderbook
    private static final int quantity = 50; // quantity for each order

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

        //make sure we have an exit condition
        if (totalOrderCount >= maxOrders) {
            logger.info("[MYALGO] Maximum order count reached. No more buy orders will be placed.");
            return NoAction.NoAction;
        }

        // Retrieve the best bid and ask prices
        BidLevel bestBid = state.getBidAt(0); // the highest bid price - the price buyers are willing to pay
        AskLevel bestAsk = state.getAskAt(0); // the lowest ask price - the price sellers are asking for

        // Actual price
        long bidPrice = bestBid.price; // the highest bid price
        long askPrice = bestAsk.price; // the lowest ask price

        // Place buy orders until you reach 10
        // bidPrice < priceLimit && - not working
        if (activeOrdersCount < maxOrders) {
            logger.info("[MYALGO] The current price is below the price limit. Placing buy order");
            return new CreateChildOrder(Side.BUY, quantity, bidPrice);
        }

        // cancel all buy orders if the price has exceeded the priceLimit and start selling
        // bidPrice >= priceLimit &&
        else if (bidPrice >= priceLimit && activeOrdersCount > maxOrders) {
            logger.info("[MYALGO] Price exceeded limit. Cancelling all buy orders");
            final var option = activeOrders.stream().findFirst();
            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[ADDCANCELALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            } else{
                return NoAction.NoAction;
            }
        }

        // sell if the price exceeds the sell limit
        else if (bidPrice > sellLimit) {
            logger.info("[MYALGO] The price exceeds the sell limit. Placing sell order");
            return new CreateChildOrder(Side.SELL, quantity, bidPrice);
        }
        // if no conditions are met
        else {
            logger.info("[MYALGO] No condtions met. Take no action");
            return NoAction.NoAction;
        }
    }
}







