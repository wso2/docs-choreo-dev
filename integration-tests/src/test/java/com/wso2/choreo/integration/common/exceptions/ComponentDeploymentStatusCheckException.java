package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to check the status of Choreo component deployment does not receive expected status code
 */
public class ComponentDeploymentStatusCheckException extends Throwable {
    private final int statusCode;
    private final String message;

    public ComponentDeploymentStatusCheckException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ComponentDeploymentStatusCheckException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
