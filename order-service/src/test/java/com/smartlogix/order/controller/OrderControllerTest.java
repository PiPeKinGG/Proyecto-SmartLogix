package com.smartlogix.order.controller;

import com.smartlogix.order.dto.OrderResponse;
import com.smartlogix.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void testGetOrders() {
        Long pymeId = 1L;
        OrderResponse orderResponse = new OrderResponse();
        when(orderService.getOrdersByPyme(pymeId)).thenReturn(List.of(orderResponse));
        List<OrderResponse> response = orderController.getOrders(pymeId);
        assertNotNull(response);
        assertEquals(1, response.size());
    }
}
