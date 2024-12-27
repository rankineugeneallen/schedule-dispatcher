package com.nst.scheduledispatcher.task.payment;


import com.nst.scheduledispatcher.model.TaskMessage;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentMessage implements TaskMessage {

    long maxNumOfItems = 1234567L;
    BigDecimal maxCost = new BigDecimal("9.00");

}
