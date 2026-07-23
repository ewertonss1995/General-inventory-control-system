package com.inventory.control.system.domain.service;

import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.enums.StockMovementType;
import com.inventory.control.system.domain.model.UpdateStockInput;
import com.inventory.control.system.ports.in.UpdateStockUseCase;
import com.inventory.control.system.ports.out.ProductRepositoryPort;

public class UpdateStockService implements UpdateStockUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public UpdateStockService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public Product execute(UpdateStockInput input) {
        Product product = productRepositoryPort.findBySku(input.sku())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado para o SKU: " + input.sku()));

        if (input.movementType() == StockMovementType.IN) {
            product.addStock(input.quantity());
        } else if (input.movementType() == StockMovementType.OUT) {
            product.removeStock(input.quantity());
        }

        return productRepositoryPort.save(product);
    }
}
