package com.app.marketplace.service;

import com.app.marketplace.domain.OutboxEvent;
import com.app.marketplace.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class.getName());

    public OutboxPublisher(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void publishEvents() {
        log.info("Publishing events");
        List<OutboxEvent> events = outboxEventRepository.findByProcessedFalse();
        for (OutboxEvent event : events) {
            kafkaTemplate.send(event.getEventType(), event.getPayload());
            event.setProcessed(true);
            outboxEventRepository.save(event);
        }
    }
}
