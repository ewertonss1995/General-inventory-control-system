package com.inventory.control.web.system.adapters.in.web;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    /**
     * @Cacheable intercepta a chamada. 
     * Se a chave "categories" já existir na memória, ele pula a execução do método 
     * (não chama o Feign) e devolve a lista direto da RAM do BFF.
     */
    @GetMapping
    @Cacheable(value = "categories")
    public ResponseEntity<List<String>> getCategories() {
        // Imagina que aqui haveria uma chamada para: productClient.listCategories();
        return ResponseEntity.ok(List.of("Eletrônicos", "Alimentos", "Vestuário"));
    }

    /**
     * @CacheEvict limpa a memória RAM de forma reativa.
     * Quando alguém cadastrar uma nova categoria, nós forçamos o BFF a limpar o cache 
     * para que a próxima listagem busque o dado atualizado direto do backend.
     */
    @PostMapping
    @CacheEvict(value = "categories", allEntries = true)
    public ResponseEntity<Void> createCategory(@RequestBody String newCategory) {
        return ResponseEntity.ok().build();
    }
}