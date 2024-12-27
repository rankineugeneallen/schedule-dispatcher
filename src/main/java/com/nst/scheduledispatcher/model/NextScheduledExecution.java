package com.nst.scheduledispatcher.model;

import lombok.*;

@Data
@AllArgsConstructor
public class NextScheduledExecution {

    long timeDelayMs;
    long timeDelaySec;
    long timeDelayMin;
    long timeDelayHr;

}
