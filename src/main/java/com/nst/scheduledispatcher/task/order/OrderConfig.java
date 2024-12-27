package com.nst.scheduledispatcher.task.order;

import com.nst.scheduledispatcher.component.JmsSender;
import com.nst.scheduledispatcher.repository.ScheduledDispatchRepository;
import com.nst.scheduledispatcher.task.CommonScheduledTask;
import com.nst.scheduledispatcher.task.SchedulableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class OrderConfig extends CommonScheduledTask {


    @Bean
    public SchedulableTask ordersTask(JmsSender jmsSender,
                                      OrderTaskProperties orderTaskProperties,
                                      ScheduledDispatchRepository scheduledDispatchRepository){
        return new OrderTask(jmsSender, orderTaskProperties, scheduledDispatchRepository);
    }



    @Bean(name = "ordersTaskScheduler")
    public ThreadPoolTaskScheduler ordersPoolTaskScheduler(SchedulableTask ordersTask,
                                                           ErrorHandler errorHandler,
                                                           ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureTasks){
        return setup(ordersTask, errorHandler, scheduledFutureTasks);
    }


}
