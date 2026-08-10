package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductRequest;
import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductStockRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventorySaveProductResponse;
import com.inventory.control.web.system.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(
    name = "api-core-client", 
    url = "${application.feign.inventory-control-system.url}",
    configuration = FeignConfig.class)
public interface InventoryFeignClient {

    @PostMapping("/v1/products/save")
    ResponseEntity<InventorySaveProductResponse> createProduct(@RequestBody InventoryProductRequest request);

    @PutMapping("/v1/products/update/{sku}")
    ResponseEntity<InventorySaveProductResponse> updateProduct(@PathVariable("sku") String sku, @RequestBody InventoryProductRequest request);

    @GetMapping("/v1/products")
    ResponseEntity<List<InventoryProductResponse>> getAllProducts();

    @GetMapping("/v1/products/{sku}")
    ResponseEntity<InventoryProductResponse> getProductBySku(@PathVariable("sku") String sku);

    @PatchMapping("/v1/products/{sku}/stock")
    ResponseEntity<InventoryProductResponse> updateProductStock(@PathVariable("sku") String sku, @RequestBody InventoryProductStockRequest request);
}
