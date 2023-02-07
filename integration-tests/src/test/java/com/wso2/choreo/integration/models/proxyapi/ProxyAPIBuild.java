package com.wso2.choreo.integration.models.proxyapi;


import lombok.Data;

@Data
public class ProxyAPIBuild {

    private Build[] builds;
    private int count;
    private int limit;
}
