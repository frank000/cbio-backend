package com.cbio.chat.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set prefixes for the endpoint that the client listens for our messages from
        registry.enableSimpleBroker("/topic/", "/queue/")
                .setTaskScheduler(heartBeatScheduler())
                .setHeartbeatValue(new long[]{10000,10000});
        // Set prefix for endpoints the client will send messages to
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/public/websocket");

        // Registers the endpoint where the handshake will take place
        registry.addEndpoint("/ws")

                .setAllowedOrigins(
                        "https://atendimento190.policia.df.gov.br",
                        "https://atendimento190-hml.policia.df.gov.br",
                        "https://atendimento.policia.df.gov.br",
                        "https://app190df.rs.pm.df.gov.br/",
                        "http://localhost:4200/",
                        "http://localhost:4200/*",
                        "http://localhost:4200",
                        "http://localhost:8081/*",
                        "http://localhost:8081",
                        "http://localhost:8081/"
                ).withSockJS();
    }


}