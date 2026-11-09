package com.inventory.control.web.system.domain.service.kafka;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.out.EventPublisherPort;
import com.inventory.control.web.system.ports.in.product.ProductCsvProducerUseCase;
import com.inventory.control.web.system.domain.model.ProductMessage;
import com.inventory.control.web.system.domain.exception.BusinessException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductCsvProducerService implements ProductCsvProducerUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(ProductCsvProducerService.class);

    private final EventPublisherPort eventPublisher;
    private static final String TOPIC_NAME = "inventory-csv-import-topic";

    public ProductCsvProducerService(EventPublisherPort eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publishProducts(List<Product> products) {
        for (Product product : products) {

        if (product.getSku() == null || product.getSku().isBlank()) {
            log.warn("Falha ao criar produto: SKU informado é nulo ou vazio.");
            throw new BusinessException("O SKU do produto é obrigatório para criação.");
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            log.warn("Falha ao criar produto SKU '{}': Categoria ou ID da categoria é nulo.", product.getSku());
            throw new BusinessException("É necessário informar uma categoria válida para o produto.");
        }
        
        log.info("Executando caso de uso para publicar mensagem para o produto SKU: {}.", product.getSku());

            ProductMessage payload = new ProductMessage(
                    product.getSku(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getDescription()
            );

            eventPublisher.publish(TOPIC_NAME, product.getSku(), payload);

            log.info("Mensagem publicada para o produto {} com SKU '{}'.", product.getName(), product.getSku());

        }
    }
}