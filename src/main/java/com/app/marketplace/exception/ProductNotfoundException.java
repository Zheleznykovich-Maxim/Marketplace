package com.app.marketplace.exception;

public class ProductNotfoundException extends RuntimeException {
    public ProductNotfoundException(Long productId) {
      super("Product with id " + productId + " not found");
    }
}
