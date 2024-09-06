package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.sotw.SimpleAlgoState; // represents the state of the algo, such as the current state market data, active orders etc.
import codingblackfemales.sotw.marketdata.BidLevel; //price level in the order book on the bid side(buy side)
import codingblackfemales.util.Util;
import messages.order.Side; // represents the order side, buying (BUY) or selling (SELL)
import org.slf4j.Logger; // logging framework for logging messages
import org.slf4j.LoggerFactory;

public class AddCancelAlgoLogic implements AlgoLogic { //implemeneted AlgoLogic interface

    // creating a logger instance for the class, used to logging events
    private static final Logger logger = LoggerFactory.getLogger(AddCancelAlgoLogic.class);

    //creating the core logic method. taking the current state Algo (SimpleAlgoState) as iput and deciding which action to take.
    @Override 
    public Action evaluate(SimpleAlgoState state) {

        // logging a message indicating algortihm has started running 
        logger.info("[ADDCANCELALGO] In Algo Logic....");
        
        // converting order book into a string frmat and loggging it.
        // helps understanding what the algo sees in the market
        final String book = Util.orderBookToString(state);

        logger.info("[ADDCANCELALGO] Algo Sees Book as:\n" + book);

        // retrieving the total number of child orders
        var totalOrderCount = state.getChildOrders().size();

        //make sure we have an exit condition...
        // if there more than 20 orders, the algo exits early. this prevents execssive orders.
        if (totalOrderCount > 20) {
            return NoAction.NoAction;
        }


        // here we are checking if there are any active child orders
        final var activeOrders = state.getActiveChildOrders();

        // if an active child order is found, it logs that the order is being cancelled
        // and returns a CancelChildOrder action, which tells the algo to cancel that order.
        // if no active order is found, the algo does nothing.
        if (activeOrders.size() > 0) {

            final var option = activeOrders.stream().findFirst();

            if (option.isPresent()) {
                var childOrder = option.get();
                logger.info("[ADDCANCELALGO] Cancelling order:" + childOrder);
                return new CancelChildOrder(childOrder);
            }
            else{
                return NoAction.NoAction;
            }

        // if there are no active orders, the algo looks at the first bid level in the market data
        // so the best bid price and quantiy available.

        } else {

        // retreving the price and quantity from the BidLevel
            BidLevel level = state.getBidAt(0);
            final long price = level.price;
            final long quantity = level.quantity;
        // logging the intention to create a new order at this price and quantity.
            logger.info("[ADDCANCELALGO] Adding order for" + quantity + "@" + price);
        // returning a CreateChildOrder action to create a buy order(Side.BUY) at the retrieved price and quantity.
            return new CreateChildOrder(Side.BUY, quantity, price);
        }
    }
}

// main goal: the algo is changing between cancelling existing active child orders and 
// creating new buy orders based on the best bid price.

//Understanding this class:
// 1. Logging initial state of the market
// 2. Checking if there are too many orders; if yes, it stops.
// 3. If there active orders, it cancels the first one.
// 4. If no active orders exists, it creates a new buy order at the best bid price.
