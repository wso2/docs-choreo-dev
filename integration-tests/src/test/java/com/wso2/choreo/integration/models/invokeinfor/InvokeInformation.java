package com.wso2.choreo.integration.models.invokeinfor;


import lombok.Data;

@Data
public class InvokeInformation {

    private String apiId;
    private String environmentId;
    private String environmentName;
    private String environmentChoreoName;
    private String releaseId;
    private boolean isProxy;
    private String version;
    private String versionId;
    private String invokeUrl;
    private String lifecycleStatus;
    private ApiRevision apiRevision;

}
