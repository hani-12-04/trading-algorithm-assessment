package codingblackfemales.algo;

import codingblackfemales.action.Action; // represing an action such as creating, cancelling or not action
import codingblackfemales.sotw.SimpleAlgoState;


public interface AlgoLogic {
    Action evaluate(final SimpleAlgoState state); // defining a single method(evaluate) in the AlgoLogic interface.
}

// AlgoLogic Interfce: This is the blueprint that any algo needs to follow. This makes sure any class
// implementing this interface has a method called evalate that takes a SimpleAlgoState and returns an action.

// An interface allows for different implementations of the evaluate method.