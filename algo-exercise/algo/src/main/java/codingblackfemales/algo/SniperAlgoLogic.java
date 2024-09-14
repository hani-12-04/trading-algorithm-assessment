package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;
// declating SniperAlgoLogic class which implements AlgoLogic interface
public class SniperAlgoLogic implements AlgoLogic {

    // creating a logger for SniperAlgoLogic class to log info
    private static final Logger logger = LoggerFactory.getLogger(SniperAlgoLogic.class);

    @Override
    public Action evaluate(SimpleAlgoState state) {

        // logging information that the algo has started
        logger.info("[SNIPERALGO] In Algo Logic....");

        // converting order book into a string format
        final String book = Util.orderBookToString(state);

        // logging the current state of order book
        logger.info("[SNIPERALGO] Algo Sees Book as:\n" + book);

        // retrieving the best ask price (the lowst price seller is willing to accept) from the order book
        // state.getAskAt(0) represents best or lowest price in the market known as fatTuch
        final AskLevel farTouch = state.getAskAt(0);

        //setting the quantity and price for this order
        long quantity = farTouch.quantity;
        long price = farTouch.price;

        // algo checks how many child orders it currently has in the market.
        // if theres fewer 5, it will create more.
        if (state.getChildOrders().size() < 5) {
            //then keep creating a new one
            logger.info("[SNIPERALGO] Have:" + state.getChildOrders().size() + " children, want 5, sniping far touch of book with: " + quantity + " @ " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        } else {
            logger.info("[SNIPERALGO] Have:" + state.getChildOrders().size() + " children, want 5, done.");
            return NoAction;
        }
    }
   // @Override
   //public long evaluate(SimpleAlgoState state, long size) {
    //    return 0;
    //}
}
// goal: sniper algo aims to "snipe" the best ask price, so tryig to buy as much as possible at the lowest price(the best ask)
// until it has 5 child orders.
// flow:
// 1. logs the current state of the order book.
// 2. retrieves the best ask price.
// 3. if fewer than 5 chld orders exists, it creates a new buy order at the best ask price for the available quantity.
// 4. if 5 or more child orders exist, it does not hing.

// this algo follows a "sniping" strategy, where it aggressively tries to buy at the best available price on the ask side
// until it reaches a treshold of 5 active orders.