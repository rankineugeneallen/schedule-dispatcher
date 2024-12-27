package com.nst.scheduledispatcher.model;

import com.nst.scheduledispatcher.task.TaskProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.TimeZone;

@Data
@AllArgsConstructor
public class TaskPropertiesMock implements TaskProperties {

    @NonNull String threadPrefix;
    @NonNull String destination;
    @NonNull String clock;
    @NonNull TimeZone zone;
    @NonNull boolean runWithPendingTask;

    @Override
    public String getThreadPrefix() {
        return threadPrefix;
    }

    @Override
    public String getClock() {
        return clock;
    }

    @Override
    public TimeZone getZone() {
        return zone;
    }

    @Override
    public String getDestination() {
        return destination;
    }
}
