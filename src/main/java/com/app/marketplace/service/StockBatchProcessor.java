package com.app.marketplace.service;

import com.app.marketplace.domain.CustomerOrder;
import com.app.marketplace.exception.NotEnoughStockException;
import com.app.marketplace.repository.OrderRepository;
import com.app.marketplace.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockBatchProcessor {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public StockBatchProcessor(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedRate = 100)
    @Transactional
    public void processBatch() {
        List<CustomerOrder> pendingOrders = orderRepository.findByStatus("PENDING");
        if (pendingOrders.isEmpty()) {
            return;
        }

        // Группировка по productId и суммируем qty
        Map<Long, Integer> productQtyMap = pendingOrders.stream()
                .collect(Collectors.groupingBy(CustomerOrder::getProductId,
                        Collectors.summingInt(CustomerOrder::getQty)));

        // Списываем stock батчем
        for (Map.Entry<Long, Integer> entry : productQtyMap.entrySet()) {
            int updated = productRepository.decrementStock(entry.getKey(), entry.getValue());
            if (updated < entry.getValue()) {
                // обработка
                throw new NotEnoughStockException(entry.getKey());
            }
        }

        pendingOrders.forEach(order -> order.setStatus("CREATED"));
        orderRepository.saveAll(pendingOrders);
    }
}
