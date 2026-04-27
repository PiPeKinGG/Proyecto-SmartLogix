package com.smartlogix.shipping.service;

public class StandardShippingStrategy implements ShippingStrategy {
    @Override
    public double calculateCost() {
        return 5.0; // ejemplo fijo
    }
    @Override
    public int calculateEstimatedDays() {
        return 3;
    }
}
