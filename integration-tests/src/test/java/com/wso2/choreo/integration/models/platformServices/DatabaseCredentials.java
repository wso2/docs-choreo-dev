package com.wso2.choreo.integration.models.platformServices;

import lombok.Data;

import java.sql.Time;
import java.util.List;
@Data
public class DatabaseCredentials {
    private String id;
    private String databaseName;
    private String displayName;
    private List<String> applicableEnvironments;
    private boolean isSuperAdmin;
    private Time createdAt;
    private Time updatedAt;
    private List<String> privilegeLevels;
}
