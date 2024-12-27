package com.nst.scheduledispatcher.task.order;

import com.nst.scheduledispatcher.model.TaskMessage;
import lombok.Data;

@Data
public class OrderMessage implements TaskMessage {

    String test = "hello";
    int numberOfItems = 1234;

}
