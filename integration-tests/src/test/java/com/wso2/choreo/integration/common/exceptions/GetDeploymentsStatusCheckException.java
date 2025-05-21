package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to get deployments of a component does not receive the expected status 
 * code
 */
public class GetDeploymentsStatusCheckException extends Exception {

    public GetDeploymentsStatusCheckException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }

    public GetDeploymentsStatusCheckException(Throwable e) {
        super(e);
    }

}
