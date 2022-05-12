package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to get deployments of a component does not receive the expected status 
 * code
 */
public class GetDeploymentsStatusCheckException extends Throwable {
    private final int statusCode;
    private final String message;

    public GetDeploymentsStatusCheckException(int statusCode, String message) {
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
