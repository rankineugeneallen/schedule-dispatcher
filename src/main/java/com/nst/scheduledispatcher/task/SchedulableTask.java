package com.nst.scheduledispatcher.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nst.scheduledispatcher.model.DispatchRecord;
import com.nst.scheduledispatcher.model.TaskMessage;
import com.nst.scheduledispatcher.repository.ScheduledDispatchRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

public abstract class SchedulableTask implements Runnable {

    public final ObjectMapper mapper = new ObjectMapper();
    public abstract String getThreadPrefix();
    public abstract void sendMessage(TaskMessage message);
    public abstract TaskProperties getTaskProperties();

    public ScheduledFuture<?> setup(ThreadPoolTaskScheduler scheduler,
                                    SchedulableTask scheduledTask,
                                    String threadPrefix,
                                    String clock,
                                    TimeZone zone){
        CronTrigger trigger = new CronTrigger(clock, zone);

        scheduler.initialize();
        scheduler.setThreadNamePrefix(threadPrefix);
        ScheduledFuture<?> scheduledFutureTask = scheduler.schedule(scheduledTask, trigger);

        return scheduledFutureTask;
    }

    public DispatchRecord record(ScheduledDispatchRepository repository, String message, TaskProperties taskProperties, Date timestamp){
        DispatchRecord dispatchRecord = new DispatchRecord(message, timestamp, taskProperties.getThreadPrefix(), taskProperties.getDestination());
        return repository.save(dispatchRecord);
    }

}
