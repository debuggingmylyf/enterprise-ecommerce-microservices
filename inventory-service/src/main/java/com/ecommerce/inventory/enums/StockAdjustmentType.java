package com.ecommerce.inventory.enums;

/**
 * Defines the direction of a manual stock adjustment.
 *
 * <ul>
 *   <li>{@link #INCREASE} – adds stock (e.g. goods received, inventory correction upward)</li>
 *   <li>{@link #DECREASE} – removes stock (e.g. damaged goods, shrinkage, correction downward)</li>
 * </ul>
 */
public enum StockAdjustmentType {

    /** Increase available stock by the specified quantity. */
    INCREASE,

    /** Decrease available stock by the specified quantity. */
    DECREASE
}
