package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the commit history of a Choreo component is empty
 */
public class NoLatestCommitHashFoundException extends Exception {
    private static String defaultMessage = "No Commits Found";
    
    public NoLatestCommitHashFoundException(String message) {
        super(message);
    }

    public NoLatestCommitHashFoundException() {
        super(defaultMessage);
    }
}
