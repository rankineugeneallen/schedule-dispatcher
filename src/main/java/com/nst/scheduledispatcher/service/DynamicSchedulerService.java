package com.nst.scheduledispatcher.service;

import com.nst.scheduledispatcher.task.SchedulableTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class DynamicSchedulerService {

    private final TaskService taskService;

    @Autowired
    public DynamicSchedulerService(TaskService taskService){
        this.taskService = taskService;
    }

    public boolean rescheduleTask(String threadPrefix, String clock, String zone, boolean useDefaultValues, boolean scheduleIfNotPresent){
        Optional<ScheduledFuture<?>> scheduledFutureTask = taskService.findScheduledFutureTask(threadPrefix);
        TimeZone convertedZone = convertTimeZone(zone);

        if(!checkRescheduleErrorConditions(scheduledFutureTask, threadPrefix, useDefaultValues, scheduleIfNotPresent, clock, convertedZone)){
            return false;
        }

        if (scheduledFutureTask.isEmpty() && scheduleIfNotPresent){
            log.info("Going to schedule a new task with threadPrefix {} as it wasn't already scheduled", threadPrefix);
            return scheduleFutureTask(threadPrefix, clock, convertedZone, useDefaultValues);
        }

        boolean scheduledTaskCancelled = cancelScheduledFutureTask(scheduledFutureTask.get(), threadPrefix, false);

        if (scheduledTaskCancelled) {
            return scheduleFutureTask(threadPrefix, clock, convertedZone, useDefaultValues);
        }

        return false;
    }

    private TimeZone convertTimeZone(String zone){
        TimeZone convertedZone = null;

        if(zone != null) {
            convertedZone = TimeZone.getTimeZone(zone);
            log.info("Converting zone: {} to {}", zone, convertedZone);
        } else {
            log.warn("Zone is null");
        }

        return convertedZone;

    }

    private boolean checkRescheduleErrorConditions(Optional<ScheduledFuture<?>> scheduledFutureTask, String threadPrefix, boolean useDefaultValues, boolean scheduleIfNotPresent, String clock, TimeZone convertedZone){
        if(scheduledFutureTask.isEmpty() && !scheduleIfNotPresent) {
            log.error("Failed to find Scheduled Future Task with this thread prefix {}", threadPrefix);
            return false;
        }

        if((clock == null || convertedZone == null) && !useDefaultValues){
            log.error("Cannot reschedule, clock and/or zone are null, clock: {}, zone: {}", clock, convertedZone);
            return false;
        }

        return true;
    }

    public boolean cancelScheduledFutureTask(String threadPrefix, boolean cancelImmediately){
        log.info("Attempting to cancel scheduled future task with threadPrefix of {}", threadPrefix);

        Optional<ScheduledFuture<?>> scheduledFutureTask = taskService.findScheduledFutureTask(threadPrefix);

        if(scheduledFutureTask.isPresent()) {
            return cancelScheduledFutureTask(scheduledFutureTask.get(), threadPrefix, cancelImmediately);
        } else {
            log.error("Could not find scheduled future task with threadPrefix of {}", threadPrefix);
            return false;
        }
    }

    public boolean cancelScheduledFutureTask(ScheduledFuture<?> scheduledFutureTask, String threadPrefix, boolean cancelImmediately){
        log.info("Attempting to cancel scheduled future task with threadPrefix of {}", threadPrefix);

        scheduledFutureTask.cancel(cancelImmediately);

        if(scheduledFutureTask.isCancelled()){
            taskService.removeScheduledFutureTask(threadPrefix);
            log.info("Removed scheduled future task with threadPrefix {} from scheduledFutureTasks list.", threadPrefix);
            return true;
        } else {
            log.error("Failed to cancel task with threadPrefix {}", threadPrefix);
            return false;
        }
    }

    private boolean scheduleFutureTask(String threadPrefix, final String clock, final TimeZone zone, boolean useDefaultValues){
        String clockToSet;
        TimeZone zoneToSet;
        Optional<SchedulableTask> taskToSchedule = taskService.findSchedulableTask(threadPrefix);
        Optional<TaskScheduler> taskScheduler = taskService.findThreadPoolTaskScheduler(threadPrefix);

        if(taskToSchedule.isEmpty()){
            log.error("Failed to find schedulable task with thread prefix {}", threadPrefix);
            return false;
        }
        if(taskScheduler.isEmpty()){
            log.error("Failed to find the task scheduler with thread prefix {}", threadPrefix);
            return false;
        }

        if(useDefaultValues){
            String clockFromProperties = taskToSchedule.get().getTaskProperties().getClock();
            TimeZone zoneFromProperties = taskToSchedule.get().getTaskProperties().getZone();

            log.info("Scheduling future task with default values - threadId: {}, clock: {}, zone: {}", threadPrefix, clockFromProperties, zoneFromProperties);

            clockToSet = clockFromProperties;
            zoneToSet = zoneFromProperties;
        } else {
            clockToSet = clock;
            zoneToSet = zone;
        }

        log.info("Scheduling future task with values - threadId: {}, clock: {}, zone: {}", threadPrefix, clockToSet, zoneToSet);

        return scheduleTask((ThreadPoolTaskScheduler) taskScheduler.get(), taskToSchedule.get(), threadPrefix, clockToSet, zoneToSet);
    }

    private boolean scheduleTask(ThreadPoolTaskScheduler threadPoolTaskScheduler, SchedulableTask taskToSchedule, String threadPrefix, String clock, TimeZone zone){
        ScheduledFuture<?> futureTaskJustScheduled = taskToSchedule.setup(threadPoolTaskScheduler, taskToSchedule, threadPrefix, clock, zone);

        if(futureTaskJustScheduled == null){
            log.error("Failed to schedule a future task {}", threadPrefix);
            return false;
        } else {
            taskService.addScheduledFutureTask(threadPrefix, futureTaskJustScheduled);
            return true;
        }
    }
}
