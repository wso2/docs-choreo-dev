package com.wso2.choreo.integration.models.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProxyResponse <T>{

    private T entity;
    private Response response;
}
