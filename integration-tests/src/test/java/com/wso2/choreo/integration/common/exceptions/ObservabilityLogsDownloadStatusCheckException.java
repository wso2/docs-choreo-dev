package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to download zipped logs does not receive expected status code
 */
public class ObservabilityLogsDownloadStatusCheckException extends Exception {

    public ObservabilityLogsDownloadStatusCheckException(int statusCode, String message) {
        super("{ statusCode=" + statusCode +
                ", message='" + message + "'}");
    }

    public ObservabilityLogsDownloadStatusCheckException(Throwable e) {
        super(e);
    }
}
