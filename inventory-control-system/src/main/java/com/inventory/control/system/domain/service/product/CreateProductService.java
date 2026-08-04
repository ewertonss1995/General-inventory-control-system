package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.pruduct.CreateProductUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateProductService implements CreateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateProductService.class);

    private final ProductRepositoryPort productRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;

    public CreateProductService(ProductRepositoryPort productRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

@Override
    public Product execute(Product product) {
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            log.warn("Falha ao criar produto SKU '{}': Categoria ou ID da categoria é nulo.", product.getSku());
            throw new BusinessException("É necessário informar uma categoria válida para o produto.");
        }

        log.info("Iniciando processo de criação de produto. SKU: {} | Categoria ID: {}", 
                product.getSku(), product.getCategory().getId());

        if (productRepositoryPort.existsBySku(product.getSku())) {
            log.warn("Falha ao criar produto: SKU '{}' já está cadastrado no sistema.", product.getSku());
            throw new BusinessException("SKU já cadastrado: " + product.getSku());
        }

        Category category = categoryRepositoryPort.findById(product.getCategory().getId())
                .orElseThrow(() -> {
                    log.warn("Falha ao criar produto SKU '{}': Categoria ID {} não encontrada.", 
                            product.getSku(), product.getCategory().getId());
                    return new ResourceNotFoundException("Categoria não encontrada com o ID: " + product.getCategory().getId());
                });

        log.debug("Categoria ID {} encontrada. Vinculando ao produto SKU '{}'.", category.getId(), product.getSku());

        Product newProduct = new Product(
            product.getSku(), 
            product.getName(), 
            product.getDescription(), 
            product.getPrice(), 
            product.getQuantity(),
            new Category(category.getId(), category.getName(), category.getDescription()) 
        );
        
        Product savedProduct = productRepositoryPort.saveProduct(newProduct);

        log.info("Produto com SKU '{}' criado com sucesso. ID gerado: {}", savedProduct.getSku(), savedProduct.getId());

        return savedProduct;
    }
}
