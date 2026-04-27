package com.smartlogix.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @PostMapping("/products/reserve")
    String reserveStock(@RequestParam("productId") Long productId,
                        @RequestParam("quantity") int quantity,
                        @RequestHeader("pyme_id") Long pymeId);

    @PostMapping("/products/confirm")
    String confirmReservation(@RequestParam("productId") Long productId,
                             @RequestParam("quantity") int quantity,
                             @RequestHeader("pyme_id") Long pymeId);

    @PostMapping("/products/cancel")
    String cancelReservation(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity,
                            @RequestHeader("pyme_id") Long pymeId);
}
