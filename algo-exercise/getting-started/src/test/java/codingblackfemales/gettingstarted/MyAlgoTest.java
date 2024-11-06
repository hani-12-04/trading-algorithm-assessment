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
        // Send a tick to simulate the market (only sent once)
        send(createTick()); // Simulate a tick with default bid and ask prices

        // Verify no more than 10 active child orders
        assertEquals(10,container.getState().getActiveChildOrders().size()); // maxOrders = 10;
    }

    @Test
    public void testHighBidScenario() throws Exception {
        // Send high bid tick to simulate favorable conditions for selling
        send(createTickHighBid());

        // Check that a sell order was placed due to favorable bid prices
        assertTrue(container.getState().getActiveChildOrders().stream()
                .anyMatch(order -> order.getSide() == Side.SELL));
    }

    @Test
    public void testLowAskScenario() throws Exception {
        // Send low ask tick to simulate favorable conditions, but below priceLimit
        send(createTickLowAsk());

        // Check that no active buy orders were created due to the ask price being below the price limit
        assertEquals(0, container.getState().getActiveChildOrders().size());
    }
}







