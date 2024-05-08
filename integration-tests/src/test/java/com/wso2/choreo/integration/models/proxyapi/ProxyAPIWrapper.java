package com.wso2.choreo.integration.models.proxyapi;
import java.util.List;
import lombok.Data;

@Data
public class ProxyAPIWrapper {
    private  int count;
    private List<ProxyAPI> list;
}

