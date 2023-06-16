IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='last_update_times' and xtype='U')
BEGIN
    CREATE TABLE last_update_times (
        org_id VARCHAR(128) NOT NULL,
        last_update_time datetime2 NOT NULL,
        PRIMARY KEY (org_id)
    );
END
GO

-- Source control config tables
CREATE TABLE [dbo].[common_source_control_configs](
	[org_id] [varchar](128) NOT NULL,
	[env_id] [varchar](128) NOT NULL,
	[source_system_type] [varchar](128) NOT NULL,
	[client_id] [varchar](256) NOT NULL,
	[client_secret] [varchar](256) NOT NULL,
	[app_id] [varchar](128) NOT NULL,
	[collector_subscription_id] [varchar](128) NOT NULL,
	[configurator_subscription_id] [varchar](128) NOT NULL
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[common_source_control_configs] ADD PRIMARY KEY CLUSTERED 
(
	[org_id] ASC
)ON [PRIMARY]
GO

-- Incident config tables
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='common_incident_configs' and xtype='U')
BEGIN
    CREATE TABLE common_incident_configs (
        org_id VARCHAR(128) NOT NULL,
        env_id VARCHAR(128) NOT NULL,
        selector_criteria VARCHAR(128),
        rejector_criteria VARCHAR(128),
        incident_system_type VARCHAR(128) NOT NULL,
        client_id VARCHAR(256) NOT NULL,
        client_secret VARCHAR(256) NOT NULL,
	app_id VARCHAR(256) NOT NULL,
	collector_subscription_id VARCHAR(256) NOT NULL,
	configurator_subscription_id VARCHAR(256) NOT NULL,
        PRIMARY KEY (org_id)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='github_incident_configs' and xtype='U')
BEGIN
    CREATE TABLE github_incident_configs (
        org_id VARCHAR(128) NOT NULL,
        github_org_name VARCHAR(128) NOT NULL,
        github_repo_name VARCHAR(128) NOT NULL,
        PRIMARY KEY (org_id, github_org_name, github_repo_name)
    );
END
GO

-- ServiceNow config table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='servicenow_incident_secret_configs' and xtype='U')
BEGIN
    CREATE TABLE servicenow_incident_secret_configs (
        org_id VARCHAR(128) NOT NULL,
        servicenow_instance_secret VARCHAR(128) NOT NULL,
        PRIMARY KEY (org_id)
    );
END
GO
