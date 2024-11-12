package codingblackfemales.gettingstarted;


import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MyAlgoLogic implements AlgoLogic {
    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);

    // Constraints defining trading parameters
    private static final int maxOrders = 7; // The maximum number of active orders allowed
    private static final int quantity = 100; // The number of units per order

    // VWAP thresholds for buy and sell triggers
    private static final double VWAP_BUY_THRESHOLD = 0.99; // Buy if ask below VWAP
    private static final double VWAP_SELL_THRESHOLD = 0.80; // Sell if bid is above to VWAP

    // Flags to track the first trade and VWAP initialisation
    private boolean vwapInitialised = false; // Flag to track if initial VWAP has been set

    // Default VWAP value in case of unavailable market data
    private static final double DEFAULT_VWAP = 100.0;

    // Current VWAP value used for trading decisions
    private double vwap;

    // Map to track order times for managing cancellations
    private static HashMap<Long, LocalTime> orderTimeHashmap = new HashMap<>();

    @Override
    public Action evaluate(SimpleAlgoState state) {
        try {
            // Check for any null state or missing child orders and log any issues
            Action action = checkNoAction(state);
            if (action != null) {
                return action;
            }

            // Log the current state of the order book
            var orderBookAsString = Util.orderBookToString(state);
            logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

            // Log the total count of all child orders
            var totalOrderCount = state.getChildOrders().size();
            logger.info("[MYALGO] Total child orders: " + totalOrderCount);

            // Retrieve active orders
            List<ChildOrder> activeOrders = state.getActiveChildOrders();
            int activeOrdersCount = activeOrders.size();
            logger.info("[MYALGO] Active child orders: " + activeOrdersCount);

            storeUnfilledOrders(state);

            // Check if it's the end of the trading day and cancel all remaining orders if so
            action = checkShouldCancelEndOfDay(activeOrders, activeOrdersCount);
            if (action != null) {
                return action;
            }

            // Check if any orders have expired based on their times and cancel if needed
            action = checkShouldCancelByOrderTime(activeOrders);
            if (action != null) {
                return action;
            }

            // Update or initialise VWAP based on the order book
            if (!vwapInitialised) {
                initialiseVWAP(state);
                vwapInitialised = true;
            } else {
                updateVWAP(state); // Continuously update VWAP after the first calculation
            }

            // Retrieve and log the best bid and ask prices, handle potential null values
            BidLevel bestBid = state.getBidAt(0);
            AskLevel bestAsk = state.getAskAt(0);
            if (bestBid == null || bestAsk == null) {
                logger.warn("[MYALGO] Best bid or ask price is null!");
                return NoAction.NoAction;
            }

            long bidPrice = bestBid.price; // the highest bid price
            long askPrice = bestAsk.price; // the lowest ask price
            logger.info("[MYALGO] Best Bid: " + bidPrice + ", Best Ask: " + askPrice);


            // Calculate buy and sell thresholds based on current VWAP
            double buyThresholdPrice = vwap * VWAP_BUY_THRESHOLD;
            double sellThresholdPrice = vwap * VWAP_SELL_THRESHOLD;

            logger.info("[MYALGO] VWAP: " + vwap + ", Buy Threshold: " + buyThresholdPrice + ", Sell Threshold: " + sellThresholdPrice);

            // Buy logic
            action = checkShouldBuy(askPrice, buyThresholdPrice, activeOrdersCount);
            if (action != null) {
                return action;
            }

            // Sell logic
            action = checkShouldSell(bidPrice, sellThresholdPrice, activeOrdersCount);
            if (action != null) {
                return action;
            }

            // No action by default if none of the trading conditions are met
            return NoAction.NoAction;
        } catch (Exception e) {
            logger.error("[MYALGO] Error during algo evaluation: " + e.getMessage(), e);
            return NoAction.NoAction;
        }
    }

    /**
     * Validates the algorithm state; returns "No Action" if state or child orders are null.
     */
    private Action checkNoAction(SimpleAlgoState state) {
        if (state == null) {
            logger.error("[MYALGO] Algo state is null!");
            return NoAction.NoAction;
        }
        if (state.getChildOrders() == null) {
            logger.warn("[MYALGO] Child orders are null!");
            return NoAction.NoAction;
        }
        return null;
    }

    private void storeUnfilledOrders(SimpleAlgoState state){
    // Process any unfilled orders to track their times in the orderTimes map
    List<ChildOrder> unfilledOrders = state.getChildOrders().stream()
            .filter(order -> order.getFilledQuantity() == 0).toList();

            for(ChildOrder order : unfilledOrders) {
                if (orderTimeHashmap.containsKey(order.getOrderId())) {
                    continue;
                }
                orderTimeHashmap.put(order.getOrderId(), TradingDayClockService.getCurrentTime());
            }
    }

    private Action checkShouldBuy(long askPrice, double buyThresholdPrice, int activeOrdersCount) {
        if (activeOrdersCount < maxOrders && askPrice < buyThresholdPrice) {
            logger.info("[MYALGO] Ask price is below VWAP buy threshold. Placing BUY order.");
            return new CreateChildOrder(Side.BUY, quantity, askPrice);
        }
        return null;
    }

    private Action checkShouldSell(long bidPrice, double sellThresholdPrice, int activeOrdersCount) {
        if (activeOrdersCount < maxOrders && bidPrice >= sellThresholdPrice) {
            logger.info("[MYALGO] Bid price is above VWAP sell threshold. Placing SELL order.");
            return new CreateChildOrder(Side.SELL, quantity, bidPrice);
        }
        return null;
    }

    /**
     * Monitors each of the orders time; cancels orders that have been unfilled for too long.
     * Ensures that older orders are removed to free up capacity for new orders.
     */
    private Action checkShouldCancelByOrderTime(List<ChildOrder> activeOrders) {
        // Define allOrderIds to hold all order IDs in the hashmap
        Set<Long> allOrderIds = orderTimeHashmap.keySet(); // getting all the orderId's in the hashmap

        // Iterate over the order IDs in the set
        for (long orderId : allOrderIds) {
            if (orderTimeHashmap.get(orderId).compareTo(TradingDayClockService.getCurrentTime()) == -1) {
                ChildOrder childOrder = activeOrders.stream().filter(order -> order.getOrderId()==orderId).findFirst().orElse(null);
                orderTimeHashmap.remove(orderId);
                if(childOrder.getFilledQuantity() == 0) {
                    return new CancelChildOrder(childOrder);
                }
            }
        }
        return null;
    }

    /**
     * Cancels all active orders at the end of the trading day, preventing positions from remaining open overnight.
     */
    private Action checkShouldCancelEndOfDay(List<ChildOrder> activeOrders, int activeOrdersCount) {
        if (activeOrdersCount > 0 && TradingDayClockService.isEndOfDay()) {
            for (ChildOrder order : activeOrders) {
                if (order != null) {
                    logger.info("[MYALGO] Cancelling end-of-day order: " + order);
                    return new CancelChildOrder(order);
                }
            }
        } else if(TradingDayClockService.isEndOfDay()){
            logger.info("[MYALGO] End of evaluation cycle");
            return NoAction.NoAction;
        }
        return null;
    }

    /**
     * Initialises and updates the VWAP to serve as a price benchmark.
     * Determines if the current market prices are favorable for buying or selling.
     */
    private void initialiseVWAP(SimpleAlgoState state) {
        // Retrieve the best bid and ask levels
        BidLevel bestBid = state.getBidAt(0);
        AskLevel bestAsk = state.getAskAt(0);

        // Ensure both best bid and ask are available
        if (bestBid != null && bestAsk != null) {
            long bidPrice = bestBid.getPrice();
            long askPrice = bestAsk.getPrice();

            // Calculate VWAP as the midpoint between best bid and ask prices
            vwap = (double) (bidPrice + askPrice) / 2.0;
            logger.info("[MYALGO] VWAP initialised using the midpoint of best bid and ask: " + vwap);
        } else {
            // Use default VWAP if either best bid or ask is unavailable
            vwap = DEFAULT_VWAP;
            logger.info("[MYALGO] No sufficient market data for best bid or ask. Using default VWAP: " + vwap);
        }
    }

    /**
     * Periodically updates VWAP based on current market data, ensuring that
     * the algorithm reacts to changes in bid and ask quantities.
     */
    private void updateVWAP(SimpleAlgoState state) {
        long totalBidQuantity = 0;
        long totalAskQuantity = 0;
        long bidPriceQuantitySum = 0;
        long askPriceQuantitySum = 0;

        int bidLevels = state.getBidLevels();
        for (int i = 0; i < bidLevels; i++) {
            BidLevel bid = state.getBidAt(i);
            if (bid != null) {
                totalBidQuantity += bid.getQuantity();
                bidPriceQuantitySum += bid.getPrice() * bid.getQuantity();
            }
        }
        int askLevels = state.getAskLevels();
        for (int i = 0; i < askLevels; i++) {
            AskLevel ask = state.getAskAt(i);
            if (ask != null) {
                totalAskQuantity += ask.getQuantity();
                askPriceQuantitySum += ask.getPrice() * ask.getQuantity();
            }
        }
        long totalQuantity = totalBidQuantity + totalAskQuantity;
        if (totalQuantity > 0) {
            vwap = (double) (bidPriceQuantitySum + askPriceQuantitySum) / totalQuantity;
            logger.info("[MYALGO] VWAP updated based on order book: " + vwap);
        } else {
            logger.warn("[MYALGO] No sufficient market data for VWAP calculation.");
        }
    }
}






