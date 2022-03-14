package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to retrieve Choreo component does not receive expected status code
 */
public class ComponentRetrieveException extends Throwable {
    private final int statusCode;
    private final String message;

    public ComponentRetrieveException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ComponentRetrieveException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
