package com.library.tanda.tandadarajaintegration;

import com.library.tanda.tandadarajaintegration.Data.Result;
import com.library.tanda.tandadarajaintegration.Kafka.Service.KafkaProducerService;
import com.library.tanda.tandadarajaintegration.Service.OAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@Configuration
public class TestSetup {
    @Bean
    public OAuthService oAuthService() {
        return mock(OAuthService.class);
    }

    @Bean
    public KafkaTemplate<String, Result> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    @Bean
    public KafkaProducerService kafkaProducerService(KafkaTemplate<String, Result> kafkaTemplate) {
        return new KafkaProducerService(kafkaTemplate);
    }
}
