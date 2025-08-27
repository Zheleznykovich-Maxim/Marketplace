package com.app.marketplace.service;

import com.app.marketplace.config.KafkaProperties;
import com.app.marketplace.domain.CustomerOrder;
import com.app.marketplace.domain.OutboxEvent;
import com.app.marketplace.domain.Product;
import com.app.marketplace.exception.InsufficientStockException;
import com.app.marketplace.exception.ProductNotfoundException;
import com.app.marketplace.repository.OrderRepository;
import com.app.marketplace.repository.OutboxEventRepository;
import com.app.marketplace.repository.ProductRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OutboxEventRepository outboxEventRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final KafkaProperties kafkaProperties;

    public OrderService(OutboxEventRepository outboxEventRepository, ProductRepository productRepository, OrderRepository orderRepository, KafkaProperties kafkaProperties) {
        this.outboxEventRepository = outboxEventRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.kafkaProperties = kafkaProperties;
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

        //Запись события в outbox
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("CustomerOrder");
        event.setAggregateId(order.getPublicId().toString());
        event.setEventType(kafkaProperties.getTopic().getOrderCreated());
        event.setPayload("{\"productId\" : " + productId + ", \"qty\" : " + qty + "}");
        outboxEventRepository.save(event);

        return order;
    }
}
