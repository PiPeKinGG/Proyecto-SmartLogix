package com.smartlogix.order.controller;

import com.smartlogix.order.dto.OrderRequest;
import com.smartlogix.order.dto.OrderResponse;
import com.smartlogix.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request, @RequestHeader("pyme_id") Long pymeId, @RequestHeader("userId") Long userId) {
        request.setPymeId(pymeId);
        request.setUserId(userId);
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestHeader("pyme_id") Long pymeId) {
        return ResponseEntity.ok(orderService.getOrdersByPyme(pymeId));
    }
    
    @PostMapping("/delivered")
    public ResponseEntity<String> updateOrderStatusToDelivered(@RequestParam Long orderId, @RequestHeader("pyme_id") Long pymeId) {
        orderService.updateOrderStatusToDelivered(orderId, pymeId);
        return ResponseEntity.ok("Pedido marcado como Entregado");
    }
}