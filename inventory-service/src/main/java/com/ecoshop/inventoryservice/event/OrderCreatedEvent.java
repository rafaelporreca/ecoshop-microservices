package com.ecoshop.inventoryservice.event;

import java.math.BigDecimal;

// Precisa ter os mesmos campos do OrderCreatedEvent do order-service
public record OrderCreatedEvent(Long orderId, Long userId, Long productId, Integer quantity, BigDecimal price) {}
