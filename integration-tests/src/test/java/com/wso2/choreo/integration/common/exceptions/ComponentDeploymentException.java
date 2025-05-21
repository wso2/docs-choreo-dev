package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the Choreo component deployment fails
 */
public class ComponentDeploymentException extends Exception {
    private int stausCode;
    public ComponentDeploymentException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
        this.stausCode=statusCode;
    }
    public int getStatusCode(){return stausCode;}
}
