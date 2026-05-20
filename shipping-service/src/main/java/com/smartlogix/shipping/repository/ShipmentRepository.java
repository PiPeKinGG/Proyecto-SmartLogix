package com.smartlogix.shipping.repository;

import com.smartlogix.shipping.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findAllByPymeId(Long pymeId);
    Optional<Shipment> findByTrackingId(String trackingId);
    Optional<Shipment> findByOrderIdAndPymeId(Long orderId, Long pymeId);
}
