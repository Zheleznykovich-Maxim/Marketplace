package com.app.marketplace.exception;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException(Long key) {
        super("Not enough stock for product " + key);
    }
}
