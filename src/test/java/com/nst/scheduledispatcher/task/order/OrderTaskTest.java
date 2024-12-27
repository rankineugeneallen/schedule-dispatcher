package com.nst.scheduledispatcher.task.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nst.scheduledispatcher.component.JmsSender;
import com.nst.scheduledispatcher.controller.DispatcherController;
import com.nst.scheduledispatcher.model.DispatchRecord;
import com.nst.scheduledispatcher.repository.ScheduledDispatchRepository;
import com.nst.scheduledispatcher.task.TaskProperties;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderTaskTest {

    @Mock
    TaskProperties taskPropertiesMock;

    @Mock
    JmsSender jmsSenderMock;

    @Mock
    ScheduledDispatchRepository scheduledDispatchRepositoryMock;

    Date date;
    ObjectMapper mapper = new ObjectMapper();
    String threadPrefix = "threadPrefix";
    String destination = "destination";

    OrderTask orderTask;

    @BeforeEach
    void setup() throws JsonProcessingException {
        Calendar calendar = Calendar.getInstance();
        date = calendar.getTime();

        when(taskPropertiesMock.getDestination()).thenReturn(destination);
        when(jmsSenderMock.sendMessage(eq(destination), anyString())).thenReturn(date);

        orderTask = new OrderTask(jmsSenderMock, taskPropertiesMock, scheduledDispatchRepositoryMock);
    }


    @Test
    void test_orderTask_sendMessage_happyPath() throws JsonProcessingException {
        OrderMessage message = new OrderMessage();
        String messageStr = mapper.writeValueAsString(message);
        DispatchRecord expDispatchRecord = new DispatchRecord(messageStr, date, threadPrefix, destination);

//        when(scheduledDispatchRepositoryMock.save(any(DispatcherController.class))).thenReturn(expDispatchRecord);

//        when(scheduledDispatchRepositoryMock.save(any(DispatcherController.class))).thenAnswer(i -> i.getArguments()[0]);
        orderTask.sendMessage(message);



//        assertEquals(expDispatchRecord, );
    }

}