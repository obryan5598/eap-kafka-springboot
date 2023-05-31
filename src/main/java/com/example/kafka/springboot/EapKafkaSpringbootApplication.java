package com.example.kafka.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class EapKafkaSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(EapKafkaSpringbootApplication.class, args);
	}

}
