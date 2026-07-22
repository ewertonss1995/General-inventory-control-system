package com.inventory.control.web.system.infrastructure.config;

import com.inventory.control.web.system.domain.service.ProductCatalogService;
import com.inventory.control.web.system.ports.out.InventoryClientPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ProductCatalogService productCatalogService(InventoryClientPort inventoryClientPort) {
        return new ProductCatalogService(inventoryClientPort);
    }
}