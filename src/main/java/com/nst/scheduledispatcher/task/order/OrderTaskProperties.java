package com.nst.scheduledispatcher.task.order;

import com.nst.scheduledispatcher.task.TaskProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
@ConfigurationProperties(prefix = "default.run.order")
@Data
public class OrderTaskProperties implements TaskProperties {
    String threadPrefix;
    String destination;
    String clock;
    TimeZone zone;
    boolean runWithPendingTask;
}
