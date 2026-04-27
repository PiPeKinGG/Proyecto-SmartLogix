package com.smartlogix.shipping.service;

public class ShippingStrategyFactory {
    public static ShippingStrategy getStrategy(String shippingType) {
        if ("EXPRESS".equalsIgnoreCase(shippingType)) {
            return new ExpressShippingStrategy();
        } else {
            return new StandardShippingStrategy();
        }
    }
}
