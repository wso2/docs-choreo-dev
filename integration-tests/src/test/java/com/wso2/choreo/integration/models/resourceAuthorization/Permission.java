package com.wso2.choreo.integration.models.resourceAuthorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Permission {
    private String domainArea;
    private String handle;
    private String id;
}
