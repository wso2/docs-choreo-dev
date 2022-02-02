package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the Choreo component creation fails
 */
public class ComponentCreationException extends Throwable {

    private final int statusCode;
    private final String message;

    public ComponentCreationException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ComponentCreationException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
