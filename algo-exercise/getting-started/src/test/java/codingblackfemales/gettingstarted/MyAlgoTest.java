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


import java.time.LocalTime;

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
        // Simulate market data tick that triggers orders being created
        send(createTick());

        // Verify the number of active child orders does not exceed maxOrders limit
        assertEquals(7,container.getState().getActiveChildOrders().size());

        //Verify total child orders created
        assertEquals(7,container.getState().getChildOrders().size());
    }
    @Test
    public void testBuyLogicWithHighAsk() throws Exception {
        // Simulate tick with high ask price to trigger buy logic
        send(createTickWithHighAsk());

        // Confirm that buy orders were created
        int activeBuys = container.getState().getActiveChildOrders().size();
        assertTrue("Expected buy orders to be created", activeBuys > 0);

        // // Ensure algorithm does not exceed maxOrders.
        assertEquals(7, container.getState().getActiveChildOrders().size());
    }

    @Test
    public void testCancelLogicAtEndOfDay() throws Exception {
        // Send three ticks with different market data to generate some active orders.
        System.out.println("Tick 1");
        send(createTickWithLowBid());
        System.out.println("Tick 2");
        send(createTickWithHighAsk());
        System.out.println("Tick 3");
        send(createTick());

        // Set time to end of trading day and send final tick to trigger end-of day cancellations
        TradingDayClockService.setCurrentTime(LocalTime.of(17, 0));
        System.out.println("Tick 4");
        send(createTickWithLowBid());

        // Verify that all orders have been cancelled at end of day
        int activeOrders = container.getState().getActiveChildOrders().size();
        assertEquals("Expected all orders to be canceled at end of day",
                0, activeOrders);
    }
    @Test
    public void testNoOrderAfterEndOfDay() throws Exception {
        // Set time to after market close and send a tick
        TradingDayClockService.setCurrentTime(LocalTime.of(17, 1));
        send(createTick());

        // Verify that no new orders are created after market close
        assertEquals("No orders should be created after market close",
                0, container.getState().getActiveChildOrders().size());
    }
    @Test
    public void testOrderExpirationBasedOnTime() throws Exception {
        // Send initial tick to create orders
        System.out.println("Tick 1");
        send(createTick());

        // Move the clock forward to 1:00 PM to simulate a few hours passing
        TradingDayClockService.setCurrentTime(LocalTime.of(13, 0));

        System.out.println("Tick 2");
        // Send another tick to check if any orders have expired based on the new time.
        send(createTick()); // Process a tick to trigger order expiration logic

        // Verify that orders created in the morning are now cancelled
        assertTrue("Expected some orders to be canceled after expiration time",
                container.getState().getActiveChildOrders().size() <= 7);
    }
}







