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

        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n{}", orderBookAsString);

        // Retrieve the best bid and ask prices
        BidLevel bestBid = state.getBidAt(0); // the highest bid price - the price buyers are willing to pay
        AskLevel bestAsk = state.getAskAt(0); // the lowest ask price - the price sellers are asking for

        // setting a threshold price below which we are willing to buy
        long buyThreshold = 100;  // the max price we are willing to buy shares

        // Checking if we hold no shares, and the current ask price is low enough to buy
        if (position == 0 && bestAsk.price < buyThreshold) {
            logger.info("[MYALGO] No shares held and ask price (" + bestAsk.price + ") is below threshold (" + buyThreshold + "). Buying " + bestAsk.quantity + " shares.");

            // Update position and entry buy price after buying
            position = bestAsk.quantity;
            entryBuyPrice = bestAsk.price;

            return new CreateChildOrder(Side.BUY, bestAsk.quantity, bestAsk.price);
        } else {
            logger.info("[MYALGO] Ask price (" + bestAsk.price + ") is too high. Not buying at this time.");
        }

        // Checking if we hold shares and the bid price is high enough to sell
        if (position > 0 && bestBid.price > entryBuyPrice) {
            logger.info("[MYALGO] Holding shares, and bid price (" + bestBid.price + ") is above entry price (" + entryBuyPrice + "). Selling.");

            // Reset position after selling
            long sharesToSell = position;
            long position = 0;

            return new CreateChildOrder(Side.SELL, sharesToSell, bestBid.price);
        } else {
            logger.info("[MYALGO] Bid price (" + bestBid.price + ") is not high enough. Holding position for now.");
        }
        return NoAction.NoAction; // no buy or sell conditions are met, take no action
    }

    @Override
    public long evaluate(SimpleAlgoState state, long size) {
        return 0;
    }
}


