package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to create a Choreo project does not receive expected status code
 */
public class ProjectCreationException extends Exception {

    public ProjectCreationException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }

    public ProjectCreationException(Throwable e) {
        super(e);
    }

}
