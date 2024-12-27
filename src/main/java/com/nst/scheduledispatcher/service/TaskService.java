package com.nst.scheduledispatcher.service;

import com.nst.scheduledispatcher.task.SchedulableTask;
import com.nst.scheduledispatcher.task.TaskProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class TaskService {

    private final List<TaskScheduler> taskSchedulerList;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureTasks;
    private final List<SchedulableTask> schedulableTaskList;
    private final List<TaskProperties> taskConfigurableList;

    public TaskService(List<TaskScheduler> taskSchedulerList,
                       ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutureTasks,
                       List<SchedulableTask> schedulableTaskList,
                       List<TaskProperties> taskConfigurableList){
        this.taskSchedulerList = taskSchedulerList;
        this.scheduledFutureTasks = scheduledFutureTasks;
        this.schedulableTaskList = schedulableTaskList;
        this.taskConfigurableList = taskConfigurableList;
    }

    public Optional<SchedulableTask> findSchedulableTask(String threadPrefix){
        return schedulableTaskList.stream().filter(task -> task.getThreadPrefix().equals(threadPrefix)).findFirst();
    }

    public Optional<TaskScheduler> findThreadPoolTaskScheduler(String threadPrefix){
        return taskSchedulerList.stream().filter((taskScheduler) -> taskScheduler instanceof ThreadPoolTaskScheduler && ((ThreadPoolTaskScheduler) taskScheduler).getThreadNamePrefix().equals(threadPrefix)).findFirst();
    }

    public Optional<ScheduledFuture<?>> findScheduledFutureTask(String threadPrefix){
        return Optional.ofNullable(scheduledFutureTasks.get(threadPrefix));
    }

    public void addScheduledFutureTask(String threadPrefix, ScheduledFuture<?> scheduledFutureTask){
        scheduledFutureTasks.put(threadPrefix, scheduledFutureTask);
    }

    public void removeScheduledFutureTask(String threadPrefix){
        scheduledFutureTasks.remove(threadPrefix);
    }

    public ScheduledFuture<?> getScheduledFutureTask(String threadPrefix) {
        return scheduledFutureTasks.get(threadPrefix);
    }
}
