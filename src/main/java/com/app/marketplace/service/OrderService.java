package com.app.marketplace.service;

import com.app.marketplace.domain.CustomerOrder;
import com.app.marketplace.domain.Product;
import com.app.marketplace.exception.InsufficientStockException;
import com.app.marketplace.exception.ProductNotfoundException;
import com.app.marketplace.repository.OrderRepository;
import com.app.marketplace.repository.ProductRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public CustomerOrder createOrder(Long productId, int qty) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotfoundException(productId));

        if (product.getStock() < qty) {
            throw new InsufficientStockException(productId, qty, product.getStock());
        }

        product.setStock(product.getStock() - qty);
        productRepository.save(product);

        CustomerOrder order = new CustomerOrder();
        order.setProductId(productId);
        order.setQty(qty);
        order.setStatus("CREATED");
        orderRepository.save(order);

        kafkaTemplate.send("order.created", order.getPublicId().toString());

        return order;
    }
}
