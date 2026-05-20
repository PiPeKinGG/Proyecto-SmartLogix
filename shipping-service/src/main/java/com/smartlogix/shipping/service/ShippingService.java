package com.smartlogix.shipping.service;

import com.smartlogix.shipping.dto.ShipmentResponse;
import com.smartlogix.shipping.entity.Shipment;
import com.smartlogix.shipping.event.OrderConfirmedEvent;
import com.smartlogix.shipping.event.ShipmentDispatchedEvent;
import com.smartlogix.shipping.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ShippingService {
    private static final String SHIPPING_STANDARD = "STANDARD";
    private static final String SHIPPING_EXPRESS = "EXPRESS";
    private static final String STATUS_DISPATCHED = "DISPATCHED";

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private KafkaTemplate<String, ShipmentDispatchedEvent> kafkaTemplate;

    @Transactional
    public Shipment handleOrderConfirmed(OrderConfirmedEvent event) {
        validateOrderConfirmedEvent(event);
        return shipmentRepository.findByOrderIdAndPymeId(event.getOrderId(), event.getPymeId())
                .orElseGet(() -> createShipment(event));
    }

    public List<ShipmentResponse> getShipmentsByPyme(Long pymeId) {
        return shipmentRepository.findAllByPymeId(pymeId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ShipmentResponse getShipmentByTrackingId(String trackingId, Long pymeId) {
        return shipmentRepository.findByTrackingId(trackingId)
                .filter(shipment -> shipment.getPymeId().equals(pymeId))
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Envio no encontrado o no pertenece a la pyme"));
    }

    public ShipmentResponse getShipmentByOrderId(Long orderId, Long pymeId) {
        return shipmentRepository.findByOrderIdAndPymeId(orderId, pymeId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Envio no encontrado o no pertenece a la pyme"));
    }

    private Shipment createShipment(OrderConfirmedEvent event) {
        String shippingType = normalizeShippingType(event.getShippingType());
        ShippingStrategy strategy = ShippingStrategyFactory.getStrategy(shippingType);
        double cost = strategy.calculateCost();
        int estimatedDays = strategy.calculateEstimatedDays();
        String trackingId = UUID.randomUUID().toString();

        Shipment shipment = new Shipment();
        shipment.setOrderId(event.getOrderId());
        shipment.setPymeId(event.getPymeId());
        shipment.setUserId(event.getUserId());
        shipment.setShippingType(shippingType);
        shipment.setCost(cost);
        shipment.setEstimatedDays(estimatedDays);
        shipment.setTrackingId(trackingId);
        Shipment savedShipment = shipmentRepository.save(shipment);

        ShipmentDispatchedEvent dispatchedEvent = new ShipmentDispatchedEvent(
                event.getOrderId(), event.getPymeId(), event.getUserId(), trackingId, shippingType
        );
        kafkaTemplate.send("shipment-dispatched", dispatchedEvent);
        return savedShipment;
    }

    private void validateOrderConfirmedEvent(OrderConfirmedEvent event) {
        if (event == null || event.getOrderId() == null || event.getPymeId() == null || event.getUserId() == null) {
            throw new IllegalArgumentException("El evento de pedido confirmado esta incompleto");
        }
    }

    private String normalizeShippingType(String shippingType) {
        if (shippingType == null || shippingType.isBlank()) {
            return SHIPPING_STANDARD;
        }

        String normalized = shippingType.trim().toUpperCase();
        if (!SHIPPING_STANDARD.equals(normalized) && !SHIPPING_EXPRESS.equals(normalized)) {
            throw new IllegalArgumentException("El tipo de envio debe ser Estandar o Expres");
        }
        return normalized;
    }

    private ShipmentResponse toResponse(Shipment shipment) {
        ShipmentResponse response = new ShipmentResponse();
        response.setId(shipment.getId());
        response.setOrderId(shipment.getOrderId());
        response.setPymeId(shipment.getPymeId());
        response.setUserId(shipment.getUserId());
        response.setShippingType(toSpanishShippingType(shipment.getShippingType()));
        response.setCost(shipment.getCost());
        response.setEstimatedDays(shipment.getEstimatedDays());
        response.setTrackingId(shipment.getTrackingId());
        response.setStatus(toSpanishStatus(shipment.getStatus()));
        response.setCreatedAt(shipment.getCreatedAt());
        return response;
    }

    private String toSpanishStatus(String status) {
        if (STATUS_DISPATCHED.equals(status)) {
            return "Despachado";
        }
        return status;
    }

    private String toSpanishShippingType(String shippingType) {
        if (SHIPPING_STANDARD.equals(shippingType)) {
            return "Estandar";
        }
        if (SHIPPING_EXPRESS.equals(shippingType)) {
            return "Expres";
        }
        return shippingType;
    }
}
