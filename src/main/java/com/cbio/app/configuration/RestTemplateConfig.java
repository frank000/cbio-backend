package com.cbio.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

@Configuration
public class RestTemplateConfig {
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .defaultHeaders(new Consumer<HttpHeaders>() {
                    @Override
                    public void accept(HttpHeaders httpHeaders) {
                        // https://github.com/spring-projects/spring-ai/issues/372
                        httpHeaders.set("Accept-Encoding", "gzip, deflate");
                    }
                });
    }
}
