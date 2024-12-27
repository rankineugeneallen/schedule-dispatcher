package com.nst.scheduledispatcher.task.order;

import com.nst.scheduledispatcher.task.TaskProperties;
import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.TimeZone;

@Configuration
@ConfigurationProperties(prefix = "default.run.order")
@Data
@Validated
public class OrderTaskProperties implements TaskProperties {
    @NonNull String threadPrefix;
    @NonNull String destination;
    @NonNull String clock;
    @NonNull TimeZone zone;
    @NonNull boolean runWithPendingTask;
}
