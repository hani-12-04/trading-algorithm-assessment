package codingblackfemales.algo;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static codingblackfemales.action.NoAction.NoAction;

// declaring the class PassiveAlgoLogic which implements the AlgoLogic interface.
// So the PassiveAlgoLogic needs to provide an implementation of the evaluate method defined in the AlgoLogic Interface.
public class PassiveAlgoLogic implements AlgoLogic{

    // creating a logger for PassiveAlgoLogic class (used to output information, warning or error messages during the execution of the algo)
    private static final Logger logger = LoggerFactory.getLogger(PassiveAlgoLogic.class);

    // method where the algo logic resides
    @Override // overriding the evaluate method from AlgoLogic interface and taking in the current algo SimpleAlgoState.
    public Action evaluate(SimpleAlgoState state) {

        // logging a message showing the algo logic has started. this helps track the flow of execution during runtime.
        logger.info("[PASSIVEALGO] In Algo Logic....");

        // converting the current order book(state) to a string format for easier logging.
        final String book = Util.orderBookToString(state);

        // logging the order book so that the algo's current view of the market is recorded.
        // useful for debugging and understanding the algo's decision.
        logger.info("[PASSIVEALGO] Algo Sees Book as:\n" + book);

        // getting the best bid price from the order book.
        // state.getBidAt(0) refers to the highest bid price in the market(called "near touch").
        final BidLevel nearTouch = state.getBidAt(0);

        // algo has set a fixed order quantity of 75 units.
        long quantity = 75;
        // the price is set to the best bid price from the nearTouch.price
        long price = nearTouch.price;

        // checking for how many child orders currently exist.
        // algo wants to create new oders if fewer than 3 child orders are active
        if(state.getChildOrders().size() < 3){
            // if the condition above applies, it logs a message:
            logger.info("[PASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 3, joining passive side of book with: " + quantity + " @ " + price);
           // telling the algo to keep creating a new child order with the details:
            return new CreateChildOrder(Side.BUY, quantity, price);
        }else{
            logger.info("[PASSIVEALGO] Have:" + state.getChildOrders().size() + " children, want 3, done.");
            return NoAction;
        }

    }
}
// goal: this algo aims to maintain up to 3 child orders on the passive side of the market.(placing orders at the best bid price).
// flow: 
// 1. logs the current state of the algo and order book.
// 2. retrieving the best bid price from the market.
// 3. if fewer tha 3 child orders exist, it creates a new buy order for 75 units at the best bid price.
// 4. if 3 or more child orders already exist, the algo takes no action.

// this logic is a "passive" algo because it places orders on the bid side of the market and waits for them to be filled,
// rather than aggresively crossing the spread immediately fill orders at higher prices.
