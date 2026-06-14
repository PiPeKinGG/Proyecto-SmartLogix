package com.smartlogix.shipping.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.shipping.dto.ShipmentResponse;
import com.smartlogix.shipping.service.ShippingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
public class ShippingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShippingService shippingService;

    @InjectMocks
    private ShippingController shippingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(shippingController)
                .setControllerAdvice(new com.smartlogix.shipping.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void getShipments_ReturnsList() throws Exception {
        ShipmentResponse resp = new ShipmentResponse();
        resp.setId(1L);
        resp.setTrackingId("TRACK-1");
        resp.setShippingType("STANDARD");

        when(shippingService.getShipmentsByPyme(100L)).thenReturn(List.of(resp));

        mockMvc.perform(get("/shipping").header("pyme_id", 100L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].trackingId", is("TRACK-1")));
    }

    @Test
    void getByTrackingId_ReturnsOk() throws Exception {
        ShipmentResponse resp = new ShipmentResponse();
        resp.setId(5L);
        resp.setTrackingId("T-5");

        when(shippingService.getShipmentByTrackingId("T-5", 100L)).thenReturn(resp);

        mockMvc.perform(get("/shipping/tracking/T-5").header("pyme_id", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.trackingId", is("T-5")));
    }

    @Test
    void getByTrackingId_ServiceThrows_ReturnsBadRequest() throws Exception {
        when(shippingService.getShipmentByTrackingId("X", 100L)).thenThrow(new IllegalArgumentException("Envio no encontrado o no pertenece a la pyme"));

        mockMvc.perform(get("/shipping/tracking/X").header("pyme_id", 100L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Envio no encontrado o no pertenece a la pyme")));
    }

    @Test
    void getByOrderId_ReturnsOk() throws Exception {
        ShipmentResponse resp = new ShipmentResponse();
        resp.setId(7L);
        when(shippingService.getShipmentByOrderId(10L, 100L)).thenReturn(resp);

        mockMvc.perform(get("/shipping/orders/10").header("pyme_id", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)));
    }
}
