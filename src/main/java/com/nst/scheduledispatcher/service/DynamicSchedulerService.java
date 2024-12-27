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

    public boolean rescheduleTask(String threadPrefix, String clock, TimeZone zone){
        Optional<ScheduledFuture<?>> scheduledFutureTask = taskService.findScheduledFutureTask(threadPrefix);

        if(scheduledFutureTask.isEmpty()) {
            log.error("Failed to find Scheduled Future Task with this thread prefix {}", threadPrefix);
            return false;
        }

        boolean scheduledTaskCancelled = cancelScheduledTask(scheduledFutureTask.get(), threadPrefix);

        if (scheduledTaskCancelled) {
            return rescheduleFutureTask(threadPrefix, clock, zone);
        }

        return false;
    }


    private boolean cancelScheduledTask(ScheduledFuture<?> scheduledFutureTask, String threadPrefix){
        scheduledFutureTask.cancel(false);

        if(scheduledFutureTask.isCancelled()){
            taskService.removeScheduledFutureTask(threadPrefix);
            return true;
        }

        return false;
    }

    private boolean rescheduleFutureTask(String threadPrefix, String clock, TimeZone zone){
        Optional<SchedulableTask> taskToSchedule = taskService.findSchedulableTask(threadPrefix);
        Optional<TaskScheduler> taskScheduler = taskService.findThreadPoolTaskScheduler(threadPrefix);

        if(taskToSchedule.isEmpty()){
            log.error("Failed to find schedulable task with thread prefix {}", threadPrefix);
            return false;
        } else if(taskScheduler.isEmpty()){
            log.error("Failed to find the task schduler with thread prefix {}", threadPrefix);
            return false;
        }

        return scheduleTask((ThreadPoolTaskScheduler) taskScheduler.get(), taskToSchedule.get(), threadPrefix, clock, zone);
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
