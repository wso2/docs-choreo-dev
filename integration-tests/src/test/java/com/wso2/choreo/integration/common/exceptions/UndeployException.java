package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to redeploy a stopped component does not receive the expected status code
 */
public class UndeployException extends Exception {

    public UndeployException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }
}
