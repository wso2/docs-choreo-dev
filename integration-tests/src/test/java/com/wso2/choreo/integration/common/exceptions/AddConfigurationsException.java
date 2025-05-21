package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to add Choreo component configurations does not receive expected status code
 */
public class AddConfigurationsException extends Exception {
    private final int statusCode;
    private final String message;

    public AddConfigurationsException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "AddConfigurationsException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
