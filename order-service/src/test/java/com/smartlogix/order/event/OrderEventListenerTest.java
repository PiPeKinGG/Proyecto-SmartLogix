package com.smartlogix.order.event;

import com.smartlogix.order.entity.Order;
import com.smartlogix.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderEventListenerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate;

    @InjectMocks
    private OrderEventListener listener;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setPymeId(100L);
        order.setUserId(7L);
        order.setStatus("PENDING");
        order.setShippingType("STANDARD");
    }

    @Test
    void testHandleInventoryReservedSuccess_ConfirmsOrderAndPublishesEvent() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        listener.handleInventoryReservedSuccess(new InventoryReservedSuccessEvent(1L, 100L));
        assertEquals("CONFIRMED", order.getStatus());
        verify(orderRepository).save(order);

        ArgumentCaptor<OrderConfirmedEvent> eventCaptor = ArgumentCaptor.forClass(OrderConfirmedEvent.class);
        verify(kafkaTemplate).send(eq("order-confirmed"), eventCaptor.capture());
        assertEquals(1L, eventCaptor.getValue().getOrderId());
        assertEquals(100L, eventCaptor.getValue().getPymeId());
        assertEquals(7L, eventCaptor.getValue().getUserId());
        assertEquals("STANDARD", eventCaptor.getValue().getShippingType());
    }

    @Test
    void testHandleInventoryReservedSuccess_IgnoresAlreadyConfirmedOrder() {
        order.setStatus("CONFIRMED");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        listener.handleInventoryReservedSuccess(new InventoryReservedSuccessEvent(1L, 100L));
        verify(orderRepository, never()).save(order);
        verify(kafkaTemplate, never()).send(eq("order-confirmed"),
                org.mockito.ArgumentMatchers.any(OrderConfirmedEvent.class));
    }

    @Test
    void testHandleInventoryReservedSuccess_PymeMismatch() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> listener.handleInventoryReservedSuccess(new InventoryReservedSuccessEvent(1L, 200L)));
        assertEquals("Orden no encontrada o pymeId no coincide", exception.getMessage());
        verify(orderRepository, never()).save(order);
    }

    @Test
    void testHandleInventoryReservedFailed_CancelsOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        listener.handleInventoryReservedFailed(new InventoryReservedFailedEvent(1L, 100L));
        assertEquals("CANCELLED_NO_STOCK", order.getStatus());
        verify(orderRepository).save(order);
    }
}
