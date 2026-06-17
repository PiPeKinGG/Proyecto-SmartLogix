package com.smartlogix.inventory.repository;

import com.smartlogix.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findAllByPymeId(Long pymeId);
    
    Optional<Product> findByIdAndPymeId(Long id, Long pymeId);

    @Modifying
    @Query("UPDATE Product p SET p.availableQuantity = p.availableQuantity - :quantity, p.reservedQuantity = p.reservedQuantity + :quantity WHERE p.id = :productId AND p.pymeId = :pymeId AND p.availableQuantity >= :quantity")
    int reserveStockAtomic(@Param("productId") Long productId, @Param("pymeId") Long pymeId, @Param("quantity") int quantity);

    @Modifying
    @Query("UPDATE Product p SET p.reservedQuantity = p.reservedQuantity - :quantity, p.totalQuantity = p.totalQuantity - :quantity WHERE p.id = :productId AND p.pymeId = :pymeId AND p.reservedQuantity >= :quantity")
    int confirmReservationAtomic(@Param("productId") Long productId, @Param("pymeId") Long pymeId, @Param("quantity") int quantity);

    @Modifying
    @Query("UPDATE Product p SET p.availableQuantity = p.availableQuantity + :quantity, p.reservedQuantity = p.reservedQuantity - :quantity WHERE p.id = :productId AND p.pymeId = :pymeId AND p.reservedQuantity >= :quantity")
    int cancelReservationAtomic(@Param("productId") Long productId, @Param("pymeId") Long pymeId, @Param("quantity") int quantity);
}