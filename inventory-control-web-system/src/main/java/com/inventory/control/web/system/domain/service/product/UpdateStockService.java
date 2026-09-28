package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.ports.in.product.UpdateStockUseCase;
import com.inventory.control.web.system.ports.out.ProductFeignPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateStockService implements UpdateStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateStockService.class);

    private final ProductFeignPort productFeignPort;

    public UpdateStockService(ProductFeignPort productFeignPort) {
        this.productFeignPort = productFeignPort;
    }

    @Override
    public UpdateStock execute(String sku, UpdateStockInput input) {
        String skuFormatted = sku != null ? sku.trim().toUpperCase() : null;

        log.info("Iniciando movimentação de estoque. SKU: {} | Tipo: {} | Quantidade: {}", 
                skuFormatted, input.movementType(), input.quantity());

        UpdateStock updatedStock = productFeignPort.updateProductStock(skuFormatted, input);

        log.info("Movimentação de estoque concluída com sucesso. SKU: {} | Novo saldo: {}", 
                skuFormatted, updatedStock.newQuantity());

        return updatedStock;
    }
}
