package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to deploy a Choreo component does not receive expected status code
 */
public class ComponentDeploymentFailureException extends Throwable {

    @Override
    public String toString() {
        String message = "failure when deploying the component";
        return "DeploymentFailureException{" +
                "message='" + message + '\'' +
                '}';
    }
}
