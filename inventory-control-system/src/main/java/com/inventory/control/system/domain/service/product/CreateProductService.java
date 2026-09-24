package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.product.CreateProductUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateProductService implements CreateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateProductService.class);

    private final ProductRepositoryPort productRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final MeterRegistry meterRegistry;

    public CreateProductService(ProductRepositoryPort productRepositoryPort, 
                                CategoryRepositoryPort categoryRepositoryPort, 
                                MeterRegistry meterRegistry) {
        this.productRepositoryPort = productRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Product execute(Product product) {
        return Timer.builder("usecase.product.create.time")
                .description("Tempo de execução do caso de uso de criação de produto")
                .tag("layer", "usecase")
                .register(meterRegistry)
                .record(() -> {
                    if (product.getSku() == null || product.getSku().isBlank()) {
                        log.warn("Falha ao criar produto: SKU informado é nulo ou vazio.");
                        recordFailure("empty_sku");
                        throw new BusinessException("O SKU do produto é obrigatório para criação.");
                    }

                    if (product.getCategory() == null || product.getCategory().getId() == null) {
                        log.warn("Falha ao criar produto SKU '{}': Categoria ou ID da categoria é nulo.", product.getSku());
                        recordFailure("invalid_category");
                        throw new BusinessException("É necessário informar uma categoria válida para o produto.");
                    }

                    String formattedSku = product.getSku().trim().toUpperCase();
                    String categoryId = product.getCategory().getId().trim();
                    log.info("Iniciando processo de criação de produto. SKU: {} | Categoria ID: {}", 
                            formattedSku, categoryId);

                    if (productRepositoryPort.existsBySku(formattedSku)) {
                        log.warn("Falha ao criar produto: SKU '{}' já está cadastrado no sistema.", formattedSku);
                        recordFailure("duplicate_sku");
                        throw new BusinessException("SKU já cadastrado: " + formattedSku);
                    }

                    Category category = categoryRepositoryPort.findById(categoryId)
                            .orElseThrow(() -> {
                                log.warn("Falha ao criar produto SKU '{}': Categoria ID {} não encontrada.", 
                                        formattedSku, categoryId);
                                recordFailure("category_not_found");
                                return new ResourceNotFoundException("Categoria não encontrada com o ID: " + categoryId);
                            });

                    log.debug("Categoria ID {} encontrada. Vinculando ao produto SKU '{}'.", category.getId(), formattedSku);

                    Product newProduct = new Product(
                        formattedSku, 
                        product.getName(), 
                        product.getDescription(), 
                        product.getPrice(), 
                        product.getQuantity(),
                        new Category(category.getId(), category.getName(), category.getDescription()) 
                    );
                    
                    Product savedProduct = productRepositoryPort.saveProduct(newProduct);

                    log.info("Produto com SKU '{}' criado com sucesso. ID gerado: {}", savedProduct.getSku(), savedProduct.getId());
                    
                    meterRegistry.counter("business.product.created.total", 
                            "category_name", category.getName() != null ? category.getName() : "unknown").increment();

                    return savedProduct;
                });
    }

    private void recordFailure(String reason) {
        meterRegistry.counter("business.product.creation.failures", 
                "layer", "usecase",
                "reason", reason).increment();
    }
}
