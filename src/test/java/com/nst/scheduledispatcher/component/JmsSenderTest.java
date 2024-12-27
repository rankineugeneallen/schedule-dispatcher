package com.nst.scheduledispatcher.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JmsSenderTest {

    @Mock
    JmsTemplate jmsTemplateMock;

    JmsSender jmsSender;

    @BeforeEach
    void setup(){
        doNothing().when(jmsTemplateMock).convertAndSend(anyString(), Optional.ofNullable(any()));

        jmsSender = new JmsSender(jmsTemplateMock);
    }

    @Test
    void test_singleMessage() throws JsonProcessingException {
        Date date = jmsSender.sendMessage("destination", "message");

        assertNotNull(date);

        verify(jmsTemplateMock, times(1)).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void test_multipleMessages() throws JsonProcessingException, InterruptedException {
        Date date1 = jmsSender.sendMessage("destination", "message");
        Thread.sleep(100);
        Date date2 = jmsSender.sendMessage("destination", "message");

        assertNotNull(date1);
        assertNotNull(date2);
        assertNotEquals(date1, date2);

        verify(jmsTemplateMock, times(2)).convertAndSend(anyString(), Optional.ofNullable(any()));

    }

}