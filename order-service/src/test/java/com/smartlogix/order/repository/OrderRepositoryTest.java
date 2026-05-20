package com.smartlogix.order.repository;

import com.smartlogix.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testSaveAndFindById() {
        Order order = new Order();
        order.setId(1L);
        orderRepository.save(order);
        Order found = orderRepository.findById(1L).orElse(null);
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testFindAll() {
        Order order = new Order();
        order.setId(2L);
        orderRepository.save(order);
        List<Order> orders = orderRepository.findAll();
        assertFalse(orders.isEmpty());
    }
}
