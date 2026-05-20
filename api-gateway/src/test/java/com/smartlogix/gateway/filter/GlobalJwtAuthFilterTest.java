package com.smartlogix.gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GlobalJwtAuthFilterTest {

    private static final String JWT_SECRET = "01234567890123456789012345678901";

    private GlobalJwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new GlobalJwtAuthFilter();
        ReflectionTestUtils.setField(filter, "jwtSecret", JWT_SECRET);
    }

    @Test
    void testFilter_AllowsAuthRoutesWithoutToken() {
        // Given
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/auth/login"));
        GatewayFilterChain chain = capturedExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        // When
        filter.filter(exchange, chain).block();

        // Then
        assertTrue(chainCalled.get());
    }

    @Test
    void testFilter_AllowsOptionsRequestsWithoutToken() {
        // Given
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.method(HttpMethod.OPTIONS, "/orders"));
        GatewayFilterChain chain = capturedExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        // When
        filter.filter(exchange, chain).block();

        // Then
        assertTrue(chainCalled.get());
    }

    @Test
    void testFilter_RejectsProtectedRouteWithoutBearerToken() {
        // Given
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/orders"));
        GatewayFilterChain chain = capturedExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        // When
        filter.filter(exchange, chain).block();

        // Then
        assertFalse(chainCalled.get());
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testFilter_RejectsInvalidToken() {
        // Given
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/orders").header("Authorization", "Bearer invalid-token")
        );
        GatewayFilterChain chain = capturedExchange -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        // When
        filter.filter(exchange, chain).block();

        // Then
        assertFalse(chainCalled.get());
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testFilter_WithValidTokenAddsDownstreamHeaders() {
        // Given
        AtomicReference<ServerWebExchange> forwardedExchange = new AtomicReference<>();
        String token = token(7L, 100L, "ADMIN");
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/orders").header("Authorization", "Bearer " + token)
        );
        GatewayFilterChain chain = capturedExchange -> {
            forwardedExchange.set(capturedExchange);
            return Mono.empty();
        };

        // When
        filter.filter(exchange, chain).block();

        // Then
        assertNotNull(forwardedExchange.get());
        assertEquals("7", forwardedExchange.get().getRequest().getHeaders().getFirst("userId"));
        assertEquals("100", forwardedExchange.get().getRequest().getHeaders().getFirst("pyme_id"));
        assertEquals("ADMIN", forwardedExchange.get().getRequest().getHeaders().getFirst("role"));
    }

    @Test
    void testGetOrder() {
        assertEquals(-1, filter.getOrder());
    }

    private String token(Long userId, Long pymeId, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("pymeId", pymeId)
                .claim("role", role)
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
