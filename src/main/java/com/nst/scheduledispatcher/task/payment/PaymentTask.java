package com.nst.scheduledispatcher.task.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nst.scheduledispatcher.component.JmsSender;
import com.nst.scheduledispatcher.repository.ScheduledDispatchRepository;
import com.nst.scheduledispatcher.task.TaskProperties;
import com.nst.scheduledispatcher.model.TaskMessage;
import com.nst.scheduledispatcher.task.SchedulableTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class PaymentTask extends SchedulableTask {

    @Getter
    TaskProperties taskProperties;
    JmsSender jmsSender;
    ScheduledDispatchRepository scheduledDispatchRepository;

    public PaymentTask(JmsSender jmsSender, PaymentTaskProperties taskProperties, ScheduledDispatchRepository scheduledDispatchRepository){
        this.jmsSender = jmsSender;
        this.taskProperties = taskProperties;
        this.scheduledDispatchRepository = scheduledDispatchRepository;
    }

    @Override
    public void run() {
        log.info(this.getClass().getName() + " ran");
        sendMessage(new PaymentMessage());
    }

    @Override
    public String getThreadPrefix() {
        return taskProperties.getThreadPrefix();
    }

    @Override
    public void sendMessage(TaskMessage message) {
        try{
            String messageStr = mapper.writeValueAsString(message);

            Date timestamp = jmsSender.sendMessage(taskProperties.getDestination(), messageStr);

            record(scheduledDispatchRepository, messageStr, taskProperties, timestamp);
        } catch (JsonProcessingException e){
            log.error("Failed to send measaage in Payment Task", e);
        }
    }

}
