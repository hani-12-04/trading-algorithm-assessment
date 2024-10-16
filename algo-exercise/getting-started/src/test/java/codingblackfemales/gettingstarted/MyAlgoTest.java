package codingblackfemales.gettingstarted;

import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.action.Action;
import codingblackfemales.algo.AlgoLogic;
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
    public void testDispatchThroughSequencer() throws Exception {
        // Step 1: Send a tick to simulate the market (only sent once)
        send(createTick()); // Simulate a tick with default bid and ask prices

        // Step 2: Retrieve the updated bid and ask levels
        BidLevel bestBid = container.getState().getBidAt(0); // Highest bid price
        AskLevel bestAsk = container.getState().getAskAt(0); // Lowest ask price
        long bidPrice = bestBid.price;
        long askPrice = bestAsk.price;

        // Step 3: Assert that the bid and ask prices are within the expected limits
        assertTrue(bidPrice >= 91); // assuming 91 as sell limit
        assertTrue(askPrice <= 115); // assuming 115 as price limit

        // Step 4: Test the algorithm's evaluation for buy orders
        Action action = createAlgoLogic().evaluate(container.getState());

        // Step 5: Assert that no action is taken (NoAction) when conditions are not met
        assertEquals(NoAction.NoAction, action);

        // Step 6: Simulate the conditions for canceling an order
//        send(createTick());

//        // Step 7: Evaluate the algorithm again after the market change
//        Action CancelChildOrder = createAlgoLogic().evaluate(container.getState());
//
//        // Step 8: Assert that the algorithm returns a CancelChildOrder action when conditions are met
//        assertEquals(CancelChildOrder, action);

        // Step 9: Check if the algorithm has placed or canceled orders correctly
        // Check no more than 10 active child orders created at once
        assertEquals(container.getState().getActiveChildOrders().size(),10); // maxOrders = 10;

        // Step 10: Verify the total number of child orders (active + canceled)
        assertEquals(container.getState().getChildOrders().size(), 20);
    }
}







