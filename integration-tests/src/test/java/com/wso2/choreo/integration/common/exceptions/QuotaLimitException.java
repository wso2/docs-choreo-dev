package com.wso2.choreo.integration.common.exceptions;

public class QuotaLimitException extends Exception {
    public QuotaLimitException(int statusCode, String message) {

        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }
}
