package com.smartlogix.auth.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleRuntimeException() {
        // Given
        RuntimeException exception = new RuntimeException("Credenciales inválidas");

        // When
        // Nota: Asegúrate de que el método invocado corresponda a la firma que tienes en tu clase real
        ResponseEntity<?> response = exceptionHandler.handleRuntimeException(exception);

        // Then
        assertNotNull(response);
        // Ajusta el estatus esperado de acuerdo a la respuesta real que configuraste (ej. UNAUTHORIZED o BAD_REQUEST)
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}