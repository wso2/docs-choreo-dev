package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to check the status of Choreo component creation does not receive expected status code
 */
public class ComponentCreationStatusCheckException extends Exception {

    public ComponentCreationStatusCheckException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }

    public ComponentCreationStatusCheckException(Throwable e) {
        super(e);
    }
}
