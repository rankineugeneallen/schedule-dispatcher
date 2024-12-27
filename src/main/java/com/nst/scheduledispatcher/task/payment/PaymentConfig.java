package com.nst.scheduledispatcher.task.payment;

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
public class PaymentConfig extends CommonScheduledTask {

    @Bean
    public SchedulableTask paymentTask(JmsSender jmsSender,
                                       PaymentTaskProperties paymentTaskProperties,
                                       ScheduledDispatchRepository scheduledDispatchRepository){
        return new PaymentTask(jmsSender, paymentTaskProperties, scheduledDispatchRepository);
    }

    @Bean(name = "paymentTaskScheduler")
    public ThreadPoolTaskScheduler paymentPoolTaskScheduler(SchedulableTask paymentTask,
                                                            ErrorHandler errorHandler,
                                                            ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureTasks){
        return setup(paymentTask, errorHandler, scheduledFutureTasks);
    }

}
