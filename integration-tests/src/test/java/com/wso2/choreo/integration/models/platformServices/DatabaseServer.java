package com.wso2.choreo.integration.models.platformServices;

import lombok.Data;

@Data
public class DatabaseServer extends CreatedDatabaseServer {

    private ConnectionParams connection_params;

    @Data
    public static class ConnectionParams {
        private String database;
        private String host;
        private boolean password_reset;
        private String port;
        private boolean ssl_required;
        private String user;
    }
}
