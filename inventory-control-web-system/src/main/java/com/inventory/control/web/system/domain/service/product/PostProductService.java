package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostProductService implements PostProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(PostProductService.class);

    private final ProductFeignPort productFeignPort;
    private final CategoryFeignPort categoryFeignPort;

    public PostProductService(ProductFeignPort productFeignPort, CategoryFeignPort categoryFeignPort) {
        this.productFeignPort = productFeignPort;
        this.categoryFeignPort = categoryFeignPort;
    }

    @Override
    public Product execute(Product product) {
        // if (product.getCategory() == null || product.getCategory().getId() == null) {
        //     log.warn("Falha ao criar produto SKU '{}': Categoria ou ID da categoria é nulo.", product.getSku());
        //     throw new BusinessException("É necessário informar uma categoria válida para o produto.");
        // }

        // log.info("Iniciando processo de criação de produto. SKU: {} | Categoria ID: {}", 
        //         product.getSku(), product.getCategory().getId());

        // if (productRepositoryPort.existsBySku(product.getSku())) {
        //     log.warn("Falha ao criar produto: SKU '{}' já está cadastrado no sistema.", product.getSku());
        //     throw new BusinessException("SKU já cadastrado: " + product.getSku());
        // }

        // Category category = categoryRepositoryPort.findById(product.getCategory().getId())
        //         .orElseThrow(() -> {
        //             log.warn("Falha ao criar produto SKU '{}': Categoria ID {} não encontrada.", 
        //                     product.getSku(), product.getCategory().getId());
        //             return new ResourceNotFoundException("Categoria não encontrada com o ID: " + product.getCategory().getId());
        //         });

        // log.debug("Categoria ID {} encontrada. Vinculando ao produto SKU '{}'.", category.getId(), product.getSku());

        // Product newProduct = new Product(
        //     product.getSku(), 
        //     product.getName(), 
        //     product.getDescription(), 
        //     product.getPrice(), 
        //     product.getQuantity(),
        //     new Category(category.getId(), category.getName(), category.getDescription()) 
        // );
        
        // Product savedProduct = productRepositoryPort.saveProduct(newProduct);

        // log.info("Produto com SKU '{}' criado com sucesso. ID gerado: {}", savedProduct.getSku(), savedProduct.getId());

        return null;
    }
}
