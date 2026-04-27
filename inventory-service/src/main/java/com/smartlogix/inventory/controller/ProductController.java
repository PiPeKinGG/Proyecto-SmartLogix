package com.smartlogix.inventory.controller;

import com.smartlogix.inventory.entity.Product;
import com.smartlogix.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAll(@RequestHeader("pyme_id") Long pymeId) {
        return productService.getAllProductsByPyme(pymeId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id, @RequestHeader("pyme_id") Long pymeId) {
        Optional<Product> product = productService.getProductById(id, pymeId);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product create(@RequestBody Product product, @RequestHeader("pyme_id") Long pymeId) {
        product.setPymeId(pymeId);
        return productService.createProduct(product);
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
