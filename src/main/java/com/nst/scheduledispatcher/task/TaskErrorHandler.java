package com.nst.scheduledispatcher.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

@Slf4j
public class TaskErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.error("Throwing error", t);
    }

}
