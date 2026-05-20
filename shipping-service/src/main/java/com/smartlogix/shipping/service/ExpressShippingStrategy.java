package com.smartlogix.shipping.service;

public class ExpressShippingStrategy implements ShippingStrategy {
    @Override
    public double calculateCost() {
        return 10.0; 
    }
    @Override
    public int calculateEstimatedDays() {
        return 1;
    }
}
