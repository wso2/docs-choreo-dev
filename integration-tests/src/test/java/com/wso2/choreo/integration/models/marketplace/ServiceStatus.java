package com.wso2.choreo.integration.models.marketplace;

public enum ServiceStatus {
    PROTOTYPE, //Until deploy to production, definition can change
    PUBLISHED, //deployed to production
    DEPRECATED,
    CREATED
}
