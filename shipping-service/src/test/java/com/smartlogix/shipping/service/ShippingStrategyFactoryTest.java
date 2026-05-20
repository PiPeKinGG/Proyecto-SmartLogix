package com.smartlogix.shipping.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ShippingStrategyFactoryTest {

    @Test
    void testGetStrategy_Express() {
        // When
        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy("EXPRESS");

        // Then
        assertInstanceOf(ExpressShippingStrategy.class, strategy);
        assertEquals(10.0, strategy.calculateCost());
        assertEquals(1, strategy.calculateEstimatedDays());
    }

    @Test
    void testGetStrategy_ExpressCaseInsensitive() {
        // When
        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy("express");

        // Then
        assertInstanceOf(ExpressShippingStrategy.class, strategy);
    }

    @Test
    void testGetStrategy_StandardByDefault() {
        // When
        ShippingStrategy strategyUnknown = ShippingStrategyFactory.getStrategy("UNKNOWN");
        ShippingStrategy strategyNull = ShippingStrategyFactory.getStrategy(null);

        // Then
        assertInstanceOf(StandardShippingStrategy.class, strategyUnknown);
        assertInstanceOf(StandardShippingStrategy.class, strategyNull);
        assertEquals(5.0, strategyUnknown.calculateCost());
        assertEquals(3, strategyUnknown.calculateEstimatedDays());
    }
}
