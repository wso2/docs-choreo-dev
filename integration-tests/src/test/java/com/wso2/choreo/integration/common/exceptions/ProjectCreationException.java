package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to create a Choreo project does not receive expected status code
 */
public class ProjectCreationException extends Throwable {
    private final int statusCode;
    private final String message;

    public ProjectCreationException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ProjectCreationException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
