package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the Choreo component deployment fails
 */
public class ComponentDeploymentException extends Throwable {
    private final int statusCode;
    private final String message;

    public ComponentDeploymentException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "DeployComponentException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
