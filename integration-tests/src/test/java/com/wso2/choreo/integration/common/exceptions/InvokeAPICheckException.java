package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call for the component does not receive expected status code
 */
public class InvokeAPICheckException extends Throwable {
    private final int statusCode;
    private final String message;

    public InvokeAPICheckException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "InvokeAPICheckException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
