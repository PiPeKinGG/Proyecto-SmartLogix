package com.smartlogix.order.service;

import com.smartlogix.order.dto.OrderRequest;
import com.smartlogix.order.dto.OrderResponse;
import com.smartlogix.order.entity.Order;
import com.smartlogix.order.entity.OrderItem;
import com.smartlogix.order.event.OrderConfirmedEvent;
import com.smartlogix.order.feign.InventoryClient;
import com.smartlogix.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InventoryClient inventoryClient;
    @Autowired
    private KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setPymeId(request.getPymeId());
        order.setUserId(request.getUserId());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        List<OrderItem> items = new ArrayList<>();
        boolean allReserved = true;
        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            String reserveResult = inventoryClient.reserveStock(itemReq.getProductId(), itemReq.getQuantity(), request.getPymeId());
            if (!"Stock reservado".equals(reserveResult)) {
                allReserved = false;
                break;
            }
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            items.add(item);
        }
        order.setItems(items);
        if (allReserved) {
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
            // Confirm reservation in inventory
            for (OrderItem item : items) {
                inventoryClient.confirmReservation(item.getProductId(), item.getQuantity(), request.getPymeId());
            }
            // Publish event
            OrderConfirmedEvent event = new OrderConfirmedEvent(order.getId(), order.getPymeId(), order.getUserId());
            kafkaTemplate.send("order-confirmed", event);
        } else {
            order.setStatus("CANCELLED_NO_STOCK");
            orderRepository.save(order);
            // Cancel reservation in inventory
            for (OrderItem item : items) {
                inventoryClient.cancelReservation(item.getProductId(), item.getQuantity(), request.getPymeId());
            }
        }
        // Build response
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());
        List<OrderResponse.OrderItemDto> itemDtos = new ArrayList<>();
        for (OrderItem item : items) {
            OrderResponse.OrderItemDto dto = new OrderResponse.OrderItemDto();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            itemDtos.add(dto);
        }
        response.setItems(itemDtos);
        return response;
    }

    public List<Order> getOrdersByPyme(Long pymeId) {
        return orderRepository.findAllByPymeId(pymeId);
    }
}
