package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the Choreo component creation fails
 */
public class ComponentCreationException extends Exception {

    public ComponentCreationException(String message) {
        super(message);
    }

    public ComponentCreationException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }


    public ComponentCreationException(Throwable e) {
        super(e);
    }
}
