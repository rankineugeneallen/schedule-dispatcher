package com.nst.scheduledispatcher.controller;

import com.nst.scheduledispatcher.model.NextScheduledExecution;
import com.nst.scheduledispatcher.service.FutureScheduleService;
import com.nst.scheduledispatcher.service.DynamicSchedulerService;
import com.nst.scheduledispatcher.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.TimeZone;

@RestController
@RequestMapping(value = "/scheduled-dispatch", consumes = "application/json")
@Slf4j
public class DispatcherController {

    private DynamicSchedulerService dynamicSchedulerService;
    private FutureScheduleService futureScheduleService;
    private TaskService taskService;

    @Autowired
    public DispatcherController(DynamicSchedulerService dynamicSchedulerService,
                                FutureScheduleService futureScheduleService,
                                TaskService taskService){
        this.dynamicSchedulerService = dynamicSchedulerService;
        this.futureScheduleService = futureScheduleService;
        this.taskService = taskService;
    }

    @PostMapping("/update-schedule")
    public boolean updateScheduledRequest(@RequestParam String threadPrefix,
                                          @RequestParam(required = false) String clock,
                                          @RequestParam(required = false) String zone,
                                          @RequestParam boolean useDefaultValues,
                                          @RequestParam(required = false) boolean scheduleIfNotPresent) {
        return dynamicSchedulerService.rescheduleTask(threadPrefix, clock, zone, useDefaultValues, scheduleIfNotPresent);
    }

    @PostMapping("cancel-scheduled-task")
    public boolean cancelScheduledTask(@RequestParam String threadPrefix, @RequestParam boolean cancelImmediately){
        return dynamicSchedulerService.cancelScheduledFutureTask(threadPrefix, cancelImmediately);
    }

    @GetMapping("/get-next-schedule-for-task")
    public NextScheduledExecution getNextScheduleForTask(@RequestParam String threadPrefix){
        return futureScheduleService.findNextScheduledExecution(threadPrefix);
    }
//
//    @GetMapping("/get-task-config")
//    public void getNextScheduleForTask(){
//        taskService.getTaskConfig();
//    }


}
