package com.nst.scheduledispatcher.service;

import com.nst.scheduledispatcher.model.NextScheduledExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FutureScheduleServiceTest {

    @Mock
    TaskService taskServiceMock;

    @Mock
    ScheduledFuture<?> scheduledFutureMock;

    FutureScheduleService futureScheduleService;

    @BeforeEach
    void setup(){
        doReturn(scheduledFutureMock).when(taskServiceMock).getScheduledFutureTask(anyString());
        when(scheduledFutureMock.getDelay(eq(TimeUnit.MILLISECONDS))).thenReturn(123L);
        when(scheduledFutureMock.getDelay(eq(TimeUnit.SECONDS))).thenReturn(223L);
        when(scheduledFutureMock.getDelay(eq(TimeUnit.MINUTES))).thenReturn(323L);
        when(scheduledFutureMock.getDelay(eq(TimeUnit.HOURS))).thenReturn(423L);

        futureScheduleService = new FutureScheduleService(taskServiceMock);
    }

    @Test
    void test_findNextScheduledExecution_happyPath(){
        NextScheduledExecution expNextScheduledExecution = new NextScheduledExecution(123L, 223L, 323L, 423L);
        NextScheduledExecution nextScheduledExecution = futureScheduleService.findNextScheduledExecution("threadPrefix");

        assertEquals(expNextScheduledExecution, nextScheduledExecution);
    }

}