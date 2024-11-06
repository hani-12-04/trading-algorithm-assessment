package codingblackfemales.gettingstarted;

import codingblackfemales.algo.AlgoLogic;
import messages.order.Side;
import org.junit.Test;
import codingblackfemales.sotw.ChildOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test plugs together all of the infrastructure, including the order book (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data coming back from the order book.
 *
 * If you cross the srpead (i.e. you BUY an order with a price which is == or > askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the childOrders of the state object.
 *
 * If you cancel the order your child order will show the order status as cancelled in the childOrders of the state object.
 *
 * First Tick: This is when the orders are created based on the initial market conditions.
 * Second Tick: This is when the market moves towards your orders, potentially filling them or causing other actions to take place.
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Test
    public void testTotalFilledQuantity() throws Exception {
        // Step 1: Send initial market tick to create and fill some orders
        send(createTick());

        //Assert that the number of orders created is correct
        assertEquals(20,container.getState().getChildOrders().size());

        // Step 2: Send another tick to simulate market data moving and orders being filled
        send(createTick2());

        // Step 3: Get the current state of orders
        var state = container.getState();

        // Step 4: Calculate the total filled quantity
        long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();

        // Step 5: Print the total filled quantity
        System.out.println("Total filled quantity: " + filledQuantity);

        // Step 6: Assert that the total filled quantity matches the expected value
        assertEquals(751, filledQuantity);

        // Test 2: Total number of partially filled orders
        // Step 7: Count the number of partially filled orders
        long partiallyFilledOrdersCount = state.getChildOrders().stream()
                .filter(order -> order.getFilledQuantity() > 0 && order.getFilledQuantity() < order.getQuantity())  // Partially filled orders
                .count();

        // Step 8: Find and print the details of each partially filled order
        state.getChildOrders().stream()
                .filter(order -> order.getFilledQuantity() > 0 && order.getFilledQuantity() < order.getQuantity())  // Partially filled orders
                .forEach(order -> {
                    System.out.println("Partially Filled Order ID: " + order.getOrderId() +
                            ", Filled Quantity: " + order.getFilledQuantity() +
                            ", Remaining Quantity: " + (50 - order.getFilledQuantity()) +  // Remaining unfilled quantity
                            ", Total Quantity: 50");
                });

        // Print the number of partially filled orders
        System.out.println("Number of partially filled orders: " + partiallyFilledOrdersCount);

        // Assert the expected number of partially filled orders
        assertEquals(1, partiallyFilledOrdersCount);  // Adjust expected value

        // Test 3: Total number of fully filled orders
        //Count the number of fully filled orders
        long fullyFilledOrders = state.getChildOrders().stream()
                .filter(order -> order.getFilledQuantity() == order.getQuantity())  // Fully filled
                .count();

        // Find and print the details of each partially filled order
        state.getChildOrders().stream()
                .filter(order -> order.getFilledQuantity() == order.getQuantity())  // Fully filled orders
                .forEach(order -> {
                    System.out.println("Fully Filled Order ID: " + order.getOrderId() +
                            ", Filled Quantity: " + order.getFilledQuantity() +
                            ", Price: " + order.getPrice());
                });

        //Print the number of fully filled orders
        System.out.println("Number of fully filled orders: " + fullyFilledOrders);

        //Assert the number of fully filled orders is as expected
        assertEquals(15, fullyFilledOrders);  // Adjust based on expected fully filled orders

        // Calculate profit based on buy and sell orders
        long totalBuyCost = 0;
        long totalSellRevenue = 0;

        // Loop through all child orders in the container's state
        for (ChildOrder order : container.getState().getChildOrders()) {
            long filledQty = order.getFilledQuantity(); // Get filled quantity (includes full and partial)
            long price = order.getPrice(); // Get price for this order

            if (order.getSide() == Side.BUY && filledQty > 0) {
                // For buy orders, calculate total buy cost
                totalBuyCost += filledQty * price;
            } else if (order.getSide() == Side.SELL && filledQty > 0) {
                // For sell orders, calculate total sell revenue
                totalSellRevenue += filledQty * price;
            }
        }
        // Step 10: Calculate the profit
        long profit = totalSellRevenue - totalBuyCost;

        // Step 11: Print the profit
        System.out.println("Total Buy Cost: £" + totalBuyCost);
        System.out.println("Total Sell Revenue: £" + totalSellRevenue);
        System.out.println("Total Profit: £" + profit);

        // Step 12: Assert that the profit is as expected
        assertTrue(profit >= 0); // Ensure profit is non-negative as expected
    }

}
