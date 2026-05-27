package com.smartlogix.order.service;

import com.smartlogix.order.dto.OrderRequest;
import com.smartlogix.order.dto.OrderResponse;
import com.smartlogix.order.entity.Order;
import com.smartlogix.order.entity.OrderItem;
import com.smartlogix.order.event.OrderConfirmedEvent;
import com.smartlogix.order.feign.InventoryClient;
import com.smartlogix.order.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_CANCELLED_NO_STOCK = "CANCELLED_NO_STOCK";
    private static final String STATUS_DELIVERED = "DELIVERED";

    private static final String SHIPPING_STANDARD = "STANDARD";
    private static final String SHIPPING_EXPRESS = "EXPRESS";
    private static final String SHIPPING_STANDARD_ES = "ESTANDAR";
    private static final String SHIPPING_EXPRESS_ES = "EXPRES";

    private static final String USER_STATUS_PENDING = "Pendiente";
    private static final String USER_STATUS_CONFIRMED = "Confirmado";
    private static final String USER_STATUS_CANCELLED_NO_STOCK = "Cancelado por falta de stock";
    private static final String USER_STATUS_DELIVERED = "Entregado";
    private static final String USER_SHIPPING_STANDARD = "Estandar";
    private static final String USER_SHIPPING_EXPRESS = "Expres";

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderConfirmedEvent> orderConfirmedKafkaTemplate;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = createPendingOrder(request);
        List<OrderItem> reservedItems = new ArrayList<>();

        try {
            reserveInventory(order, reservedItems);
            confirmOrder(order);
            publishOrderConfirmed(order);
        } catch (FeignException ex) {
            compensateInventory(reservedItems, order.getPymeId());
            cancelOrderNoStock(order);
        }

        return toResponse(order);
    }

    @Transactional
    public void updateOrderStatusToDelivered(Long orderId, Long pymeId) {
        Order order = findOrderByIdAndPyme(orderId, pymeId);
        order.setStatus(STATUS_DELIVERED);
        orderRepository.save(order);
    }

    public List<OrderResponse> getOrdersByPyme(Long pymeId) {
        return orderRepository.findAllByPymeIdOrderByCreatedAtDesc(pymeId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Order createPendingOrder(OrderRequest request) {
        Order order = new Order();
        order.setPymeId(request.getPymeId());
        order.setUserId(request.getUserId());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerRut(request.getCustomerRut());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingType(normalizeShippingType(request.getShippingType()));
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(STATUS_PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(buildOrderItems(request, order));

        return orderRepository.saveAndFlush(order);
    }

    private List<OrderItem> buildOrderItems(OrderRequest request, Order order) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }
        return items;
    }

    private void reserveInventory(Order order, List<OrderItem> reservedItems) {
        for (OrderItem item : order.getItems()) {
            inventoryClient.reserveStock(item.getProductId(), item.getQuantity(), order.getPymeId());
            reservedItems.add(item);
        }
    }

    private void confirmOrder(Order order) {
        order.setStatus(STATUS_CONFIRMED);
        orderRepository.save(order);
    }

    private void cancelOrderNoStock(Order order) {
        order.setStatus(STATUS_CANCELLED_NO_STOCK);
        orderRepository.save(order);
    }

    private void compensateInventory(List<OrderItem> reservedItems, Long pymeId) {
        for (OrderItem item : reservedItems) {
            try {
                inventoryClient.cancelReservation(item.getProductId(), item.getQuantity(), pymeId);
            } catch (FeignException ignored) {
                // La orden queda cancelada; la reconciliacion de stock puede reintentarse operacionalmente.
            }
        }
    }

    private void publishOrderConfirmed(Order order) {
        OrderConfirmedEvent event = new OrderConfirmedEvent(
                order.getId(),
                order.getPymeId(),
                order.getUserId(),
                order.getShippingType()
        );
        orderConfirmedKafkaTemplate.send("order-confirmed", event);
    }

    private Order findOrderByIdAndPyme(Long orderId, Long pymeId) {
        return orderRepository.findByIdAndPymeId(orderId, pymeId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado o no pertenece a la pyme"));
    }

    private String normalizeShippingType(String shippingType) {
        if (shippingType == null || shippingType.isBlank()) {
            return SHIPPING_STANDARD;
        }

        String normalized = shippingType.trim().toUpperCase();
        if (SHIPPING_STANDARD_ES.equals(normalized)) {
            return SHIPPING_STANDARD;
        }
        if (SHIPPING_EXPRESS_ES.equals(normalized)) {
            return SHIPPING_EXPRESS;
        }
        if (SHIPPING_STANDARD.equals(normalized) || SHIPPING_EXPRESS.equals(normalized)) {
            return normalized;
        }

        throw new IllegalArgumentException("El tipo de envio debe ser Estandar o Expres");
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderId(order.getId());
        response.setPymeId(order.getPymeId());
        response.setUserId(order.getUserId());
        response.setStatus(toSpanishStatus(order.getStatus()));
        response.setCustomerName(order.getCustomerName());
        response.setCustomerRut(order.getCustomerRut());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setShippingAddress(order.getShippingAddress());
        response.setShippingType(toSpanishShippingType(order.getShippingType()));
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(toItemResponses(order.getItems()));
        return response;
    }

    private List<OrderResponse.OrderItemDto> toItemResponses(List<OrderItem> items) {
        List<OrderResponse.OrderItemDto> itemResponses = new ArrayList<>();
        for (OrderItem item : items) {
            OrderResponse.OrderItemDto itemResponse = new OrderResponse.OrderItemDto();
            itemResponse.setProductId(item.getProductId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponses.add(itemResponse);
        }
        return itemResponses;
    }

    private String toSpanishStatus(String status) {
        if (STATUS_PENDING.equals(status)) return USER_STATUS_PENDING;
        if (STATUS_CONFIRMED.equals(status)) return USER_STATUS_CONFIRMED;
        if (STATUS_CANCELLED_NO_STOCK.equals(status)) return USER_STATUS_CANCELLED_NO_STOCK;
        if (STATUS_DELIVERED.equals(status)) return USER_STATUS_DELIVERED;
        return status;
    }

    private String toSpanishShippingType(String shippingType) {
        if (SHIPPING_STANDARD.equals(shippingType)) return USER_SHIPPING_STANDARD;
        if (SHIPPING_EXPRESS.equals(shippingType)) return USER_SHIPPING_EXPRESS;
        return shippingType;
    }
}
