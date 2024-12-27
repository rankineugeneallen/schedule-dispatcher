package com.nst.scheduledispatcher.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public abstract class CommonScheduledTask {

    public ThreadPoolTaskScheduler setup(SchedulableTask scheduledTask, ErrorHandler errorHandler, ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureTasks){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        String threadPrefix = scheduledTask.getTaskProperties().getThreadPrefix();
        CronTrigger trigger = new CronTrigger(scheduledTask.getTaskProperties().getClock(), scheduledTask.getTaskProperties().getZone());

        scheduler.initialize();
        scheduler.setThreadNamePrefix(threadPrefix);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setErrorHandler(errorHandler);
        ScheduledFuture<?> scheduledFutureTask = scheduler.schedule(scheduledTask, trigger);

        scheduledFutureTasks.put(threadPrefix, scheduledFutureTask);

        return scheduler;
    }

}
