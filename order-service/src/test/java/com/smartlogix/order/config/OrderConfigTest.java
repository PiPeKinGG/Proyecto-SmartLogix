package com.smartlogix.order.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderConfigTest {
    @Test
    void testOrderConfigLoads() {
        OrderConfig config = new OrderConfig();
        assertNotNull(config);
    }
}
