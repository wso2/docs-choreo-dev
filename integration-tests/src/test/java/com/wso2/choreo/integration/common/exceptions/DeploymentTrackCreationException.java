package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the deployment track creation fails
 */
public class DeploymentTrackCreationException extends Exception {

    public DeploymentTrackCreationException(String message) {
        super(message);
    }

    public DeploymentTrackCreationException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }


    public DeploymentTrackCreationException(Throwable e) {
        super(e);
    }
}
