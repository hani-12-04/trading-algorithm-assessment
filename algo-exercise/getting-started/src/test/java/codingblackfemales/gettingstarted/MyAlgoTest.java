package codingblackfemales.gettingstarted;

import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.action.Action;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.marketdata.BidLevel;
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
    public void testBidPriceWithinLimit() throws Exception {
        //Simulate a tick where the price exceeds the limit
        send(createTick()); // does this tick update the market price?

        // Retrieve the updated bid and ask levels
        BidLevel bestBid = container.getState().getBidAt(0); // Highest bid price

        // Check if the bid price now exceeds the price limit
        long bidPrice = bestBid.price;
        assertTrue(bidPrice >= 100); // Assuming price limit is 99.00
    }

    @Test
    public void testEvaluateNoActionWhenNoConditionsAreMet() throws Exception {
        send(createTick());

        Action action = createAlgoLogic().evaluate(container.getState());

        //simple assert to check if no conditions are met, there's no action returned
        assertEquals(NoAction.NoAction, action);
    }

    @Test
    public void testMaxActiveBuyOrdersLimit() throws Exception {
        //create a sample market data tick....
        send(createTick());

        //simple assert to check we only create 10 active orders (maxOrders = 10)
        assertEquals(container.getState().getActiveChildOrders().size(), 10);
    }

    @Test
    public void testCancelBuyOrdersWhenPriceExceedsLimit() throws Exception {
        MyAlgoLogic algo = new MyAlgoLogic();

        // Step 1: Send ticks to simulate placing 10 buy orders below the price limit
        send(createTick());

        // Assert that 10 buy orders were placed
        assertEquals(10, container.getState().getActiveChildOrders().size());

        // Step 2: Simulate a tick where the price exceeds the limit
        send(createTick());

        // Evaluate the algo logic after the price exceeds the limit
        Action action = algo.evaluate(container.getState());

        // Assert that the algo responds by cancelling a buy order
        assertEquals(true, action instanceof CancelChildOrder);

    }
}
