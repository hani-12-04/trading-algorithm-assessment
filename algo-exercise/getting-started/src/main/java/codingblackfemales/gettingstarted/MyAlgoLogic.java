package codingblackfemales.gettingstarted;

import codingblackfemales.action.Action;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);
    private long position = 0;     // Variable to track how many shares we currently hold
    private long entryBuyPrice = 0; // Variable to track the price at which we first bought shares

    @Override
    public Action evaluate(SimpleAlgoState state) {

        // logging the state of the order book
        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n{}", orderBookAsString);

        // Retrieve the best bid and ask prices
        BidLevel bestBid = state.getBidAt(0); // the highest bid price - the price buyers are willing to pay
        AskLevel bestAsk = state.getAskAt(0); // the lowest ask price - the price sellers are asking for

        // setting a threshold price below which we are willing to buy
        long buyThreshold = 200;  // the max price we are willing to buy shares
        boolean actionTaken = false; // using a flag to check if we have taken any action

        // while we haven't taken any action yet, check if we should buy or sell
        while (!actionTaken) {
            // if we dont currently own any shares and the ask price is below our buy treshold
            if (position == 0 && bestAsk.price < buyThreshold) {
                logger.info("[MYALGO] No shares held and ask price (" + bestAsk.price + ") is below threshold (" + buyThreshold + "). Buying " + bestAsk.quantity + " shares.");
                // Update position and entry buy price after buying
                position += bestAsk.quantity; // increasing our position by the number of shares we're buying
                entryBuyPrice = bestAsk.price; // saving the price at which we bought the shares
                actionTaken = true; // we have taken action, so exit the loop
                return new CreateChildOrder(Side.BUY, bestAsk.quantity, bestAsk.price);
            }
            // Checking if we hold any shares and the bid price is higher than the price we paid
            else if (position > 0 && bestBid.price > entryBuyPrice) {
                logger.info("[MYALGO] Holding shares, and bid price (" + bestBid.price + ") is above entry price (" + entryBuyPrice + "). Selling.");

                // Reset position after selling
                long quantityToSell = position; // selling any shares we own
                position = 0; // after selling, we don't own any shares
                actionTaken = true; // we have taken action, so exit the loop
                return new CreateChildOrder(Side.SELL, quantityToSell, bestBid.price);
            }
            // if no action is needed so conditions weren't met, exit the loop
            else {
                actionTaken = true;
            }
        }
        return NoAction.NoAction; // no buy or sell conditions are met, take no action
    }
}


