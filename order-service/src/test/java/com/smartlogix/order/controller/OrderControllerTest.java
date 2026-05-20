package com.smartlogix.order.controller;

import com.smartlogix.order.dto.OrderDto;
import com.smartlogix.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

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
    void testGetAllOrders() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        when(orderService.getAllOrders()).thenReturn(List.of(orderDto));

        ResponseEntity<List<OrderDto>> response = orderController.getAllOrders();
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }
}
