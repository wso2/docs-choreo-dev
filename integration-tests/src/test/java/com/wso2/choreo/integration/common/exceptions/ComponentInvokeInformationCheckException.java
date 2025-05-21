package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to check the status of Choreo component invoke information does not receive expected status code
 */
public class ComponentInvokeInformationCheckException extends Exception {
    private final int statusCode;
    private final String message;

    public ComponentInvokeInformationCheckException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ComponentInvokeInformationException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
