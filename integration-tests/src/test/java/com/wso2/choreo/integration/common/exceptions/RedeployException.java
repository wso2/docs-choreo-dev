package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to redeploy a stopped component does not receive the expected status code
 */
public class RedeployException extends Throwable {
    private final int statusCode;
    private final String message;

    public RedeployException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "RedeployException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
