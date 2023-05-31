package com.example.kafka.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;

import java.text.SimpleDateFormat;
import java.util.Date;

@ApplicationScope
@RestController
@RequestMapping("/kafka/send")
public class MessagingBean {


    private final Logger LOG = LoggerFactory.getLogger(MessagingBean.class);

    @Autowired
    private final KafkaEventProducer kafkaEventProducer;
    private SimpleDateFormat formatter;
    private Integer counter;

    public MessagingBean(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        this.counter = 0;
    }


    @GetMapping("/integer")
    public void sendEventToTopic1(@RequestParam(name = "integer", required = false) String integer) {
        LOG.info("Sending Integer");
        int number;
        try {
            number = Integer.parseInt(integer);
        } catch (NumberFormatException | NullPointerException e) {
            number = ++counter;
        }
        kafkaEventProducer.sendToTopic1("Integer: " + number);
        LOG.info("Integer message sent");
    }

    @GetMapping("/date")
    public void sendEventToTopic2() {
        LOG.info("Sending Date message");
        kafkaEventProducer.sendToTopic2("Date of the message: " + formatter.format(new Date()));
        LOG.info("Date message sent");
    }
}
