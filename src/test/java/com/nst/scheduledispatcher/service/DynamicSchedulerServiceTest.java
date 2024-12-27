package com.nst.scheduledispatcher.service;

import com.nst.scheduledispatcher.model.TaskPropertiesMock;
import com.nst.scheduledispatcher.task.SchedulableTask;
import com.nst.scheduledispatcher.task.order.OrderTaskProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DynamicSchedulerServiceTest {

    @Mock
    TaskService taskServiceMock;

    @Mock
    ScheduledFuture<?> scheduledFutureMock;

    @Mock
    SchedulableTask schedulableTaskMock;

    @Mock
    ThreadPoolTaskScheduler taskSchedulerMock;

    DynamicSchedulerService dynamicSchedulerService;

    @BeforeEach
    void setup(){
        when(taskServiceMock.findScheduledFutureTask(anyString())).thenReturn(Optional.ofNullable(scheduledFutureMock));
        doNothing().when(taskServiceMock).removeScheduledFutureTask(anyString());
        when(taskServiceMock.findSchedulableTask(anyString())).thenReturn(Optional.ofNullable(schedulableTaskMock));
        when(taskServiceMock.findThreadPoolTaskScheduler(anyString())).thenReturn(Optional.ofNullable(taskSchedulerMock));
        doNothing().when(taskServiceMock).addScheduledFutureTask(anyString(), any());

        when(scheduledFutureMock.cancel(anyBoolean())).thenReturn(true);
        when(scheduledFutureMock.isCancelled()).thenReturn(true);

        doReturn(scheduledFutureMock).when(taskSchedulerMock).schedule(any(Runnable.class), any(Trigger.class));
        doReturn(scheduledFutureMock).when(schedulableTaskMock).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

        dynamicSchedulerService = new DynamicSchedulerService(taskServiceMock);
    }


    @Test
    void test_rescheduleTask_resetAllValues_happyPath(){
        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", "CST", false, false);

        assertTrue(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(1)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(1)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(1)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));
    }


    @Test
    void test_rescheduleTask_useDefaultValues_happyPath(){
        TaskPropertiesMock taskPropertiesMock = new TaskPropertiesMock("defaultThreadPrefix", "defaultDestination", "2 * * * *", TimeZone.getTimeZone("EST"), true);

        when(schedulableTaskMock.getTaskProperties()).thenReturn(taskPropertiesMock);

        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", "CST", true, false);

        assertTrue(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(1)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(1)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(1)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));
        verify(schedulableTaskMock, times(2)).getTaskProperties();

    }

    @Test
    void test_rescheduleTask_scheduleIfNotPresent_happyPath(){
        when(taskServiceMock.findScheduledFutureTask(anyString())).thenReturn(Optional.empty());

        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", "CST", false, true);

        assertTrue(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(1)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(1)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(0)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(0)).isCancelled();
        verify(schedulableTaskMock, times(1)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_rescheduleTask_timeZoneFails_invalidZone(){
        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", "ABC", false, true);

        assertTrue(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(1)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(1)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(1)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_rescheduleTask_timeZoneFails_nullZone(){
        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", null, false, true);

        assertFalse(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(0)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(0)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(0)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_rescheduleTask_scheduledTask_empty(){
        when(taskServiceMock.findScheduledFutureTask(anyString())).thenReturn(Optional.empty());

        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", null, false, false);

        assertFalse(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(0)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(0)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(0)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_rescheduleTask_zone_null(){
        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", null, false, false);

        assertFalse(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(0)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(0)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(0)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_rescheduleTask_scheduledFutureTask_cancelFailed(){
        when(scheduledFutureMock.isCancelled()).thenReturn(false);

        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", "CST", false, false);

        assertFalse(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(0)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_rescheduleTask_schedulableTask_empty(){
        when(taskServiceMock.findSchedulableTask(anyString())).thenReturn(Optional.empty());

        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", "CST", false, false);

        assertFalse(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(1)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_rescheduleTask_threadPoolTaskScheduler_empty(){
        when(taskServiceMock.findThreadPoolTaskScheduler(anyString())).thenReturn(Optional.empty());

        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", "CST", false, false);

        assertFalse(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(1)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_rescheduleTask_scheduleTaskSetup_failedScheduling() {
        when(schedulableTaskMock.setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class))).thenReturn(null);

        boolean taskRescheduled = dynamicSchedulerService.rescheduleTask("threadPrefix", "1 * * * *", "CST", false, false);

        assertFalse(taskRescheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(1)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(1)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_cancelScheduledFutureTask_happyPath(){
        boolean taskScheduled = dynamicSchedulerService.cancelScheduledFutureTask("defaultThread", true);

        assertTrue(taskScheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(1)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(0)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_cancelScheduledFutureTask_scheduledFutureTask_empty(){
        when(taskServiceMock.findScheduledFutureTask(anyString())).thenReturn(Optional.empty());

        boolean taskScheduled = dynamicSchedulerService.cancelScheduledFutureTask("defaultThread", true);

        assertFalse(taskScheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(0)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(0)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(0)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

    @Test
    void test_cancelScheduledFutureTask_scheduledFutureTask_cancelFailed(){
        when(scheduledFutureMock.isCancelled()).thenReturn(false);

        boolean taskScheduled = dynamicSchedulerService.cancelScheduledFutureTask("defaultThread", true);

        assertFalse(taskScheduled);

        verify(taskServiceMock, times(1)).findScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).removeScheduledFutureTask(anyString());
        verify(taskServiceMock, times(0)).findSchedulableTask(anyString());
        verify(taskServiceMock, times(0)).findThreadPoolTaskScheduler(anyString());
        verify(taskServiceMock, times(0)).addScheduledFutureTask(anyString(), any());
        verify(scheduledFutureMock, times(1)).cancel(anyBoolean());
        verify(scheduledFutureMock, times(1)).isCancelled();
        verify(schedulableTaskMock, times(0)).setup(any(ThreadPoolTaskScheduler.class), any(SchedulableTask.class), anyString(), anyString(), any(TimeZone.class));

    }

}