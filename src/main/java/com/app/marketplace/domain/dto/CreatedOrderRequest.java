package com.app.marketplace.domain.dto;

public record CreatedOrderRequest(
        Long productId,
        int qty
) {
}
