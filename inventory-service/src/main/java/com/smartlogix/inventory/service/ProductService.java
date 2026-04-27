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
    public boolean reserveStock(Long productId, Long pymeId, int quantity) {
        Optional<Product> opt = productRepository.findByIdAndPymeId(productId, pymeId);
        if (opt.isPresent()) {
            Product product = opt.get();
            if (product.getAvailableQuantity() >= quantity) {
                product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
                product.setReservedQuantity(product.getReservedQuantity() + quantity);
                productRepository.save(product);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void confirmReservation(Long productId, Long pymeId, int quantity) {
        Optional<Product> opt = productRepository.findByIdAndPymeId(productId, pymeId);
        if (opt.isPresent()) {
            Product product = opt.get();
            product.setReservedQuantity(product.getReservedQuantity() - quantity);
            product.setTotalQuantity(product.getTotalQuantity() - quantity);
            productRepository.save(product);
        }
    }

    @Transactional
    public void cancelReservation(Long productId, Long pymeId, int quantity) {
        Optional<Product> opt = productRepository.findByIdAndPymeId(productId, pymeId);
        if (opt.isPresent()) {
            Product product = opt.get();
            product.setAvailableQuantity(product.getAvailableQuantity() + quantity);
            product.setReservedQuantity(product.getReservedQuantity() - quantity);
            productRepository.save(product);
        }
    }
}
