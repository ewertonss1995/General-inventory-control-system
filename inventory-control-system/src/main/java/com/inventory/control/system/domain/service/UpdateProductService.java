package com.inventory.control.system.domain.service;

import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.UpdateProductUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateProductService implements UpdateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProductService.class);

    private final ProductRepositoryPort productRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;

    public UpdateProductService(ProductRepositoryPort productRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public Product execute(Product product) {
        String formattedSku = product.getSku() != null ? product.getSku().trim().toUpperCase() : null;

        log.info("Iniciando atualização do produto com SKU: {} | Nova Categoria ID: {}", 
                formattedSku, product.getCategoryId());

        // Validação da existência do Produto
        if (!productRepositoryPort.existsBySku(formattedSku)) {
            log.warn("Falha na atualização: Produto não encontrado para o SKU '{}'.", formattedSku);
            throw new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
        }

        // Validação da Categoria
        Category category = categoryRepositoryPort.findById(product.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Falha na atualização do produto SKU '{}': Categoria ID {} não encontrada.", 
                            formattedSku, product.getCategoryId());
                    return new ResourceNotFoundException("Categoria não encontrada com o ID: " + product.getCategoryId());
                });

        log.debug("Categoria de ID {} validada com sucesso para o produto SKU '{}'.", category.getId(), formattedSku);

        Product productToUpdate = new Product(
            formattedSku, 
            product.getName(), 
            product.getDescription(), 
            product.getPrice(), 
            product.getQuantity(), 
            category.getId(),
            category.getName()
        );
        
        Product updatedProduct = productRepositoryPort.updateProduct(productToUpdate);

        log.info("Produto com SKU '{}' atualizado com sucesso no caso de uso.", updatedProduct.getSku());

        return updatedProduct;
    }
}
