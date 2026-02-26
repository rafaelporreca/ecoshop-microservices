package com.ecoshop.orderservice.dto;

import java.math.BigDecimal;

public record OrderRequest(Long userId, Long productId, Integer quantity, BigDecimal price) {
}
