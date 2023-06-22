-- Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.


-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_usage_db_user')
BEGIN
    CREATE USER [choreo_usage_db_user] FOR LOGIN [choreo_usage_db_user]
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_usage_db TO choreo_usage_db_user
END;
GO

-- choreo_usage_db.dbo.monthly_infra_cost_breakdown definition
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[monthly_infra_cost_breakdown]') AND TYPE IN (N'U'))
BEGIN
    CREATE TABLE monthly_infra_cost_breakdown (
        id INTEGER IDENTITY(1,1),
        org_id VARCHAR(128) NOT NULL,
        project_id VARCHAR(128) NOT NULL,
        start_date DATETIME2(0) NOT NULL,
        end_date DATETIME2(0) NOT NULL,
        namespace VARCHAR(128) NOT NULL,
        monthly_total_cost FLOAT,
        cpu_cost FLOAT,
        gpu_cost FLOAT,
        ram_cost FLOAT,
        network_cost FLOAT, 
        lb_cost FLOAT,
        pv_cost FLOAT,
        PRIMARY KEY (id)
    );
END
GO

-- choreo_usage_db.dbo.daily_infra_cost_dp definition (cluster: shared data plane)
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[daily_infra_cost]') AND TYPE IN (N'U'))
BEGIN
    CREATE TABLE daily_infra_cost (
        id INTEGER IDENTITY(1,1),
        org_id VARCHAR(128) NOT NULL,
        project_id VARCHAR(128) NOT NULL,
        namespace VARCHAR(255) NOT NULL,
        job_id VARCHAR(128) NOT NULL,
        start_date DATETIME2(0) NOT NULL,
        end_date DATETIME2(0) NOT NULL,
        execution_timestamp DATETIME2(3) NOT NULL,
        daily_total_cost FLOAT,
        cpu_cost FLOAT,
        gpu_cost FLOAT,
        ram_cost FLOAT,
        network_cost FLOAT, 
        lb_cost FLOAT,
        pv_cost FLOAT,
        PRIMARY KEY (id)
    );
END
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[infra_cost_collection_job_status]') AND TYPE IN (N'U'))
BEGIN
    CREATE TABLE infra_cost_collection_job_status (
        id VARCHAR(128) NOT NULL,
        cluster_id VARCHAR(128) NOT NULL,
        date DATETIME2(0) NOT NULL,
        execution_timestamp DATETIME2(3) NOT NULL,
        started BIT NOT NULL DEFAULT 0,
        completed BIT NOT NULL DEFAULT 0,
        collection_successful BIT NOT NULL DEFAULT 0,
        insertion_successful BIT NOT NULL DEFAULT 0,
        monthly_insertion_successful BIT NOT NULL DEFAULT 0,
        PRIMARY KEY (date)
    );
END
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[usage_publishing_status]') AND TYPE IN (N'U'))
BEGIN
    CREATE TABLE usage_publishing_status (
        date DATETIME2(0) NOT NULL,
        org_uuid VARCHAR(128) NOT NULL,
        subscription_item_id VARCHAR(100) NOT NULL,
        usage FLOAT NOT NULL,
        usage_type VARCHAR(128) NOT NULL, -- "infra", "infra-support" or "component"
        frequency VARCHAR(100) NOT NULL, -- "daily" or "monthly"
        successful BIT NOT NULL DEFAULT 0,
        PRIMARY KEY (date, org_uuid, usage_type, frequency)
    );
END
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[usage_publisher_job_status]') AND TYPE IN (N'U'))
BEGIN
    CREATE TABLE usage_publisher_job_status (
        usage_date DATETIME2(0) NOT NULL,
        execution_timestamp DATETIME2(3) NOT NULL,
        started BIT NOT NULL DEFAULT 0,
        completed BIT NOT NULL DEFAULT 0,
        frequency VARCHAR(100) NOT NULL, -- "daily" or "monthly"
        PRIMARY KEY (usage_date, frequency)
    );
END
GO
