package com.library.tanda.tandadarajaintegration.Kafka.Service;

import com.library.tanda.tandadarajaintegration.Data.Result;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Result> kafkaTemplate;
    private final String topic = "cps-topic";
    private final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    public void sendResult(Result result) {
        try {
            kafkaTemplate.send(topic, result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}

