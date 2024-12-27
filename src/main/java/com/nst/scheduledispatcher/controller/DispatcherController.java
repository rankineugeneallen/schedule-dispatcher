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
@RequestMapping("/scheduled-dispatch")
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
    public boolean updateScheduledRequest(@RequestParam String threadPrefix, @RequestParam String clock, @RequestParam String zone) {
        return dynamicSchedulerService.rescheduleTask(threadPrefix, clock, TimeZone.getTimeZone(zone));
    }

    @GetMapping("/get-next-schedule-for-task")
    public NextScheduledExecution getNextScheduleForTask(@RequestParam String threadPrefix){
        return futureScheduleService.findNextScheduledExecution(threadPrefix);
    }

    @GetMapping("/get-task-config")
    public void getNextScheduleForTask(){
        taskService.getTaskConfig();
    }


}
