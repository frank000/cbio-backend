package com.cbio.rasa.config;
import io.github.jrasa.ActionExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan("com.cbio.rasa.action")
public class JRasaConfig {
    @Bean
    public ActionExecutor actionExecutor() {
        return new ActionExecutor();
    }
}