package com.app.marketplace.controller;

import com.app.marketplace.domain.CustomerOrder;
import com.app.marketplace.domain.dto.CreatedOrderRequest;
import com.app.marketplace.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreatedOrderRequest request) {
        CustomerOrder order = orderService.createOrder(request.productId(), request.qty());
        return ResponseEntity.status(201).body(order.getPublicId());
    }
}
