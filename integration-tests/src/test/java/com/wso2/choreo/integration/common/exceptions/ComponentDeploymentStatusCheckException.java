package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to check the status of Choreo component deployment does not receive expected status code
 */
public class ComponentDeploymentStatusCheckException extends Exception {

    public ComponentDeploymentStatusCheckException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }

    public ComponentDeploymentStatusCheckException(Throwable e) {
        super(e);
    }

    public ComponentDeploymentStatusCheckException(String msg) {
        super(msg);
    }
}
