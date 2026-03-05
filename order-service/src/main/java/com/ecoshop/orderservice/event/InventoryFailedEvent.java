package com.ecoshop.orderservice.event;

public record InventoryFailedEvent(Long orderId, String reason) {
}
