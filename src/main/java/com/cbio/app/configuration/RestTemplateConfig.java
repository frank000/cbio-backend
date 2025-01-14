package com.cbio.app.configuration;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException {
        // Configuração de timeout
        int connectTimeout = 30000;  // 5 segundos
        int readTimeout = 45000;    // 10 segundos



        // Configuração de timeouts usando ConnectionConfig.Builder
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))  // Timeout de conexão
                .setSocketTimeout(Timeout.ofMilliseconds(readTimeout))     // Timeout de resposta
                .build();

        // Criação do RequestConfig
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectTimeout))  // Timeout para requisição de conexão
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeout))              // Timeout para resposta
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);  // Máximo de 100 conexões
        connectionManager.setDefaultMaxPerRoute(20);

        // Configuração do HttpClient
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();

        // Retorno do RestTemplate com a fábrica de HttpClient configurada
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}
