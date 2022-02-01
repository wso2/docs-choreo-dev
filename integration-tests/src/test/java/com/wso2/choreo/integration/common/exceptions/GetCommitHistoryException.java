package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to retrieve the commit history of a Choreo component does not receive expected status code
 */
public class GetCommitHistoryException extends Throwable {
    private final int statusCode;
    private final String message;

    public GetCommitHistoryException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "GetCommitHistoryException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
