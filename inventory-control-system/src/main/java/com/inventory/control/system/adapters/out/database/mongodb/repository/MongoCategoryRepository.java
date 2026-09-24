package com.inventory.control.system.adapters.out.database.mongodb.repository;

import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoCategoryRepository extends MongoRepository<CategoryDocument, String> {

    Optional<CategoryDocument> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}