package com.cbio.app.configuration.keycloak;

import com.cbio.app.configuration.JWTConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita proteção CSRF
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new JWTConverter())));



        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/unauthenticated", "/oauth2/**", "/v1/login/**", "/v1/autenticacao/login", "/v1/publico/**").permitAll()
                .requestMatchers("/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html").permitAll()
                .requestMatchers("/v1/login").permitAll()

                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/v1/bot/**").permitAll()
                .requestMatchers("/v1/action-server/**").permitAll()
                .requestMatchers("/v1/login/**").permitAll()
                .requestMatchers("/v1/login/logout").permitAll()
                .requestMatchers("/v1/aplicativo/**").permitAll()
                .requestMatchers("/v1/endereco-app/**").permitAll()
                .requestMatchers("/public/stomp/**").permitAll()
                .requestMatchers("/public/websocket/**").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/v1/whatsapp/**").permitAll()
                .requestMatchers("/v1/public/**").permitAll()
                .requestMatchers("/v1/google-calendar/**").permitAll()
                .requestMatchers("/v1/payment/**").permitAll()
                .anyRequest()
                .authenticated());


        // Configuração de CORS
        http.cors(cors -> cors
                .configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.addAllowedOrigin("https://bot.rayzatec.com.br");  // Adicione seu domínio aqui
                    config.addAllowedOrigin("https://pleasing-elf-instantly.ngrok-free.app");  // Domínio para teste
                    config.addAllowedOrigin("http://localhost:4200");  // Domínio local

                    config.addAllowedMethod("*");
                    config.addAllowedHeader("*");
                    config.setAllowCredentials(true);  // Permite cookies em requisições cross-origin

                    return config;
                })
        );

        // Configuração de sessão e cookies
        http.sessionManagement(sessionCustomizer ->
                sessionCustomizer
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Cria a sessão somente quando necessário
        );
        // Configuração para garantir cookies com SameSite=None, Secure e HttpOnly
        http.sessionManagement(sessionCustomizer -> sessionCustomizer
                .sessionFixation().none() // Evita alteração do ID da sessão após o login
        );

//        http.cors(Customizer.withDefaults());
        return http.build();

    }

}
