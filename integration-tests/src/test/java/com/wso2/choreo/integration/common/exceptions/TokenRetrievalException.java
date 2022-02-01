package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to retrieve the OAuth token does not receive expected status code
 */
public class TokenRetrievalException extends Throwable {
    private final int statusCode;
    private final String message;

    public TokenRetrievalException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "TokenRetrievalException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
