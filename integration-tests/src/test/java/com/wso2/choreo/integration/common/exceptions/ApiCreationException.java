package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to create the API does not receive expected status code
 */
public class ApiCreationException extends Throwable {
    private final int statusCode;
    private final String message;

    public ApiCreationException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiCreationException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
