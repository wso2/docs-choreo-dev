package com.wso2.choreo.integration.models.componentstatus;


import lombok.Data;

@Data
public class Status {

    private String message;
    private boolean success;
    private StatusData data;


}
