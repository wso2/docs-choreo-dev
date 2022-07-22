package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to redeploy a stopped component does not receive the expected status code
 */
public class RedeployException extends Exception {
    private int stausCode;
    public RedeployException(int statusCode, String message) {

        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
        this.stausCode=statusCode;
    }
    public int getStatusCode(){return stausCode;}
}
