package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateProductService implements UpdateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProductService.class);

    private final ProductRepositoryPort productRepositoryPort;
    private final GetCategoryUseCase getCategoryUseCase;
    private final MeterRegistry meterRegistry;

    public UpdateProductService(ProductRepositoryPort productRepositoryPort,
                                GetCategoryUseCase getCategoryUseCase,
                                MeterRegistry meterRegistry) {
        this.productRepositoryPort = productRepositoryPort;
        this.getCategoryUseCase = getCategoryUseCase;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Product execute(String sku, Product product) {
        return Timer.builder("usecase.product.update.time")
                .description("Tempo de execução do caso de uso de atualização de produto")
                .tag("layer", "usecase")
                .register(meterRegistry)
                .record(() -> {
                    if (sku == null || sku.isBlank()) {
                        log.warn("Falha na atualização: SKU informado é nulo ou vazio.");
                        recordFailure("empty_sku");
                        throw new BusinessException("O SKU do produto é obrigatório para atualização.");
                    }

                    if (product.getCategory() == null || product.getCategory().getId() == null) {
                        log.warn("Falha na atualização do produto SKU '{}': Categoria ou ID da categoria é nulo.", sku);
                        recordFailure("invalid_category");
                        throw new BusinessException("É necessário informar uma categoria válida para atualizar o produto.");
                    }

                    String formattedSku = sku.trim().toUpperCase();

                    log.info("Iniciando atualização do produto com SKU: {} | Nova Categoria ID: {}",
                            formattedSku, product.getCategory().getId());

                    Product existingProduct = productRepositoryPort.findBySku(formattedSku)
                            .orElseThrow(() -> {
                                log.warn("Falha na atualização: Produto não encontrado no banco para o SKU '{}'.", formattedSku);
                                recordFailure("product_not_found");
                                return new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
                            });

                    Category category = getCategoryUseCase.findById(product.getCategory().getId());
                    log.debug("Categoria de ID {} validada com sucesso para o produto SKU '{}'.", category.getId(), formattedSku);

                    Product productToUpdate = new Product(
                            existingProduct.getId(),
                            formattedSku,
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            product.getQuantity(),
                            new Category(category.getId(), category.getName(), category.getDescription()));

                    Product updatedProduct = productRepositoryPort.updateProduct(productToUpdate);

                    meterRegistry.counter("business.product.updated.total", 
                            "category_name", category.getName() != null ? category.getName() : "unknown").increment();

                    log.info("Produto com SKU '{}' atualizado com sucesso.", updatedProduct.getSku());

                    return updatedProduct;
                });

    }

    private void recordFailure(String reason) {
        meterRegistry.counter("business.product.update.failures", 
                "layer", "usecase",
                "reason", reason).increment();
    }
}
