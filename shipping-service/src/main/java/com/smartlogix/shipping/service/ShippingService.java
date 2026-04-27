package com.smartlogix.shipping.service;

import com.smartlogix.shipping.entity.Shipment;
import com.smartlogix.shipping.event.OrderConfirmedEvent;
import com.smartlogix.shipping.event.ShipmentDispatchedEvent;
import com.smartlogix.shipping.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ShippingService {
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private KafkaTemplate<String, ShipmentDispatchedEvent> kafkaTemplate;

    @Transactional
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(event.getShippingType());
        double cost = strategy.calculateCost();
        int estimatedDays = strategy.calculateEstimatedDays();
        String trackingId = UUID.randomUUID().toString();

        Shipment shipment = new Shipment();
        shipment.setOrderId(event.getOrderId());
        shipment.setPymeId(event.getPymeId());
        shipment.setUserId(event.getUserId());
        shipment.setShippingType(event.getShippingType());
        shipment.setCost(cost);
        shipment.setEstimatedDays(estimatedDays);
        shipment.setTrackingId(trackingId);
        shipmentRepository.save(shipment);

        ShipmentDispatchedEvent dispatchedEvent = new ShipmentDispatchedEvent(
                event.getOrderId(), event.getPymeId(), event.getUserId(), trackingId, event.getShippingType()
        );
        kafkaTemplate.send("shipment-dispatched", dispatchedEvent);
    }
}
