package com.inventory.control.system.adapters.out.database.postgres.repository;

import com.inventory.control.system.adapters.out.database.postgres.entities.StockBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PostgresStockRepository extends JpaRepository<StockBalanceEntity, UUID> {
    Optional<StockBalanceEntity> findByProductId(String productId);
    Optional<StockBalanceEntity> findBySkuIgnoreCase(String sku);
}