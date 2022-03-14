package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to check the status of Choreo component creation does not receive expected status code
 */
public class ComponentCreationStatusCheckException extends Throwable {
    private final int statusCode;
    private final String message;

    public ComponentCreationStatusCheckException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ComponentCreationStatusCheckException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
