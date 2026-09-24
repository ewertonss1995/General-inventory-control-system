package com.inventory.control.system.adapters.out.database.mongodb.seeder;

import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryDocument;
import com.inventory.control.system.adapters.out.database.mongodb.repository.MongoCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CategoryDataSeeder.class);

    private final MongoCategoryRepository categoryRepository;

    public CategoryDataSeeder(MongoCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Iniciando verificação do seed de categorias no MongoDB...");
        
        List<CategoryDocument> defaultCategories = List.of(
            CategoryDocument.builder()
            .name("Eletrônicos")
            .description("Dispositivos eletrônicos, componentes e acessórios")
            .build(),
        CategoryDocument.builder()
            .name("Periféricos")
            .description("Teclados, mouses, monitores e periféricos de informática")
            .build(),
        CategoryDocument.builder()
            .name("Móveis de Escritório")
            .description("Cadeiras ergonômicas, mesas e organizadores")
            .build(),
        CategoryDocument.builder()
            .name("Acessórios de Rede")
            .description("Cabos, roteadores, switches e adaptadores")
            .build()
        );

        int addedCount = 0;

        for (CategoryDocument category : defaultCategories) {
            if (!categoryRepository.existsByNameIgnoreCase(category.getName())) {
                categoryRepository.save(category);
                log.info("Categoria cadastrada com sucesso: '{}'", category.getName());
                addedCount++;
            } else {
                log.debug("Categoria '{}' já existe no MongoDB. Ignorando insenção.", category.getName());
            }
        }

        if (addedCount > 0) {
            log.info("Seed de categorias concluído. Total de novas categorias inseridas: {}", addedCount);
        } else {
            log.info("Todas as categorias padrão já estão cadastradas no MongoDB.");
        }
    }
}
