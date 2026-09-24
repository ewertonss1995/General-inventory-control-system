package com.inventory.control.system.adapters.out.database.mongodb.repository;

import com.inventory.control.system.adapters.out.database.mongodb.documents.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MongoProductRepository extends MongoRepository<ProductDocument, String> {
    boolean existsBySkuIgnoreCase(String sku);
    Optional<ProductDocument> findBySkuIgnoreCase(String sku);
}