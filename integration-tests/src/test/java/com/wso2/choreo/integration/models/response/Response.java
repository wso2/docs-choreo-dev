package com.wso2.choreo.integration.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response{
    private String res;
    private int statusCode;

}
