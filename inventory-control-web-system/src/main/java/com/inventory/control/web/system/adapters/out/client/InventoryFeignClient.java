package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.InventoryProductRequest;
import com.inventory.control.web.system.adapters.out.client.dto.InventoryProductResponse;
import com.inventory.control.web.system.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@FeignClient(
    name = "inventory-control-system", 
    url = "${application.feign.inventory-control-system.url",
    configuration = FeignConfig.class)
public interface InventoryFeignClient {

    @PostMapping("/v1/products")
    ResponseEntity<InventoryProductResponse> createProduct(@RequestBody InventoryProductRequest request);

    @GetMapping("/v1/products")
    ResponseEntity<List<InventoryProductResponse>> findAll();

    @GetMapping("/v1/products/{sku}")
    ResponseEntity<InventoryProductResponse> findBySku(@PathVariable("sku") String sku);
}