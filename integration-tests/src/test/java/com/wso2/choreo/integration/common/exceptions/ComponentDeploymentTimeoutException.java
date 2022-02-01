package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the Choreo component deployment has succeeded for configured time
 */
public class ComponentDeploymentTimeoutException extends Throwable {

    @Override
    public String toString() {
        String message = "Component deployment status check timed out";
        return "ComponentDeploymentTimeoutException{" +
                "message='" + message + '\'' +
                '}';
    }
}
