package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to retrieve Choreo component does not receive expected status code
 */
public class ComponentRetrieveException extends Exception {

    public ComponentRetrieveException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }

    public ComponentRetrieveException(Throwable e) {
        super(e);
    }

    public ComponentRetrieveException(String msg) {
        super(msg);
    }
}
