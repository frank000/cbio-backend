package com.cbio.chat.config;


import com.fasterxml.jackson.databind.ObjectMapper;
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
        // Allow the origin http://localhost:63343 to send messages to us. (Base url of the client)
        //.setAllowedOrigins(appConfigProperties.getCorsOrigins().toArray(new String[0]))
        //https://github.com/spring-projects/spring-framework/issues/26111
//            .setAllowedOriginPatterns(appConfigProperties.getCorsOrigins().toArray(new String[0])); lugar pra por cors

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
                )
                // Allow the origin http://localhost:63343 to
                // send messages to us. (Base url of the client)
                //.setAllowedOrigins(appConfigProperties.getCorsOrigins().toArray(new String[0]))
                //https://github.com/spring-projects/spring-framework/issues/26111
//            .setAllowedOriginPatterns(appConfigProperties.getCorsOrigins().toArray(new String[0]))  lugar pra por cors
                // Enable SockJS fallback options
                .withSockJS();
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        // Add our interceptor for authentication/authorization
//        registration.interceptors(webSocketAuthChannelInterceptor);
//    }
}

//
//import com.cbio.chat.services.UserPresenceService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    private final int OUTBOUND_CHANNEL_CORE_POOL_SIZE = 8;
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableStompBrokerRelay("/topic/", "/queue/");
//        config.setApplicationDestinationPrefixes("/app");
//    }
//
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws").withSockJS();
//    }
////
////    @Bean
////    public UserPresenceService presenceChannelInterceptor() {
////        return new UserPresenceService();
////    }
////
////    @Override
////    public void configureClientInboundChannel(ChannelRegistration registration) {
////        registration.interceptors(presenceChannelInterceptor());
////    }
////
////    @Override
////    public void configureClientOutboundChannel(ChannelRegistration registration) {
////        registration.taskExecutor().corePoolSize(OUTBOUND_CHANNEL_CORE_POOL_SIZE);
////        registration.interceptors(presenceChannelInterceptor());
////    }
//
//
//}
