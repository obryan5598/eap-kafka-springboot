package com.example.kafka.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEventProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic1;
    private final String topic2;

    @Autowired
    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                              @Value("${kafka.topic1}") String topic1,
                              @Value("${kafka.topic2}") String topic2) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic1 = topic1;
        this.topic2 = topic2;
    }

    public void sendToTopic1(String message) {
        LOG.info("Sending to topic1 message: " + message);
        kafkaTemplate.send(topic1, message);
    }


    public void sendToTopic2(String message) {
        LOG.info("Sending to topic2 message: " + message);
        kafkaTemplate.send(topic2, message);
    }
}
