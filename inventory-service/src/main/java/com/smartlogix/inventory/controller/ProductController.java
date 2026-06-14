package com.smartlogix.inventory.controller;

import com.smartlogix.inventory.dto.ProductRequest;
import com.smartlogix.inventory.dto.ProductResponse;
import com.smartlogix.inventory.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(@RequestHeader("pyme_id") Long pymeId) {
        return ResponseEntity.ok(productService.getAllProductsByPyme(pymeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id, @RequestHeader("pyme_id") Long pymeId) {
        return productService.getProductById(id, pymeId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request, @RequestHeader("pyme_id") Long pymeId) {
        ProductResponse createdProduct = productService.createProduct(request, pymeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request, @RequestHeader("pyme_id") Long pymeId) {
        return productService.updateProduct(id, pymeId, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestParam Long productId, @RequestParam int quantity, @RequestHeader("pyme_id") Long pymeId) {
        boolean reserved = productService.reserveStock(productId, pymeId, quantity);
        if (reserved) {
            return ResponseEntity.ok("Stock reservado");
        } else {
            return ResponseEntity.badRequest().body("No hay stock suficiente");
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam Long productId, @RequestParam int quantity, @RequestHeader("pyme_id") Long pymeId) {
        productService.confirmReservation(productId, pymeId, quantity);
        return ResponseEntity.ok("Reserva confirmada");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestParam Long productId, @RequestParam int quantity, @RequestHeader("pyme_id") Long pymeId) {
        productService.cancelReservation(productId, pymeId, quantity);
        return ResponseEntity.ok("Reserva cancelada");
    }
}