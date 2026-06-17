package com.smartlogix.inventory.service;

import com.smartlogix.inventory.dto.ProductRequest;
import com.smartlogix.inventory.dto.ProductResponse;
import com.smartlogix.inventory.entity.Product;
import com.smartlogix.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getAllProductsByPyme(Long pymeId) {
        return productRepository.findAllByPymeId(pymeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponse> getProductById(Long id, Long pymeId) {
        return productRepository.findByIdAndPymeId(id, pymeId)
                .map(this::mapToResponse);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, Long pymeId) {
        Product product = new Product();
        product.setPymeId(pymeId);
        product.setName(request.getName());
        product.setTotalQuantity(request.getTotalQuantity());
        product.setAvailableQuantity(request.getTotalQuantity());
        product.setReservedQuantity(0);
        product.setPrice(request.getPrice());

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Transactional
    public Optional<ProductResponse> updateProduct(Long id, Long pymeId, ProductRequest productDetails) {
        Optional<Product> opt = productRepository.findByIdAndPymeId(id, pymeId);
        if (opt.isPresent()) {
            Product existingProduct = opt.get();
            existingProduct.setName(productDetails.getName());
            existingProduct.setPrice(productDetails.getPrice());

            int diff = productDetails.getTotalQuantity() - existingProduct.getTotalQuantity();
            existingProduct.setTotalQuantity(productDetails.getTotalQuantity());
            existingProduct.setAvailableQuantity(existingProduct.getAvailableQuantity() + diff);

            return Optional.of(mapToResponse(productRepository.save(existingProduct)));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean reserveStock(Long productId, Long pymeId, int quantity) {
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

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setPymeId(product.getPymeId());
        response.setName(product.getName());
        response.setAvailableQuantity(product.getAvailableQuantity());
        response.setReservedQuantity(product.getReservedQuantity());
        response.setTotalQuantity(product.getTotalQuantity());
        response.setPrice(product.getPrice());
        return response;
    }
}