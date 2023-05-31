# eap-kafka-springboot

The application shows how to connect a Spring Boot application deployed on JBoss EAP to Apache Kafka.
This basic application sends events to specific topics when dedicated REST endpoints are invoked.
This reproducer is by no means extensive of both Spring Boot & Apache Kafka features.
Please refer to Spring Boot documentation for all [*spring-kafka* properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#:~:text=spring.kafka.admin.auto%2Dcreate).


The following code has been built using:
- Apache Maven 3.9.1
- Java openjdk version "17.0.5"
- EAP 7.4.10

First you need to enable support for JDK 17:
```
sh /path/to/EAP/installation/jboss-eap-7.4/bin/jboss-cli.sh --file=/path/to/EAP/installation/jboss-eap-7.4/docs/examples/enable-elytron-se17.cli -Djboss.server.config.dir=/path/to/eap_kafka_instance/configuration -Dconfig=standalone-microprofile.xml
```


Then launch the standalone server:

```
sh /path/to/EAP/installation/jboss-eap-7.4/bin/standalone.sh -Djboss.server.base.dir=/path/to/eap_kafka_instance -c standalone-full-ha.xml
```

Once the server has been launched, please enable useful logging and proper extensions via EAP CLI:

```
batch
/system-property=KAFKA_BOOTSTRAP_SERVERS_SYSPROP:add(value="localhost:9092")
/system-property=KAFKA_TOPIC1_SYSPROP:add(value="myTopic1")
/system-property=KAFKA_TOPIC2_SYSPROP:add(value="myTopic2")
reload
```

In a new terminal window, launch Kafka typing:

``` 
docker-compose up
```

and wait for the Kafka instance to be up.


Launch Maven to build and deploy the application:

```
mvn clean package wildfly:deploy
```

Application has two topics to publish/consume from:

- myTopic1 (set as system property) which contains events as integers
- myTopic2 (set as system property as well) which contains events as dates


To send an event of type Integer to the Kafka topic *myTopic1*, please execute the following as an example:


```
http :8080/eap-kafka-springboot-1.0.0/kafka/send/integer?integer=15
```

parameter *integer=15* is the integer which is going to be published as event.
The following output on EAP log should appear:

```
11:59:15,876 INFO  [org.springframework.kafka.listener.KafkaMessageListenerContainer] (org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1) integer-group: partitions assigned: [myTopic1-0]
...
12:01:26,806 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Sending Integer
12:01:26,807 INFO  [com.example.kafka.springboot.KafkaEventProducer] (default task-1) Sending to topic1 message: Integer: 15
12:01:26,824 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Integer message sent
12:01:26,887 INFO  [com.example.kafka.springboot.KafkaEventConsumer] (org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1) consumeFromTopic1 [Integer: 15]
```

Since *integer* is an optional parameter, an incremental value will be sent. By typing twice:

```
http :8080/eap-kafka-springboot-1.0.0/kafka/send/integer
http :8080/eap-kafka-springboot-1.0.0/kafka/send/integer
```

Then the output on EAP server.log would be:

```
12:02:30,851 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Sending Integer
12:02:30,852 INFO  [com.example.kafka.springboot.KafkaEventProducer] (default task-1) Sending to topic1 message: Integer: 1
12:02:30,852 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Integer message sent
12:02:30,965 INFO  [com.example.kafka.springboot.KafkaEventConsumer] (org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1) consumeFromTopic1 [Integer: 1]
12:02:40,314 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Sending Integer
12:02:40,314 INFO  [com.example.kafka.springboot.KafkaEventProducer] (default task-1) Sending to topic1 message: Integer: 2
12:02:40,314 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Integer message sent
12:02:40,335 INFO  [com.example.kafka.springboot.KafkaEventConsumer] (org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1) consumeFromTopic1 [Integer: 2]
```

The same incremental value happens with wrong integers as well, such as:
```
http :8080/eap-kafka-springboot-1.0.0/kafka/send/integer?integer=wrongValue
```

getting as output:

```
12:04:51,540 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Sending Integer
12:04:51,540 INFO  [com.example.kafka.springboot.KafkaEventProducer] (default task-1) Sending to topic1 message: Integer: 3
12:04:51,541 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Integer message sent
12:04:51,561 INFO  [com.example.kafka.springboot.KafkaEventConsumer] (org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1) consumeFromTopic1 [Integer: 3]
```



To send an event of type Date to the Kafka topic *myTopic2*, please execute:

```
http :8080/eap-kafka-springboot-1.0.0/kafka/send/date
```

which will send the current timestamp as an event.
The following output on EAP log should appear:

```
11:58:50,216 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Sending Date message
11:58:50,216 INFO  [com.example.kafka.springboot.KafkaEventProducer] (default task-1) Sending to topic2 message: Date of the message: 2023-05-31 11:58:50+0200
...
11:58:50,275 INFO  [com.example.kafka.springboot.MessagingBean] (default task-1) Date message sent
...
11:59:15,876 INFO  [org.springframework.kafka.listener.KafkaMessageListenerContainer] (org.springframework.kafka.KafkaListenerEndpointContainer#1-0-C-1) date-group: partitions assigned: [myTopic2-0]
...
11:59:16,090 INFO  [com.example.kafka.springboot.KafkaEventConsumer] (org.springframework.kafka.KafkaListenerEndpointContainer#1-0-C-1) consumeFromTopic2 [Date of the message: 2023-05-31 11:58:50+0200]
```
