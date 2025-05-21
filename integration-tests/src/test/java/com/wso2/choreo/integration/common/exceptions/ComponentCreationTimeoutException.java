package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the Choreo component creation has not succeeded for configured time
 */
public class ComponentCreationTimeoutException extends Exception {

    @Override
    public String toString() {
        String message = "Component creation status check timed out";
        return "ComponentCreationTimeoutException{" +
                "message='" + message + '\'' +
                '}';
    }
}
