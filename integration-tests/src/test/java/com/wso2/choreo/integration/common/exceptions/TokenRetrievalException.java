package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to retrieve the OAuth token does not receive expected status code
 */
public class TokenRetrievalException extends Exception {


    public TokenRetrievalException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }

    public TokenRetrievalException(Throwable e) {
        super(e);
    }

    public TokenRetrievalException(String msg, Throwable e) {
        super(msg, e);
    }
}
