package com.kk.klist.domain.chat.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ChatbotClientConfig {

    @Bean
    public RestClient chatbotRestClient(
            @Value("${chatbot.base-url}") String baseUrl,
            @Value("${chatbot.timeout-ms}") long timeoutMs
    ) {
        Duration timeout = Duration.ofMillis(timeoutMs);
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
