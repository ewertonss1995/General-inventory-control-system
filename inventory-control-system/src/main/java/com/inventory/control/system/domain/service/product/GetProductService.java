package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.product.GetProductUseCase;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

public class GetProductService implements GetProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetProductService.class);

    private final ProductRepositoryPort productRepositoryPort;
    private final MeterRegistry meterRegistry;

    public GetProductService(ProductRepositoryPort productRepositoryPort, MeterRegistry meterRegistry) {
        this.productRepositoryPort = productRepositoryPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public List<Product> findAll() {
        return executeWithTimer("findAll", () -> {
            log.info("Executando caso de uso para listar todos os produtos.");

            List<Product> products = productRepositoryPort.findAll();
            meterRegistry.summary("usecase.product.findall.result.size").record(products.size());

            log.info("Consulta de produtos concluída. Total retornado: {}", products.size());
            return products;
        });
    }

    @Override
    public Product findBySku(String sku) {
        return executeWithTimer("findById", () -> {
            if (sku == null || sku.isBlank()) {
                log.warn("Tentativa de busca com SKU nulo ou em branco. SKU recebido: '{}'", sku);
                recordFailure("empty_sku");
                throw new BusinessException("O SKU informado para busca não pode ser nulo ou vazio.");
            }

            String formattedSku = sku.trim().toUpperCase();
            log.info("Executando caso de uso para buscar produto pelo SKU: {}", formattedSku);

            return productRepositoryPort.findBySku(formattedSku)
                    .orElseThrow(() -> {
                        log.warn("Falha na busca de produto: SKU '{}' não encontrado.", formattedSku);
                        recordFailure("product_not_found");
                        return new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
                    });
        });
    }

    private <T> T executeWithTimer(String operation, Supplier<T> supplier) {
        return Timer.builder("usecase.category.time")
                .description("Tempo de execução dos casos de uso de busca de categoria")
                .tag("layer", "usecase")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(supplier);
    }

    private void recordFailure(String reason) {
        meterRegistry.counter("business.product.get.failures",
                "layer", "usecase",
                "reason", reason).increment();
    }
}
