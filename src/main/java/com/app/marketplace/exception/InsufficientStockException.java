package com.app.marketplace.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, int requested, int available) {
        super("Product with id " + productId + " not found");
    }
}
