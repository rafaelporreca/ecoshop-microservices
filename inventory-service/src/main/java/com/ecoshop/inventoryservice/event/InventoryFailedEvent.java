package com.ecoshop.inventoryservice.event;

public record InventoryFailedEvent(Long orderId, String reason) {
}
