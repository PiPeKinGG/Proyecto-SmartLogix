package com.smartlogix.order.controller;

import com.smartlogix.order.dto.OrderRequest;
import com.smartlogix.order.dto.OrderResponse;
import com.smartlogix.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request, @RequestHeader("pyme_id") Long pymeId, @RequestHeader("userId") Long userId) {
        request.setPymeId(pymeId);
        request.setUserId(userId);
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<OrderResponse> getOrders(@RequestHeader("pyme_id") Long pymeId) {
        return orderService.getOrdersByPyme(pymeId);
    }
    @PostMapping("/delivered")
    public ResponseEntity<String> updateOrderStatusToDelivered(@RequestParam Long orderId, @RequestHeader("pyme_id") Long pymeId) {
        orderService.updateOrderStatusToDelivered(orderId, pymeId);
        return ResponseEntity.ok("Pedido marcado como Entregado");
    }
}
