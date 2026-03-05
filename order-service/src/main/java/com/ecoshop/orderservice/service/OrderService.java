package com.ecoshop.orderservice.service;

import com.ecoshop.orderservice.dto.OrderRequest;
import com.ecoshop.orderservice.event.*;
import com.ecoshop.orderservice.model.Order;
import com.ecoshop.orderservice.model.OrderStatus;
import com.ecoshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Nome do tópico onde vamos gritar que um pedido foi criado
    private static final String TOPIC = "orders-topic";

    @Transactional
    public Long createOrder(OrderRequest request) {
        // 1. Salva no banco como PENDENTE
        Order order = Order.builder()
                .userId(request.userId())
                .productId(request.productId())
                .quantity(request.quantity())
                .price(request.price())
                .status(OrderStatus.PENDING) // Início da Saga
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Pedido {} salvo no banco com status PENDING.", savedOrder.getId());

        // 2. Monta o evento
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getPrice()
        );

        // 3. Envia para o Kafka
        kafkaTemplate.send(TOPIC, String.valueOf(savedOrder.getId()), event);
        log.info("Evento OrderCreatedEvent enviado para o Kafka no tópico: {}", TOPIC);

        return savedOrder.getId();
    }

    // 1. O ESTOQUE DEU CERTO (Apenas loga, não aprova o pedido ainda)
    @KafkaListener(topics = "inventory-success-topic", groupId = "order-group")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Estoque reservado para o pedido {}. Aguardando processamento do pagamento...", event.orderId());
    }

    // 2. O ESTOQUE FALHOU (Rejeita o pedido direto)
    @KafkaListener(topics = "inventory-failed-topic", groupId = "order-group")
    @Transactional
    public void handleInventoryFailed(InventoryFailedEvent event) {
        log.error("Estoque falhou para o pedido {}: {}. Cancelando pedido.", event.orderId(), event.reason());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        order.setStatus(OrderStatus.REJECTED);
        orderRepository.save(order);
    }

    // 3. O PAGAMENTO DEU CERTO (Aprovação Final!)
    @KafkaListener(topics = "payment-success-topic", groupId = "order-group")
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Pagamento confirmado para o pedido: {}", event.orderId());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.setStatus(OrderStatus.APPROVED);
        orderRepository.save(order);

        log.info("SAGA CONCLUÍDA! Pedido {} 100% aprovado.", order.getId());
    }

    // 4. O PAGAMENTO FALHOU (Rejeita o pedido)
    @KafkaListener(topics = "payment-failed-topic", groupId = "order-group")
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.error("Pagamento recusado para o pedido {}: {}", event.orderId(), event.reason());
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.setStatus(OrderStatus.REJECTED);
        orderRepository.save(order);

        // NOTA DE ARQUITETURA: Em um sistema real de produção, aqui nós enviaríamos
        // uma mensagem para um tópico como "inventory-rollback-topic" para o
        // Inventory Service devolver os itens para a prateleira!
    }

}
