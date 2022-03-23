package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to generate an API-key does not receive expected status code
 */
public class GenerateAPIKeyCheckException extends Throwable {
    private final int statusCode;
    private final String message;

    public GenerateAPIKeyCheckException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "GenerateAPIKeyCheckException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
