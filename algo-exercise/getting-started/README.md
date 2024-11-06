# MyAlgoLogic Trading Algorithm

## Overview

`MyAlgoLogic` is a trading algorithm designed to interact with a simulated market environment, making decisions to place buy and sell orders based on defined constraints. It manages an order book, handles market data changes, and dynamically updates based on predefined price limits and order management rules.

This repository includes:
- **Algorithm Logic** (`MyAlgoLogic`): Core trading logic with buy, sell, and cancel strategies.
- **Unit Tests** (`MyAlgoTest`): Isolated tests that simulate tick data to verify the algorithm's buy/sell and cancel logic.
- **Back Test** (`MyAlgoBackTest`): End-to-end backtesting that integrates with a mock market environment to validate overall behavior, including order fills and profit calculation.

## Project Structure

- **MyAlgoLogic.java**: Core algorithm logic implementing buy, sell, and cancel strategies.
- **MyAlgoTest.java**: Unit tests for isolated testing of the algorithm's behavior under controlled tick data.
- **MyAlgoBackTest.java**: Comprehensive backtest, simulating a live trading environment with order fills and profit evaluation.
- **AbstractAlgoTest.java** and **AbstractAlgoBackTest.java**: Base classes providing test infrastructure, including market simulation, tick data, and sequencing.

## Algorithm Features

### Key Logic
- **Buy Logic**: Places buy orders if the current market ask price is within a defined `priceLimit`.
- **Sell Logic**: Places sell orders if the market bid price exceeds a predefined `sellLimit`.
- **Cancel Logic**: Cancels active orders incrementally if the `clearActiveOrders` flag is enabled or `maxOrders` limit is reached.

### Constraints and Parameters
- **Price Limits**: Defines `priceLimit` for buy orders and `sellLimit` for sell orders.
- **Order Management**: Limits active orders to `maxOrders` and manages incremental cancellations when needed.
- **Quantity Per Order**: Each order is placed with a fixed quantity (`quantity`).

## Testing Overview

### Unit Tests (`MyAlgoTest`)
Tests the algorithm’s logic in isolation, ensuring:
- **Buy Logic Test**: Verifies buy orders are only placed when conditions match `priceLimit`.

### Backtests (`MyAlgoBackTest`)
Simulates the algorithm in a live market environment, validating:
- **Order Fills**: Checks if expected quantities are filled.
- **Order Status**: Confirms correct categorisation of partially and fully filled orders.
- **Profit Calculation**: Evaluates profit and verifies non-negative outcomes.

## Getting Started

1. **Run Unit Tests**:
   ```bash
   mvn test -Dtest=MyAlgoTest
2. **Run Back Tests**:
   ```bash
   mvn test -Dtest=MyAlgoBackTest
   
Happy Testing!

