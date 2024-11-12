package codingblackfemales.gettingstarted;

import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.action.Action;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.order.Side;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * This test is designed to check your algo behavior in isolation of the order book.
 *
 * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on to creating the MyAlgoBackTest.
 *
 */
public class MyAlgoTest extends AbstractAlgoTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        //this adds your algo logic to the container classes
        return new MyAlgoLogic();
    }
    @Test
    public void testMaxOrdersLimit() throws Exception {
        // Simulate hitting the max orders limit with initial conditions
        send(createTick());
        // Check for exactly 7 active child orders are created
        assertEquals(7,container.getState().getActiveChildOrders().size());

        //Check for exactly 5 child orders are created
        assertEquals(7,container.getState().getChildOrders().size());
    }
    @Test
    public void testBuyLogicWithHighAsk() throws Exception {
        send(createTickWithHighAsk());

        // Check that buy orders were created when the ask price was favorable
        int activeBuys = container.getState().getActiveChildOrders().size();
        assertTrue("Expected buy orders to be created", activeBuys > 0);
        assertEquals(7, container.getState().getActiveChildOrders().size()); // Ensuring no more than maxOrders
    }
    @Test
    public void testSellLogicWithLowBid() throws Exception {
        send(createTickWithLowBid());

        // Check that sell orders were created when the bid price triggered sell conditions
        int activeSells = container.getState().getActiveChildOrders().size();
        assertTrue("Expected sell orders to be created", activeSells > 0);
        assertEquals(7, container.getState().getActiveChildOrders().size()); // Ensuring no more than maxOrders
    }
}







