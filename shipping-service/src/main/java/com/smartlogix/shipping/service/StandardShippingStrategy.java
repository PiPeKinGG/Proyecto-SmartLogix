package com.smartlogix.shipping.service;

public class StandardShippingStrategy implements ShippingStrategy {
    @Override
    public double calculateCost() {
        return 5.0; 
    }
    @Override
    public int calculateEstimatedDays() {
        return 3;
    }
}
