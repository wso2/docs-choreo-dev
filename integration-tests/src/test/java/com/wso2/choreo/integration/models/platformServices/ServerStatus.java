package com.wso2.choreo.integration.models.platformServices;

import lombok.Getter;
@Getter
public enum ServerStatus {
    CREATING,
    ACTIVE,
    POWERED_OFF,
    RESUMING,
    DELETING,
    DELETED,
    ERROR
}