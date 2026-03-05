package com.ecoshop.paymentservice.event;

public record PaymentFailedEvent(Long orderId, String reason) {
}
