/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.common.exceptions;

/**
 * An exception to be thrown if the API call to generate an API-key does not receive expected status code
 */
public class ObservabilityASTCheckException extends Exception {
    private final int statusCode;
    private final String message;

    public ObservabilityASTCheckException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ObservabilityASTCheckException{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
