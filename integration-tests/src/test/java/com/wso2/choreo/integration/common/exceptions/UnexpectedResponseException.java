package com.wso2.choreo.integration.common.exceptions;

public class UnexpectedResponseException extends  Exception{

    public UnexpectedResponseException(int statusCode, String exception){
        super("{ statusCode=" + statusCode + ", message='" + exception + "'}");
    }
}
