package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.UpdateStockInput;
import com.inventory.control.system.ports.in.product.UpdateStockUseCase;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class UpdateStockService implements UpdateStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateStockService.class);

    private final ProductRepositoryPort productRepositoryPort;
    private final MeterRegistry meterRegistry;

    public UpdateStockService(ProductRepositoryPort productRepositoryPort, MeterRegistry meterRegistry) {
        this.productRepositoryPort = productRepositoryPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Product execute(String sku, UpdateStockInput input) {
        return Timer.builder("usecase.stock.update.time")
                .description("Tempo de execução do caso de uso de movimentação de estoque")
                .tag("layer", "usecase")
                .register(meterRegistry)
                .record(() -> {
                    if (Objects.isNull(sku) || sku.isBlank()) {
                        log.warn("Falha na atualização de estoque: SKU fornecido é nulo ou vazio.");
                        recordFailure("invalid_sku");
                        throw new IllegalArgumentException("SKU não pode ser nulo ou vazio.");
                    }

                    String formattedSku = sku.trim().toUpperCase();

                    log.info("Iniciando movimentação de estoque. SKU: {} | Tipo: {} | Quantidade: {}", 
                            formattedSku, input.movementType(), input.quantity());

                    Product product = productRepositoryPort.findBySku(formattedSku)
                            .orElseThrow(() -> {
                                log.warn("Falha na atualização de estoque: Produto SKU '{}' não foi encontrado.", formattedSku);
                                recordFailure("product_not_found");
                                return new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
                            });

                    int previousQuantity = product.getQuantity();

                    try {
                        switch (input.movementType()) {
                            case IN -> {
                                log.debug("Processando ENTRADA de estoque. Saldo anterior: {} | Adicionando: {}", previousQuantity, input.quantity());
                                product.addStock(input.quantity());
                                recordMovementItems("IN", input.quantity());
                            }
                            case OUT -> {
                                log.debug("Processando SAÍDA de estoque. Saldo anterior: {} | Subtraindo: {}", previousQuantity, input.quantity());
                                product.removeStock(input.quantity());
                                recordMovementItems("OUT", input.quantity());
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Falha de regra de negócio ao atualizar estoque. SKU: {} | Erro: {}", formattedSku, e.getMessage());
                        recordFailure("business_rule_violation");
                        throw e;
                    }

                    Product updatedProduct = productRepositoryPort.updateProduct(product);

                    meterRegistry.counter("business.stock.movement.operations.total", 
                            "type", input.movementType().name()).increment();

                    log.info("Movimentação de estoque concluída com sucesso. SKU: {} | Saldo anterior: {} | Novo saldo: {}", 
                            formattedSku, previousQuantity, updatedProduct.getQuantity());

                    return updatedProduct;
                });
    }

    private void recordMovementItems(String type, int quantity) {
        meterRegistry.counter("business.stock.movement.items", "type", type).increment(quantity);
    }

    private void recordFailure(String reason) {
        meterRegistry.counter("business.stock.update.failures", 
                "layer", "usecase",
                "reason", reason).increment();
    }
}
