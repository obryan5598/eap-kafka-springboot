package com.example.kafka.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConsumer {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEventConsumer.class);
    private final String topic1;
    private final String topic2;

    @Autowired
    public KafkaEventConsumer(@Value("${kafka.topic1}") String topic1,
                              @Value("${kafka.topic2}") String topic2) {
        this.topic1 = topic1;
        this.topic2 = topic2;
    }

    @KafkaListener(topics = "${kafka.topic1}", groupId = "integer-group")
    public void consumeFromTopic1(String message) {
        LOG.info("consumeFromTopic1 [{}]", message);
    }


    @KafkaListener(topics = "${kafka.topic2}", groupId = "date-group")
    public void consumeFromTopic2(String message) {
        LOG.info("consumeFromTopic2 [{}]", message);
    }

}
