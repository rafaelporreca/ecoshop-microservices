package com.ecoshop.orderservice.service;

import com.ecoshop.orderservice.dto.OrderRequest;
import com.ecoshop.orderservice.event.OrderCreatedEvent;
import com.ecoshop.orderservice.model.Order;
import com.ecoshop.orderservice.model.OrderStatus;
import com.ecoshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
