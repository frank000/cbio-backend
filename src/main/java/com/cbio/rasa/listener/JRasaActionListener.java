package com.cbio.rasa.listener;

import io.github.jrasa.Action;
import io.github.jrasa.ActionExecutor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class JRasaActionListener implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ConfigurableListableBeanFactory beanFactory;

    @Resource
    private ActionExecutor actionExecutor;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        beanFactory.getBeansOfType(Action.class).values().forEach(
                action -> actionExecutor.registerAction(action)
        );
    }
}