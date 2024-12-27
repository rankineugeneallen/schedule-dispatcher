package com.nst.scheduledispatcher.service;

import com.nst.scheduledispatcher.task.SchedulableTask;
import com.nst.scheduledispatcher.model.NextScheduledExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class FutureScheduleService {

    private final List<TaskScheduler> taskSchedulerList;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureTasks;
    private final List<SchedulableTask> schedulableTaskList;

    @Autowired
    public FutureScheduleService(List<TaskScheduler> taskSchedulerList,
                                 ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureTasks,
                                 List<SchedulableTask> schedulableTaskList){
        this.taskSchedulerList = taskSchedulerList;
        this.scheduledFutureTasks = scheduledFutureTasks;
        this.schedulableTaskList = schedulableTaskList;
    }

    public NextScheduledExecution findNextScheduledExecution(String threadPrefix){
        long timeDelayMs = 0L;
        long timeDelaySec = 0L;
        long timeDelayMin = 0L;
        long timeDelayHr = 0L;

        ScheduledFuture<?> scheduledFutureTask = scheduledFutureTasks.get(threadPrefix);

        if(scheduledFutureTask != null){
            timeDelayMs = scheduledFutureTask.getDelay(TimeUnit.MILLISECONDS);
            timeDelaySec = scheduledFutureTask.getDelay(TimeUnit.SECONDS);
            timeDelayMin = scheduledFutureTask.getDelay(TimeUnit.MINUTES);
            timeDelayHr = scheduledFutureTask.getDelay(TimeUnit.HOURS);
        }

        return new NextScheduledExecution(timeDelayMs, timeDelaySec, timeDelayMin, timeDelayHr);
    }

}
