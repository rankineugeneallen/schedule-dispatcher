package com.nst.scheduledispatcher.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class CommonTaskConfig {

    @Bean(name = "scheduledFutureTasks")
    public ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureTasks(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ErrorHandler taskErrorHandler(){
        return new TaskErrorHandler();
    }

}

