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
    public void testDispatchThroughSequencer() throws Exception {
        //Send a tick to simulate the market (only sent once)
        send(createTick()); // Simulate a tick with default bid and ask prices

        //Retrieve the updated bid and ask levels
        BidLevel bestBid = container.getState().getBidAt(0); // Highest bid price
        AskLevel bestAsk = container.getState().getAskAt(0); // Lowest ask price
        long bidPrice = bestBid.price;
        long askPrice = bestAsk.price;

        //Assert that the bid and ask prices are within the expected limits
        assertTrue(bidPrice >= 91); // assuming 91 as sell limit
        assertTrue(askPrice <= 115); // assuming 115 as price limit

        //Evaluate the algorithm to trigger order placement
        Action action = createAlgoLogic().evaluate(container.getState());
//
//        //Assert that no action is taken (NoAction) when conditions are not met
//        assertEquals(NoAction.NoAction, action);

        // Check for exactly 10 orders are created
        assertEquals(10,container.getState().getActiveChildOrders().size()); // maxOrders = 10;

        //Check for exactly 20 child orders are created (active + cancelled)
        assertEquals(20,container.getState().getChildOrders().size());
    }
}







