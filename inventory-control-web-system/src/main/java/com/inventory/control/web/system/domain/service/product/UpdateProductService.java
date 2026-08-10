package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateProductService implements UpdateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProductService.class);

    private final ProductFeignPort productFeignPort;
    private final CategoryFeignPort categoryFeignPort;

    public UpdateProductService(ProductFeignPort productFeignPort,
            CategoryFeignPort categoryFeignPort) {
        this.productFeignPort = productFeignPort;
        this.categoryFeignPort = categoryFeignPort;
    }

    @Override
    public Product execute(Product product) {
        if (product.getSku() == null || product.getSku().isBlank()) {
            log.warn("Falha na atualização: SKU informado é nulo ou vazio.");
            throw new BusinessException("O SKU do produto é obrigatório para atualização.");
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            log.warn("Falha na atualização do produto SKU '{}': Categoria ou ID da categoria é nulo.",
                    product.getSku());
            throw new BusinessException("É necessário informar uma categoria válida para atualizar o produto.");
        }

        String formattedSku = product.getSku().trim().toUpperCase();

        log.info("Iniciando atualização do produto com SKU: {} | Nova Categoria ID: {}",
                formattedSku, product.getCategory().getId());

        Product existingProduct = productFeignPort.findBySku(formattedSku)
                .orElseThrow(() -> {
                    log.warn("Falha na atualização: Produto não encontrado no banco para o SKU '{}'.", formattedSku);
                    return new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
                });

        Category category = categoryFeignPort.findById(product.getCategory().getId())
                .orElseThrow(() -> {
                    log.warn("Falha na atualização do produto SKU '{}': Categoria ID {} não encontrada.",
                            formattedSku, product.getCategory().getId());
                    return new ResourceNotFoundException(
                            "Categoria não encontrada com o ID: " + product.getCategory().getId());
                });

        log.debug("Categoria de ID {} validada com sucesso para o produto SKU '{}'.", category.getId(), formattedSku);

        Product productToUpdate = new Product(
                existingProduct.getId(),
                formattedSku,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                new Category(category.getId(), category.getName(), category.getDescription()));

        Product updatedProduct = productFeignPort.updateProduct(productToUpdate);

        log.info("Produto com SKU '{}' atualizado com sucesso.", updatedProduct.getSku());

        return updatedProduct;
    }
}
