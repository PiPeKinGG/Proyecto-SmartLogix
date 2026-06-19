package com.smartlogix.inventory.repository;

import com.smartlogix.inventory.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager em;

    private Product sampleProduct() {
        Product p = new Product();
        p.setPymeId(100L);
        p.setName("Repo Product");
        p.setTotalQuantity(50);
        p.setAvailableQuantity(50);
        p.setReservedQuantity(0);
        p.setPrice(19.99);
        return p;
    }

    @Test
    void testFinders() {
        Product p = sampleProduct();
        productRepository.save(p);

        List<Product> list = productRepository.findAllByPymeId(100L);
        assertThat(list).hasSize(1);

        Optional<Product> opt = productRepository.findByIdAndPymeId(p.getId(), 100L);
        assertThat(opt).isPresent();
        assertThat(opt.get().getName()).isEqualTo("Repo Product");
    }

    @Test
    void testAtomicReserveConfirmCancel() {
        Product p = sampleProduct();
        productRepository.save(p);
        Long id = p.getId();

        // Reserve 10
        int reserved = productRepository.reserveStockAtomic(id, 100L, 10);
        assertThat(reserved).isGreaterThan(0);

        // refresh entity
        em.clear();
        Optional<Product> afterReserve = productRepository.findById(id);
        assertThat(afterReserve).isPresent();
        assertThat(afterReserve.get().getAvailableQuantity()).isEqualTo(40);
        assertThat(afterReserve.get().getReservedQuantity()).isEqualTo(10);

        // Confirm reservation
        int confirmed = productRepository.confirmReservationAtomic(id, 100L, 10);
        assertThat(confirmed).isGreaterThan(0);

        em.clear();
        Optional<Product> afterConfirm = productRepository.findById(id);
        assertThat(afterConfirm).isPresent();
        assertThat(afterConfirm.get().getReservedQuantity()).isEqualTo(0);
        assertThat(afterConfirm.get().getTotalQuantity()).isEqualTo(40);

        // Cancel (reserve again first)
        productRepository.reserveStockAtomic(id, 100L, 5);
        em.clear();
        int cancelled = productRepository.cancelReservationAtomic(id, 100L, 5);
        assertThat(cancelled).isGreaterThan(0);

        em.clear();
        Optional<Product> afterCancel = productRepository.findById(id);
        assertThat(afterCancel).isPresent();
        assertThat(afterCancel.get().getReservedQuantity()).isEqualTo(0);
    }
}