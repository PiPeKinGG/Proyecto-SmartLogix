package com.smartlogix.shipping.controller;

import com.smartlogix.shipping.event.OrderConfirmedEvent;
import com.smartlogix.shipping.service.ShippingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShippingEventListenerTest {

    @Mock
    private ShippingService shippingService;

    @InjectMocks
    private ShippingEventListener listener;

    @Test
    void testListenOrderConfirmed_DelegatesToShippingService() {
        // Given
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, 100L, 7L, "STANDARD");

        // When
        listener.listenOrderConfirmed(event);

        // Then
        verify(shippingService).handleOrderConfirmed(event);
    }
}
