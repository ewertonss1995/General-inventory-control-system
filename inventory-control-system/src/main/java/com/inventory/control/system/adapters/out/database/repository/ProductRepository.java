package com.inventory.control.system.adapters.out.database.repository;

import com.inventory.control.system.adapters.out.database.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsBySkuIgnoreCase(String sku);
    Optional<ProductEntity> findBySkuIgnoreCase(String sku);
}