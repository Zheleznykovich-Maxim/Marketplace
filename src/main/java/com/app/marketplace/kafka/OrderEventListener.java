package com.app.marketplace.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;



@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class.getName());

    @KafkaListener(topics = "${kafka.topic.order-created}", groupId = "order-service")
    public void onOrderCreated(ConsumerRecord<String, String> record) {
        log.info("Recieved order.created event: key={}, value={}", record.key(), record);
    }
}
