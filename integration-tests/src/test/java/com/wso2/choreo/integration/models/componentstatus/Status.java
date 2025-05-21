package com.wso2.choreo.integration.models.componentstatus;


import lombok.Data;

import java.util.SplittableRandom;

@Data
public class Status {

    private String message;
    private boolean success;
    private StatusData data;
    private String status;
    private String conclusion;
}
