package com.smartlogix.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class EurekaServerApplicationTest {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring se cargue correctamente
    }

    @Test
    void testApplicationHasRequiredSpringAnnotations() {
        assertTrue(EurekaServerApplication.class.isAnnotationPresent(SpringBootApplication.class));
        assertTrue(EurekaServerApplication.class.isAnnotationPresent(EnableEurekaServer.class));
    }
}
