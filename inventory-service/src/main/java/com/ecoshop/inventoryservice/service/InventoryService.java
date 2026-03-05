package com.ecoshop.inventoryservice.service;

import com.ecoshop.inventoryservice.event.InventoryFailedEvent;
import com.ecoshop.inventoryservice.event.InventoryReservedEvent;
import com.ecoshop.inventoryservice.event.OrderCreatedEvent;
import com.ecoshop.inventoryservice.model.Inventory;
import com.ecoshop.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate; // Injetado para enviar mensagens

    // A mágica acontece aqui: O Spring fica escutando esse tópico
    @KafkaListener(topics = "orders-topic", groupId = "inventory-group")
    @Transactional
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Mensagem recebida do Kafka! Processando pedido ID: {}", event.orderId());

        // 1. Busca o produto no estoque
        Optional<Inventory> inventoryOpt  = inventoryRepository.findByProductId(event.productId());

        if(inventoryOpt.isEmpty()){
            log.error("Produto {} não encontrado para o pedido {}.", event.productId(), event.orderId());
            kafkaTemplate.send("inventory-failed-topic", String.valueOf(event.orderId()),
                    new InventoryFailedEvent(event.orderId(), "Produto não existe no catálogo"));
            return;
        }

        Inventory inventory = inventoryOpt.get();

        // 2. Verifica se tem quantidade suficiente
        if (inventory.getStockQuantity() >= event.quantity()) {
            // Deduz o estoque
            inventory.setStockQuantity(inventory.getStockQuantity() - event.quantity());
            inventoryRepository.save(inventory);

            log.info("Estoque reservado com sucesso! Saldo atual do produto {}: {}",
                    event.productId(), inventory.getStockQuantity());

            // Avisa o Order Service que correu tudo bem
            kafkaTemplate.send("inventory-success-topic", String.valueOf(event.orderId()),
                    new InventoryReservedEvent(event.orderId()));

        } else {
            log.error("Estoque INSUFICIENTE para o pedido {}. Produto: {}", event.orderId(), event.productId());

            // Avisa o Order Service para cancelar (Rollback)
            kafkaTemplate.send("inventory-failed-topic", String.valueOf(event.orderId()),
                    new InventoryFailedEvent(event.orderId(), "Stock insuficiente"));
        }
    }
}
