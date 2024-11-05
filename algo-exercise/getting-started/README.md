# MyAlgoLogic: VWAP-Based Trading Algorithm

## Overview
MyAlgoLogic is an algorithmic trading strategy that uses Volume Weighted Average Price (VWAP) to make buy and sell decisions based on market data. The algorithm aims to enter and exit positions at optimal points based on VWAP and preset thresholds. It includes mechanisms for risk management, dynamic mode switching between buy and sell modes, and an order cap to prevent overexposure.

## Key Features
- **VWAP-Based Trading Decisions**: Uses VWAP as a benchmark for evaluating buy and sell opportunities.
- **Buy and Sell Thresholds**: Makes buy decisions if the ask price is below the VWAP and a set limit; switches to sell mode once maximum buy orders are placed.
- **Risk Management**: Limits the total number of active orders and cancels active buy orders before switching to sell mode.
- **Mode Switching**: Automatically switches between buy and sell modes based on trading volume and thresholds.

## Algorithm Flow
1. **Initialisation**: Sets an initial VWAP based on the best bid-ask midpoint.
2. **Order Evaluation**:
    - **Buy Logic**: Places buy orders when market conditions meet the VWAP-based threshold and buy limit.
    - **Sell Logic**: After buy limits are reached, switches to sell mode and places sell orders based on the sell threshold.
3. **Order Management**: Ensures that all buy orders are canceled before switching to sell mode.

## Market Data Simulation
To test the algorithm, market data can be simulated through the `createTick()` and `createTick2()` methods in the test suite, which provide sample bid and ask levels for testing. These methods help mimic real-time market movements to validate the algorithm's responses and decisions.

## Setup and Usage
1. **Prerequisites**:
    - Ensure a Java environment is set up.
    - Import this codebase into your IDE or build environment.
2. **Running the Algorithm**:
    - Run the `evaluate` method with `SimpleAlgoState` inputs to simulate trading decisions based on market data.
3. **Configurations**:
    - Modify constants (e.g., `maxOrders`, `quantity`, `VWAP_BUY_THRESHOLD`, `VWAP_SELL_THRESHOLD`) to adjust the algorithm’s behavior.

## VWAP Calculation Details
- **Initialisation**: VWAP is set as the midpoint between the highest bid and lowest ask on the first trade.
- **Continuous Update**: VWAP is recalculated dynamically based on the volume-weighted average of the top bid and ask levels, ensuring decisions are based on current market data.

## Logging and Debugging
- Extensive logging throughout the algorithm tracks all major actions, decisions, and thresholds, providing detailed insights into the algorithm's flow.

## Future Enhancements
- **Dynamic Threshold Adjustment**: Implementing a mechanism to adjust thresholds based on market volatility.
- **Historical VWAP Tracking**: Adding historical VWAP tracking could further refine buy and sell decisions.
## Testing
This algorithm includes a suite of unit tests to verify its behavior under various market conditions. Tests are designed to:
- Validate order creation, fill rates, and cancellation logic.
- Ensure the algorithm respects VWAP thresholds for buy/sell conditions.
- Confirm correct profit calculation based on buy and sell actions.

### Running Tests
Run the test suite using:
```bash
mvn test


--- 

This text can be saved as a `README.md` file to provide detailed documentation for the project.

