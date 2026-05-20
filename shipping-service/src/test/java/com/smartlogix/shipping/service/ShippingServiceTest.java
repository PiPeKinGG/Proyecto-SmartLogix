package com.smartlogix.shipping.service;

import com.smartlogix.shipping.dto.ShipmentResponse;
import com.smartlogix.shipping.entity.Shipment;
import com.smartlogix.shipping.event.OrderConfirmedEvent;
import com.smartlogix.shipping.event.ShipmentDispatchedEvent;
import com.smartlogix.shipping.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private KafkaTemplate<String, ShipmentDispatchedEvent> kafkaTemplate;

    @InjectMocks
    private ShippingService shippingService;

    private OrderConfirmedEvent orderConfirmedEvent;

    @BeforeEach
    void setUp() {
        orderConfirmedEvent = new OrderConfirmedEvent(1L, 100L, 7L, "express");
    }

    @Test
    void testHandleOrderConfirmed_CreatesExpressShipmentAndPublishesEvent() {
        // Given
        when(shipmentRepository.findByOrderIdAndPymeId(1L, 100L)).thenReturn(Optional.empty());
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> {
            Shipment shipment = invocation.getArgument(0);
            shipment.setId(10L);
            return shipment;
        });

        // When
        Shipment shipment = shippingService.handleOrderConfirmed(orderConfirmedEvent);

        // Then
        assertNotNull(shipment);
        assertEquals(10L, shipment.getId());
        assertEquals(1L, shipment.getOrderId());
        assertEquals(100L, shipment.getPymeId());
        assertEquals(7L, shipment.getUserId());
        assertEquals("EXPRESS", shipment.getShippingType());
        assertEquals(10.0, shipment.getCost());
        assertEquals(1, shipment.getEstimatedDays());
        assertNotNull(shipment.getTrackingId());
        assertFalse(shipment.getTrackingId().isBlank());

        ArgumentCaptor<ShipmentDispatchedEvent> eventCaptor = ArgumentCaptor.forClass(ShipmentDispatchedEvent.class);
        verify(kafkaTemplate).send(eq("shipment-dispatched"), eventCaptor.capture());
        assertEquals(1L, eventCaptor.getValue().getOrderId());
        assertEquals(100L, eventCaptor.getValue().getPymeId());
        assertEquals(7L, eventCaptor.getValue().getUserId());
        assertEquals(shipment.getTrackingId(), eventCaptor.getValue().getTrackingId());
        assertEquals("EXPRESS", eventCaptor.getValue().getShippingType());
    }

    @Test
    void testHandleOrderConfirmed_ReturnsExistingShipmentWithoutDuplicating() {
        // Given
        Shipment existingShipment = shipment(10L, 1L, 100L, "STANDARD", "TRACK-1");
        when(shipmentRepository.findByOrderIdAndPymeId(1L, 100L)).thenReturn(Optional.of(existingShipment));

        // When
        Shipment shipment = shippingService.handleOrderConfirmed(orderConfirmedEvent);

        // Then
        assertSame(existingShipment, shipment);
        verify(shipmentRepository, never()).save(any(Shipment.class));
        verify(kafkaTemplate, never()).send(eq("shipment-dispatched"), any(ShipmentDispatchedEvent.class));
    }

    @Test
    void testGetShipmentsByPyme_MapsUserFriendlyValues() {
        // Given
        Shipment shipment = shipment(10L, 1L, 100L, "STANDARD", "TRACK-1");
        shipment.setStatus("DISPATCHED");
        when(shipmentRepository.findAllByPymeId(100L)).thenReturn(List.of(shipment));

        // When
        List<ShipmentResponse> responses = shippingService.getShipmentsByPyme(100L);

        // Then
        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
        assertEquals("Estandar", responses.get(0).getShippingType());
        assertEquals("Despachado", responses.get(0).getStatus());
        assertEquals("TRACK-1", responses.get(0).getTrackingId());
    }

    @Test
    void testGetShipmentByTrackingId_Success() {
        // Given
        Shipment shipment = shipment(10L, 1L, 100L, "EXPRESS", "TRACK-1");
        when(shipmentRepository.findByTrackingId("TRACK-1")).thenReturn(Optional.of(shipment));

        // When
        ShipmentResponse response = shippingService.getShipmentByTrackingId("TRACK-1", 100L);

        // Then
        assertEquals(10L, response.getId());
        assertEquals("Expres", response.getShippingType());
        assertEquals("TRACK-1", response.getTrackingId());
    }

    @Test
    void testGetShipmentByTrackingId_PymeMismatch() {
        // Given
        Shipment shipment = shipment(10L, 1L, 200L, "STANDARD", "TRACK-1");
        when(shipmentRepository.findByTrackingId("TRACK-1")).thenReturn(Optional.of(shipment));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> shippingService.getShipmentByTrackingId("TRACK-1", 100L)
        );
        assertEquals("Envio no encontrado o no pertenece a la pyme", exception.getMessage());
    }

    @Test
    void testGetShipmentByOrderId_NotFound() {
        // Given
        when(shipmentRepository.findByOrderIdAndPymeId(99L, 100L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> shippingService.getShipmentByOrderId(99L, 100L)
        );
        assertEquals("Envio no encontrado o no pertenece a la pyme", exception.getMessage());
    }

    @Test
    void testHandleOrderConfirmed_InvalidEvent() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> shippingService.handleOrderConfirmed(new OrderConfirmedEvent(null, 100L, 7L, "STANDARD"))
        );
        assertEquals("El evento de pedido confirmado esta incompleto", exception.getMessage());
        verifyNoInteractions(shipmentRepository, kafkaTemplate);
    }

    @Test
    void testHandleOrderConfirmed_InvalidShippingType() {
        // Given
        orderConfirmedEvent.setShippingType("OVERNIGHT");
        when(shipmentRepository.findByOrderIdAndPymeId(1L, 100L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> shippingService.handleOrderConfirmed(orderConfirmedEvent)
        );
        assertEquals("El tipo de envio debe ser Estandar o Expres", exception.getMessage());
        verify(shipmentRepository, never()).save(any(Shipment.class));
        verify(kafkaTemplate, never()).send(eq("shipment-dispatched"), any(ShipmentDispatchedEvent.class));
    }

    private Shipment shipment(Long id, Long orderId, Long pymeId, String shippingType, String trackingId) {
        Shipment shipment = new Shipment();
        shipment.setId(id);
        shipment.setOrderId(orderId);
        shipment.setPymeId(pymeId);
        shipment.setUserId(7L);
        shipment.setShippingType(shippingType);
        shipment.setCost("EXPRESS".equals(shippingType) ? 10.0 : 5.0);
        shipment.setEstimatedDays("EXPRESS".equals(shippingType) ? 1 : 3);
        shipment.setTrackingId(trackingId);
        shipment.setStatus("DISPATCHED");
        shipment.setCreatedAt(LocalDateTime.now());
        return shipment;
    }
}
