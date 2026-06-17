package com.smartlogix.order.service;

import com.smartlogix.order.dto.OrderRequest;
import com.smartlogix.order.dto.OrderResponse;
import com.smartlogix.order.entity.Order;
import com.smartlogix.order.event.OrderConfirmedEvent;
import com.smartlogix.order.feign.InventoryClient;
import com.smartlogix.order.repository.OrderRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private KafkaTemplate<String, OrderConfirmedEvent> orderConfirmedKafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest request;

    @BeforeEach
    void setUp() {
        request = new OrderRequest();
        request.setPymeId(100L);
        request.setUserId(7L);
        request.setCustomerName("Cliente Test");
        request.setCustomerRut("11.111.111-1");
        request.setCustomerEmail("cliente@test.com");
        request.setShippingAddress("Av. Test 123");
        request.setShippingType("expres");
        request.setTotalAmount(25000.0);
        request.setItems(List.of(itemRequest(10L, 2), itemRequest(20L, 4)));
    }

    @Test
    void testCreateOrder_Success() {
        // Given
        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(100L, response.getPymeId());
        assertEquals(7L, response.getUserId());
        assertEquals("Confirmado", response.getStatus());
        assertEquals("Expres", response.getShippingType());
        assertEquals(2, response.getItems().size());
        assertEquals(10L, response.getItems().get(0).getProductId());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("CONFIRMED", orderCaptor.getValue().getStatus());
        assertEquals("EXPRESS", orderCaptor.getValue().getShippingType());

        verify(inventoryClient).reserveStock(10L, 2, 100L);
        verify(inventoryClient).reserveStock(20L, 4, 100L);

        ArgumentCaptor<OrderConfirmedEvent> eventCaptor = ArgumentCaptor.forClass(OrderConfirmedEvent.class);
        verify(orderConfirmedKafkaTemplate).send(eq("order-confirmed"), eventCaptor.capture());
        OrderConfirmedEvent event = eventCaptor.getValue();
        assertEquals(1L, event.getOrderId());
        assertEquals(100L, event.getPymeId());
        assertEquals(7L, event.getUserId());
        assertEquals("EXPRESS", event.getShippingType());
    }

    @Test
    void testCreateOrder_CancelsReservedItemsWhenStockReservationFails() {
        FeignException stockException = mock(FeignException.class);
        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(2L);
            return order;
        });
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inventoryClient.reserveStock(anyLong(), anyInt(), eq(100L))).thenAnswer(invocation -> {
            Long productId = invocation.getArgument(0);
            if (productId.equals(20L)) {
                throw stockException;
            }
            return "Stock reservado";
        });

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(2L, response.getOrderId());
        assertEquals("Cancelado por falta de stock", response.getStatus());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("CANCELLED_NO_STOCK", orderCaptor.getValue().getStatus());

        verify(inventoryClient).cancelReservation(10L, 2, 100L);
        verify(inventoryClient, never()).cancelReservation(eq(20L), anyInt(), anyLong());
        verify(orderConfirmedKafkaTemplate, never()).send(eq("order-confirmed"), any(OrderConfirmedEvent.class));
    }

    @Test
    void testCreateOrder_InvalidShippingType() {
        request.setShippingType("OVERNIGHT");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
        assertEquals("El tipo de envio debe ser Estandar o Expres", exception.getMessage());

        verify(orderRepository, never()).saveAndFlush(any(Order.class));
        verifyNoInteractions(inventoryClient, orderConfirmedKafkaTemplate);
    }

    @Test
    void testUpdateOrderStatusToDelivered_Success() {
        Order order = order(3L, "CONFIRMED", "STANDARD");
        when(orderRepository.findByIdAndPymeId(3L, 100L)).thenReturn(Optional.of(order));

        orderService.updateOrderStatusToDelivered(3L, 100L);

        assertEquals("DELIVERED", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testUpdateOrderStatusToDelivered_NotFound() {
        when(orderRepository.findByIdAndPymeId(99L, 100L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatusToDelivered(99L, 100L)
        );
        assertEquals("Pedido no encontrado o no pertenece a la pyme", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrdersByPyme_MapsResponseForUser() {
        Order order = order(4L, "DELIVERED", "STANDARD");
        when(orderRepository.findAllByPymeIdOrderByCreatedAtDesc(100L)).thenReturn(List.of(order));

        List<OrderResponse> responses = orderService.getOrdersByPyme(100L);

        assertEquals(1, responses.size());
        assertEquals(4L, responses.get(0).getOrderId());
        assertEquals("Entregado", responses.get(0).getStatus());
        assertEquals("Estandar", responses.get(0).getShippingType());
        assertEquals(2, responses.get(0).getItems().size());
    }

    private OrderRequest.OrderItemRequest itemRequest(Long productId, Integer quantity) {
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(productId);
        item.setQuantity(quantity);
        return item;
    }

    private Order order(Long id, String status, String shippingType) {
        Order order = new Order();
        order.setId(id);
        order.setPymeId(100L);
        order.setUserId(7L);
        order.setCustomerName("Cliente Test");
        order.setCustomerRut("11.111.111-1");
        order.setCustomerEmail("cliente@test.com");
        order.setShippingAddress("Av. Test 123");
        order.setShippingType(shippingType);
        order.setTotalAmount(25000.0);
        order.setStatus(status);
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(List.of(orderItem(order, 10L, 2), orderItem(order, 20L, 4)));
        return order;
    }

    private com.smartlogix.order.entity.OrderItem orderItem(Order order, Long productId, Integer quantity) {
        com.smartlogix.order.entity.OrderItem item = new com.smartlogix.order.entity.OrderItem();
        item.setOrder(order);
        item.setProductId(productId);
        item.setQuantity(quantity);
        return item;
    }
}
