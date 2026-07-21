package com.inventory.control.system.infrastructure.seeder;

import com.inventory.control.system.adapters.out.database.entities.CategoryEntity;
import com.inventory.control.system.adapters.out.database.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepository categoryRepository;

    public DataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            log.info("Nenhuma categoria encontrada no banco SQL Server. Iniciando Carga Inicial (Data Seeder)...");

            CategoryEntity c1 = new CategoryEntity("Eletrônicos", "Dispositivos eletrônicos, componentes e acessórios");
            CategoryEntity c2 = new CategoryEntity("Periféricos", "Teclados, mouses, monitores e periféricos de informática");
            CategoryEntity c3 = new CategoryEntity("Móveis de Escritório", "Cadeiras ergonômicas, mesas e organizadores");
            CategoryEntity c4 = new CategoryEntity("Acessórios de Rede", "Cabos, roteadores, switches e adaptadores");

            categoryRepository.saveAll(List.of(c1, c2, c3, c4));

            log.info("Carga Inicial concluída com sucesso! 4 categorias foram inseridas no banco.");
        } else {
            log.info("Carga Inicial pulada: O banco de dados já possui categorias cadastradas.");
        }
    }
}