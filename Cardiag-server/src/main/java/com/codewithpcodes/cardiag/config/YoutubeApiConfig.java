package com.codewithpcodes.cardiag.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
public class YoutubeApiConfig {

    @Value("${application.youtube.api.key}")
    private String apiKey;

    @Value("${application.youtube.api.base-url}")
    private String baseUrl;

    @Bean
    public WebClient youtubeWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(appendApiKey(apiKey))
                .build();
    }

    private ExchangeFilterFunction appendApiKey(String key) {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            URI newUri = UriComponentsBuilder.fromUriString(request.url().toString())
                    .queryParam("key", key)
                    .build()
                    .toUri();

            ClientRequest updatedRequest = ClientRequest.from(request)
                    .url(newUri)
                    .build();

            return Mono.just(updatedRequest);
        });
    }
}
