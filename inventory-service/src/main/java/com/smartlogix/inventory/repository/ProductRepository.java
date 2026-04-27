package com.smartlogix.inventory.repository;

import com.smartlogix.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByPymeId(Long pymeId);
    Optional<Product> findByIdAndPymeId(Long id, Long pymeId);
}
