package com.wso2.choreo.integration.common.exceptions;

public class QuotaNotLimitedException extends Exception{
    public QuotaNotLimitedException(int statusCode, String message) {

        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }
}
