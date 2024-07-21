package com.library.tanda.tandadarajaintegration;

import com.library.tanda.tandadarajaintegration.Data.Result;
import com.library.tanda.tandadarajaintegration.Kafka.Service.KafkaProducerService;
import com.library.tanda.tandadarajaintegration.Service.OAuthService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.web.client.RestTemplate;

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
