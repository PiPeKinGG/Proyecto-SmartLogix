package com.smartlogix.order.repository;

import com.smartlogix.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {
    
    @Autowired
    private OrderRepository orderRepository;

    // Método auxiliar para crear una orden válida con todos los campos obligatorios
    private Order createValidOrder() {
        Order order = new Order();
        order.setCustomerEmail("cliente@test.com");
        order.setCustomerName("Cliente Test");
        order.setCustomerRut("11111111-1");
        order.setPymeId(100L);
        order.setShippingAddress("Direccion 123");
        order.setShippingType("STANDARD");
        order.setStatus("PENDING");
        order.setTotalAmount(15000.0);
        order.setUserId(7L);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    @Test
    void testSaveAndFindById() {
        Order order = createValidOrder();
        Order savedOrder = orderRepository.save(order);
        
        Order found = orderRepository.findById(savedOrder.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(savedOrder.getId(), found.getId());
    }

    @Test
    void testFindAll() {
        Order order = createValidOrder();
        orderRepository.save(order);
        
        List<Order> orders = orderRepository.findAll();
        assertFalse(orders.isEmpty());
    }
}