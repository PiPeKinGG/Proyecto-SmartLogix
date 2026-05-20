package com.smartlogix.inventory.service;

import com.smartlogix.inventory.entity.Product;
import com.smartlogix.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProductsByPyme(Long pymeId) {
        return productRepository.findAllByPymeId(pymeId);
    }

    public Optional<Product> getProductById(Long id, Long pymeId) {
        return productRepository.findByIdAndPymeId(id, pymeId);
    }

    public Product createProduct(Product product) {
        product.setAvailableQuantity(product.getTotalQuantity());
        product.setReservedQuantity(0);
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> updateProduct(Long id, Long pymeId, Product productDetails) {
        Optional<Product> opt = productRepository.findByIdAndPymeId(id, pymeId);
        if (opt.isPresent()) {
            Product existingProduct = opt.get();
            existingProduct.setName(productDetails.getName());

            // Calculamos la diferencia en la cantidad total para ajustar la disponible
            int diff = productDetails.getTotalQuantity() - existingProduct.getTotalQuantity();
            existingProduct.setTotalQuantity(productDetails.getTotalQuantity());
            
            // Sumamos (o restamos) la diferencia al stock disponible actual
            existingProduct.setAvailableQuantity(existingProduct.getAvailableQuantity() + diff);

            return Optional.of(productRepository.save(existingProduct));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean reserveStock(Long productId, Long pymeId, int quantity) {
        // Ejecuta la actualización atómica en la base de datos.
        // Si retorna > 0, significa que encontró el producto y había stock suficiente.
        int updatedRows = productRepository.reserveStockAtomic(productId, pymeId, quantity);
        return updatedRows > 0;
    }

    @Transactional
    public void confirmReservation(Long productId, Long pymeId, int quantity) {
        productRepository.confirmReservationAtomic(productId, pymeId, quantity);
    }

    @Transactional
    public void cancelReservation(Long productId, Long pymeId, int quantity) {
        productRepository.cancelReservationAtomic(productId, pymeId, quantity);
    }
}