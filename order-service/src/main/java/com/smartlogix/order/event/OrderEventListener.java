package com.smartlogix.order.event;

import com.smartlogix.order.entity.Order;
import com.smartlogix.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate;

    @KafkaListener(topics = "inventory-reserved-success", groupId = "order-service")
    @Transactional
    public void handleInventoryReservedSuccess(InventoryReservedSuccessEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .filter(o -> o.getPymeId().equals(event.getPymeId()))
                .orElseThrow(() -> new RuntimeException("Orden no encontrada o pymeId no coincide"));
        if ("CONFIRMED".equals(order.getStatus())) {
            return;
        }
        order.setStatus("CONFIRMED");
        orderRepository.save(order);
        OrderConfirmedEvent confirmedEvent = new OrderConfirmedEvent(
                order.getId(),
                order.getPymeId(),
                order.getUserId(),
                order.getShippingType()
        );
        kafkaTemplate.send("order-confirmed", confirmedEvent);
    }

    @KafkaListener(topics = "inventory-reserved-failed", groupId = "order-service")
    @Transactional
    public void handleInventoryReservedFailed(InventoryReservedFailedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .filter(o -> o.getPymeId().equals(event.getPymeId()))
                .orElseThrow(() -> new RuntimeException("Orden no encontrada o pymeId no coincide"));
        order.setStatus("CANCELLED_NO_STOCK");
        orderRepository.save(order);
    }
}