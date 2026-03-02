package com.ecoshop.inventoryservice.config;

import com.ecoshop.inventoryservice.model.Inventory;
import com.ecoshop.inventoryservice.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSetup {

    private final InventoryRepository repository;

    @PostConstruct
    public void seedData() {
        if (repository.count() == 0) {
            // Cria um produto de ID 99 (o mesmo que você usou no Postman antes) com 100 itens no estoque
            repository.save(Inventory.builder().productId(99L).stockQuantity(100).build());
            System.out.println("Produto ID 99 inserido no estoque com 100 unidades.");
        }
    }
}
