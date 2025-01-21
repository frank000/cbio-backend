package com.cbio.app.configuration.keycloak;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://bot.rayzatec.com.br", "https://pleasing-elf-instantly.ngrok-free.app", "http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .exposedHeaders("Content-Disposition");
    }

}