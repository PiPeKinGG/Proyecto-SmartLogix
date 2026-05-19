package com.smartlogix.shipping.controller;

import com.smartlogix.shipping.dto.ShipmentResponse;
import com.smartlogix.shipping.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private ShippingService shippingService;

    @GetMapping
    public List<ShipmentResponse> getShipments(@RequestHeader("pyme_id") Long pymeId) {
        return shippingService.getShipmentsByPyme(pymeId);
    }

    @GetMapping("/tracking/{trackingId}")
    public ResponseEntity<ShipmentResponse> getByTrackingId(@PathVariable String trackingId, @RequestHeader("pyme_id") Long pymeId) {
        return ResponseEntity.ok(shippingService.getShipmentByTrackingId(trackingId, pymeId));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ShipmentResponse> getByOrderId(@PathVariable Long orderId, @RequestHeader("pyme_id") Long pymeId) {
        return ResponseEntity.ok(shippingService.getShipmentByOrderId(orderId, pymeId));
    }
}
