package com.nst.scheduledispatcher.task;

import java.util.TimeZone;

public interface TaskProperties {
    String getThreadPrefix();
    String getClock();
    TimeZone getZone();
    String getDestination();
}
