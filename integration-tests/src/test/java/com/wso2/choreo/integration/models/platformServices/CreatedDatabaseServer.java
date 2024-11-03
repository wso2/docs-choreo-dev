package com.wso2.choreo.integration.models.platformServices;

import lombok.Data;

import java.util.Date;

@Data
public class CreatedDatabaseServer {
    private String id;
    private Date created_at;
    private String project_id;
    private String name;
    private String service_plan_id;
    private CloudProvider cloud_provider;
    private CloudRegion cloud_region;
    private ServerStatus status;
    private DatabaseTypes type;
    private Boolean is_vector_enabled;
    private Boolean display_on_marketplace;
    private ServicePlan service_plan;
}

@Data
class ServicePlan {
    private int backup_interval_hours;
    private int backup_retention_days;
    private String hourly_price_usd;
    private String monthly_price_usd;
    private String name;
    private int node_count;
    private int node_cpu_count;
    private int node_ram_gb;
    private int storage_gb;
}


