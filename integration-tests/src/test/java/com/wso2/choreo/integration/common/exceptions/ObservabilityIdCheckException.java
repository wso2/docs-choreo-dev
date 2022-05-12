package com.wso2.choreo.integration.common.exceptions;

public class ObservabilityIdCheckException extends Throwable {

    private final int statusCode;
    private final String message;

    public ObservabilityIdCheckException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ObservabilityIdCheckException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
