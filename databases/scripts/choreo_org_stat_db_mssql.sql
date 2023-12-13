-- Create User
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'choreo_org_stat_db') AND NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_org_stat_db_user')
BEGIN
    CREATE USER [choreo_ai_anomaly_detector_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_org_stat_db TO choreo_org_stat_db_user
END;
GO

-- Create Table [dbo].[active_component_details]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[active_component_details]') AND TYPE IN (N'U'))
CREATE TABLE active_component_details(
	date VARCHAR(256) NULL,
    active_component_id VARCHAR(256) NULL,
    component_type VARCHAR(256) NULL,
    org_handle VARCHAR(256) NULL,
    app_type VARCHAR(256) NULL,
    created_at VARCHAR(256) NULL,
    updated_at VARCHAR(256) NULL
);

-- Create Table [dbo].[active_components]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[active_components]') AND TYPE IN (N'U'))
CREATE TABLE active_components(
	date VARCHAR(128) NULL,
    component_type VARCHAR(256) NULL,
    deployed_component_count INT NULL,
    active_deployment_count INT NULL
);

-- Create Table [dbo].[analytics_org_active_users]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[analytics_org_active_users]') AND TYPE IN (N'U'))
CREATE TABLE analytics_org_active_users(
	org_handle VARCHAR(256) NULL,
    pay_type VARCHAR(128) NULL,
    created_date DATE NULL,
    active_users INT NULL
);

-- Create Table [dbo].[analytics_org_component]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[analytics_org_component]') AND TYPE IN (N'U'))
CREATE TABLE analytics_org_component(
	org_handle VARCHAR(256) NULL,
    component_type VARCHAR(256) NULL,
    created_date DATE NULL,
    num_of_components INT NULL
);

-- Create Table [dbo].[analytics_org_deployment]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[analytics_org_deployment]') AND TYPE IN (N'U'))
CREATE TABLE analytics_org_deployment(
	org_handle VARCHAR(256) NULL,
    deployment_type VARCHAR(256) NULL,
    created_date DATE NULL,
    count INT NULL
);

-- Create Table [dbo].[analytics_org_transaction_component]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[analytics_org_transaction_component]') AND TYPE IN (N'U'))
CREATE TABLE analytics_org_transaction_component(
	date VARCHAR(256) NULL,
    component_id VARCHAR(256) NULL,
    component_type VARCHAR(256) NULL,
    organization_id VARCHAR(256) NULL,
    org_handle VARCHAR(256) NULL,
    hit_count INT NULL
);

-- Create Table [dbo].[analytics_org_transaction_deployment]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[analytics_org_transaction_deployment]') AND TYPE IN (N'U'))
CREATE TABLE analytics_org_transaction_deployment(
	org_handle VARCHAR(256) NULL,
    deployment_type VARCHAR(256) NULL,
    created_date DATE NULL,
    count INT NULL
);

-- Create Table [dbo].[api_mappings]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[api_mappings]') AND TYPE IN (N'U'))
CREATE TABLE api_mappings(
	app_id VARCHAR(256) NULL,
    apim_id VARCHAR(256) NULL,
    org_handle VARCHAR(256) NULL,
    organization_id VARCHAR(256) NULL
);

-- Create Table [dbo].[component]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[component]') AND TYPE IN (N'U'))
CREATE TABLE component(
	id VARCHAR(256) NULL,
    org_handle VARCHAR(256) NULL,
    user_id INT NULL,
    component_type VARCHAR(256) NULL,
    created_date DATE NULL
);

-- Create Table [dbo].[deployment]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[deployment]') AND TYPE IN (N'U'))
CREATE TABLE deployment(
	organization_id VARCHAR(36) NULL,
    environment_id VARCHAR(36) NULL,
    deployment_type VARCHAR(256) NULL,
    count INT NULL,
    created_at DATE NULL
);

-- Create Table [dbo].[gateway_traffic]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[gateway_traffic]') AND TYPE IN (N'U'))
CREATE TABLE gateway_traffic(
	date VARCHAR(128) NULL,
    customer_id VARCHAR(256) NULL,
    deployment_id VARCHAR(256) NULL,
    hit_count INT NULL,
    deployment_type VARCHAR(256) NULL,
    apim_env VARCHAR(256) NULL,
    org_handle VARCHAR(256) NULL,
    org_creator_origin VARCHAR(256) NULL
);

-- Create Table [dbo].[job_executions]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[job_executions]') AND TYPE IN (N'U'))
CREATE TABLE job_executions(
	date VARCHAR(256) NULL,
    component_id VARCHAR(256) NULL,
    component_name VARCHAR(256) NULL,
    organization_id VARCHAR(256) NULL,
    workspace_id VARCHAR(256) NULL,
    execution_count INT NULL
);

-- Create Table [dbo].[organization]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[organization]') AND TYPE IN (N'U'))
CREATE TABLE organization(
	uuid VARCHAR(256) NULL,
    handle VARCHAR(256) NULL,
    created_date VARCHAR(128) NULL,
    creator_origin VARCHAR(256) NULL,
    is_onprem_only VARCHAR(256) NULL
);

-- Create Table [dbo].[payg_customer]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[payg_customer]') AND TYPE IN (N'U'))
CREATE TABLE payg_customer(
	id VARCHAR(256) NULL,
    created_timestamp INT NULL,
    created_date VARCHAR(256) NULL,
    customer_id VARCHAR(256) NULL,
    cloud_type VARCHAR(256) NULL,
    org_id VARCHAR(256) NULL,
    org_handle VARCHAR(256) NULL
);

-- Create Table [dbo].[subscription]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[subscription]') AND TYPE IN (N'U'))
CREATE TABLE subscription(
	org_id VARCHAR(128) NOT NULL,
    org_handle VARCHAR(256) NULL,
    org_type VARCHAR(128) NULL,
    created_date DATE NULL,
    PRIMARY KEY CLUSTERED (org_id ASC)
) ON [PRIMARY];

-- Create Table [dbo].[transaction_other]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[transaction_other]') AND TYPE IN (N'U'))
CREATE TABLE transaction_other(
	component_id VARCHAR(256) NULL,
    count INT NULL,
    organization_id VARCHAR(256) NULL,
    environment_id VARCHAR(256) NULL,
    created_date DATE NULL
);

-- Create Table [dbo].[transaction_proxy]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[transaction_proxy]') AND TYPE IN (N'U'))
CREATE TABLE transaction_proxy(
	count INT NULL,
    organization_id VARCHAR(256) NULL,
    created_date DATE NULL
);

-- Create Table [dbo].[transactions]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[transactions]') AND TYPE IN (N'U'))
CREATE TABLE transactions(
	date VARCHAR(256) NULL,
    customer_id VARCHAR(256) NULL,
    deployment_id VARCHAR(256) NULL,
    api_id VARCHAR(256) NULL,
    hit_count INT NULL
);

-- Create Table [dbo].[user_activity]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[user_activity]') AND TYPE IN (N'U'))
CREATE TABLE user_activity(
	date VARCHAR(256) NULL,
    org_uuid VARCHAR(256) NULL,
    user_count VARCHAR(256) NULL
);

-- Create Table [dbo].[user_registration]
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[dbo].[user_registration]') AND TYPE IN (N'U'))
CREATE TABLE user_registration(
	idp_id VARCHAR(256) NULL,
    is_anonymous INT NULL,
    created_date VARCHAR(256) NULL,
    is_enterprise VARCHAR(256) NULL,
    origin VARCHAR(256) NULL
);




