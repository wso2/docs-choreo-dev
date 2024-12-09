package com.wso2.choreo.integration.models.platformServices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseCredentialsCreateRequest {
    private String database;
    private String display_name;
    private List<String> applicable_environments;
    private boolean is_super_admin;
    private String username;
    private String password;
    private List<String> privilege_levels;
}
