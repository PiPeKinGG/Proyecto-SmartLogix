package com.smartlogix.shipping.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShipmentResponseTest {

    @Test
    void lombokGeneratedGettersAndSettersWork() {
        ShipmentResponse resp = new ShipmentResponse();
        resp.setId(1L);
        resp.setOrderId(2L);
        resp.setPymeId(100L);
        resp.setUserId(7L);
        resp.setShippingType("EXPRESS");
        resp.setCost(10.0);
        resp.setEstimatedDays(1);
        resp.setTrackingId("TRACK-1");
        resp.setStatus("DISPATCHED");
        resp.setCreatedAt(LocalDateTime.now());

        assertEquals(1L, resp.getId());
        assertEquals(2L, resp.getOrderId());
        assertEquals(100L, resp.getPymeId());
        assertEquals(7L, resp.getUserId());
        assertEquals("EXPRESS", resp.getShippingType());
        assertEquals(10.0, resp.getCost());
        assertEquals(1, resp.getEstimatedDays());
        assertEquals("TRACK-1", resp.getTrackingId());
        assertEquals("DISPATCHED", resp.getStatus());
        assertNotNull(resp.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        ShipmentResponse resp1 = new ShipmentResponse();
        resp1.setId(1L);
        resp1.setOrderId(2L);
        resp1.setPymeId(100L);
        resp1.setUserId(7L);
        resp1.setShippingType("EXPRESS");
        resp1.setCost(10.0);
        resp1.setEstimatedDays(1);
        resp1.setTrackingId("TRACK-1");
        resp1.setStatus("DISPATCHED");
        resp1.setCreatedAt(now);

        ShipmentResponse resp2 = new ShipmentResponse();
        resp2.setId(1L);
        resp2.setOrderId(2L);
        resp2.setPymeId(100L);
        resp2.setUserId(7L);
        resp2.setShippingType("EXPRESS");
        resp2.setCost(10.0);
        resp2.setEstimatedDays(1);
        resp2.setTrackingId("TRACK-1");
        resp2.setStatus("DISPATCHED");
        resp2.setCreatedAt(now);

        ShipmentResponse resp3 = new ShipmentResponse();
        resp3.setId(999L);

        assertEquals(resp1, resp2);
        assertEquals(resp1.hashCode(), resp2.hashCode());
        assertNotEquals(resp1, resp3);
        assertNotEquals(resp1, null);
        assertNotEquals(resp1, new Object());
    }

    @Test
    void testToString() {
        ShipmentResponse resp = new ShipmentResponse();
        resp.setTrackingId("TRACK-1");
        resp.setStatus("DISPATCHED");

        assertNotNull(resp.toString());
        assertTrue(resp.toString().contains("TRACK-1"));
    }
}