import React, { useEffect, useState } from 'react'; // importing react library
// UseState hook allows component to remember values between renders (current price or prev price)
// UseEffect hook helps perform actions after a component has rendered (certain values have changed)

interface PriceCellProps {
  price: number;
}

// PriceCell component will display the price in the table, with a logic implemented to show if the price
// is going up or down compared to the previous value.

// PriceCell will:
// track the price
// compare prices
// show a direction arrow:
// update the previous price:

// STEPS:
// import the needed hooks and libraries
// need to define the structure for my price component props
// setting up a logic