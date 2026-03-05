package com.ecoshop.orderservice.event;

public record PaymentFailedEvent(Long orderId, String reason) {
}
