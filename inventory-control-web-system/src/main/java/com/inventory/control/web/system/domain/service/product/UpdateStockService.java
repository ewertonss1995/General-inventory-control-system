package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.ports.in.product.UpdateStockUseCase;
import com.inventory.control.web.system.ports.out.ProductFeignPort;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateStockService implements UpdateStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateStockService.class);

    private final ProductFeignPort productFeignPort;

    public UpdateStockService(ProductFeignPort productFeignPort) {
        this.productFeignPort = productFeignPort;
    }

    @Override
    public Product execute(UpdateStockInput input) {
        // String formattedSku;
        
        // if(!Objects.isNull(input.sku())) {
        //     formattedSku = input.sku().trim().toUpperCase();
        // } else {
        //     log.warn("Falha na atualização de estoque: SKU fornecido é nulo ou vazio.");
        //     throw new IllegalArgumentException("SKU não pode ser nulo ou vazio.");
        // }

        // log.info("Iniciando movimentação de estoque. SKU: {} | Tipo: {} | Quantidade: {}", 
        //         formattedSku, input.movementType(), input.quantity());

        // Product product = productFeignPort.findBySku(formattedSku)
        //         .orElseThrow(() -> {
        //             log.warn("Falha na atualização de estoque: Produto SKU '{}' não foi encontrado.", formattedSku);
        //             return new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
        //         });

        // int previousQuantity = product.getQuantity();

        // switch (input.movementType()) {
        //     case IN -> {
        //         log.debug("Processando ENTRADA de estoque. Saldo anterior: {} | Adicionando: {}", previousQuantity, input.quantity());
        //         product.addStock(input.quantity());
        //     }
        //     case OUT -> {
        //         log.debug("Processando SAÍDA de estoque. Saldo anterior: {} | Subtraindo: {}", previousQuantity, input.quantity());
        //         product.removeStock(input.quantity());
        //     }
        // }

        // Product updatedProduct = productFeignPort.updateProduct(product);

        // log.info("Movimentação de estoque concluída com sucesso. SKU: {} | Saldo anterior: {} | Novo saldo: {}", 
        //         formattedSku, previousQuantity, updatedProduct.getQuantity());

        return null;
    }
}
