package com.inventory.control.system.domain.service;

import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.UpdateStockInput;
import com.inventory.control.system.ports.in.UpdateStockUseCase;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateStockService implements UpdateStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateStockService.class);

    private final ProductRepositoryPort productRepositoryPort;

    public UpdateStockService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public Product execute(UpdateStockInput input) {
        String formattedSku = input.sku() != null ? input.sku().trim().toUpperCase() : null;

        log.info("Iniciando movimentação de estoque. SKU: {} | Tipo: {} | Quantidade: {}", 
                formattedSku, input.movementType(), input.quantity());

        Product product = productRepositoryPort.findBySku(formattedSku)
                .orElseThrow(() -> {
                    log.warn("Falha na atualização de estoque: Produto SKU '{}' não foi encontrado.", formattedSku);
                    return new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
                });

        int previousQuantity = product.getQuantity();

        switch (input.movementType()) {
            case IN -> {
                log.debug("Processando ENTRADA de estoque. Saldo anterior: {} | Adicionando: {}", previousQuantity, input.quantity());
                product.addStock(input.quantity());
            }
            case OUT -> {
                log.debug("Processando SAÍDA de estoque. Saldo anterior: {} | Subtraindo: {}", previousQuantity, input.quantity());
                product.removeStock(input.quantity());
            }
        }

        Product updatedProduct = productRepositoryPort.updateProduct(product);

        log.info("Movimentação de estoque concluída com sucesso. SKU: {} | Saldo anterior: {} | Novo saldo: {}", 
                formattedSku, previousQuantity, updatedProduct.getQuantity());

        return updatedProduct;
    }
}
