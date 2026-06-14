package com.smartlogix.shipping.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentEntityTest {

    @Test
    void prePersistSetsDefaultsWhenNullOrBlank() {
        Shipment shipment = new Shipment();
        shipment.setOrderId(1L);
        shipment.setPymeId(100L);
        shipment.setUserId(7L);
        shipment.setShippingType("STANDARD");
        shipment.setCost(5.0);
        shipment.setEstimatedDays(3);
        shipment.setTrackingId("TRACK-123");

        // initially null status and createdAt
        shipment.setStatus(null);
        shipment.setCreatedAt(null);

        shipment.prePersist();

        assertNotNull(shipment.getStatus());
        assertEquals("DISPATCHED", shipment.getStatus());
        assertNotNull(shipment.getCreatedAt());
        assertTrue(shipment.getCreatedAt() instanceof LocalDateTime);
    }

    @Test
    void prePersistDoesNotOverrideExistingValues() {
        Shipment shipment = new Shipment();
        shipment.setOrderId(1L);
        shipment.setPymeId(100L);
        shipment.setUserId(7L);
        shipment.setShippingType("EXPRESS");
        shipment.setCost(10.0);
        shipment.setEstimatedDays(1);
        shipment.setTrackingId("TRACK-XYZ");

        shipment.setStatus("PENDING");
        LocalDateTime now = LocalDateTime.of(2020,1,1,0,0);
        shipment.setCreatedAt(now);

        shipment.prePersist();

        assertEquals("PENDING", shipment.getStatus());
        assertEquals(now, shipment.getCreatedAt());
    }
}
