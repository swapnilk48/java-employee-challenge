package com.reliaquest.api.config;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${urls.employee_server_base_url}")
    private String baseUrl;

    @Bean
    public WebClient employeeWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

}
