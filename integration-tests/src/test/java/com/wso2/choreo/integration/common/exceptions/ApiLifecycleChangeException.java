package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to change the Choreo API lifecycle does not receive expected status code
 */
public class ApiLifecycleChangeException extends Throwable {
    private final int statusCode;
    private final String message;

    public ApiLifecycleChangeException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiLifecycleChangeException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
