package com.nst.scheduledispatcher.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nst.scheduledispatcher.model.TaskMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JmsSender {

    private final JmsTemplate jmsTemplate;

    @Autowired
    public JmsSender(JmsTemplate jmsTemplate){
        this.jmsTemplate = jmsTemplate;
    }

    public Date sendMessage(String destination, String message) throws JsonProcessingException {
        Calendar calendar = Calendar.getInstance();
        Date currDate = calendar.getTime();

        jmsTemplate.convertAndSend(destination, message);

        return currDate;
    }

}
