package com.smartlogix.shipping.service;

public interface ShippingStrategy {
    double calculateCost();
    int calculateEstimatedDays();
}
