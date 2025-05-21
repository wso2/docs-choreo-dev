package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the Choreo component Build fails
 */
public class ComponentBuildException extends Exception {

    public ComponentBuildException(String message) {
        super(message);
    }

    public ComponentBuildException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }


    public ComponentBuildException(Throwable e) {
        super(e);
    }
}
