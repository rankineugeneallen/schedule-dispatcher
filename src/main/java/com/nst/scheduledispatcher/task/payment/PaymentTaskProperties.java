package com.nst.scheduledispatcher.task.payment;

import com.nst.scheduledispatcher.task.TaskProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
@ConfigurationProperties(prefix = "default.run.payment")
@Data
public class PaymentTaskProperties implements TaskProperties {
    String threadPrefix;
    String destination;
    String clock;
    TimeZone zone;
    boolean runWithPendingTask;
}
