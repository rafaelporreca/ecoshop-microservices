package com.ecoshop.inventoryservice.service;

import com.ecoshop.inventoryservice.event.OrderCreatedEvent;
import com.ecoshop.inventoryservice.model.Inventory;
import com.ecoshop.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // A mágica acontece aqui: O Spring fica escutando esse tópico
    @KafkaListener(topics = "orders-topic", groupId = "inventory-group")
    @Transactional
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Mensagem recebida do Kafka! Processando pedido ID: {}", event.orderId());

        // 1. Busca o produto no estoque
        Inventory inventory = inventoryRepository.findByProductId(event.productId())
                .orElseThrow(() -> new RuntimeException("Produto não cadastrado no estoque!"));

        // 2. Verifica se tem quantidade suficiente
        if (inventory.getStockQuantity() >= event.quantity()) {
            // Deduz o estoque
            inventory.setStockQuantity(inventory.getStockQuantity() - event.quantity());
            inventoryRepository.save(inventory);

            log.info("Estoque reservado com sucesso! Saldo atual do produto {}: {}",
                    event.productId(), inventory.getStockQuantity());

            // TODO: (Próxima fase) Enviar mensagem para o tópico "inventory-reserved-topic"
            // para avisar o serviço de pagamento.
        } else {
            log.error("Estoque INSUFICIENTE para o pedido {}. Produto: {}", event.orderId(), event.productId());

            // TODO: (Próxima fase) Enviar mensagem para o tópico "inventory-failed-topic"
            // para o Order Service cancelar o pedido (Rollback da Saga).
        }
    }
}
