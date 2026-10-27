package com.inventory.control.web.system.domain.service.kafka;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.out.EventPublisherPort;
import com.inventory.control.web.system.ports.in.product.ProductCsvProducerUseCase;
import com.inventory.control.web.system.domain.model.ProductMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductCsvProducerService implements ProductCsvProducerUseCase {

    private final EventPublisherPort eventPublisher;
    private static final String TOPIC_NAME = "inventory-csv-import-topic";

    public ProductCsvProducerService(EventPublisherPort eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

@Override
    public void publishProducts(List<Product> products) {
        for (Product product : products) {
            ProductMessage payload = new ProductMessage(
                    product.getSku(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getCategory().getName(),
                    product.getCategory().getDescription()
            );

            eventPublisher.publish(TOPIC_NAME, product.getSku(), payload);
        }
    }
}