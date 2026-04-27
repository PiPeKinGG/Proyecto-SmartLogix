package com.smartlogix.shipping.controller;

import com.smartlogix.shipping.event.OrderConfirmedEvent;
import com.smartlogix.shipping.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventListener {
    @Autowired
    private ShippingService shippingService;

    @KafkaListener(topics = "order-confirmed", groupId = "shipping-service", containerFactory = "orderConfirmedEventKafkaListenerContainerFactory")
    public void listenOrderConfirmed(OrderConfirmedEvent event) {
        shippingService.handleOrderConfirmed(event);
    }
}
