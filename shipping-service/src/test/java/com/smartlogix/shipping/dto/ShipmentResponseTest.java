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
}
