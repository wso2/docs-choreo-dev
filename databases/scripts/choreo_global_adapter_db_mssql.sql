-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_global_adapter_db_user')
BEGIN
    CREATE USER [choreo_global_adapter_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_global_adapter_db TO choreo_global_adapter_db_user
END;
GO

--create ga_local_adapter_partition
IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[ga_local_adapter_partition]') AND TYPE IN (N'U'))

CREATE TABLE ga_local_adapter_partition (
	api_uuid varchar(150) NOT NULL,
	label_hierarchy varchar(50) NOT NULL,
	api_id int NOT NULL,
	org_id varchar(150) NULL,
CONSTRAINT PK_uuid_hierarcy PRIMARY KEY (api_uuid,label_hierarchy),
CONSTRAINT api_id_label_unique UNIQUE (api_id,label_hierarchy)
);

--create la_partition_size
IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[la_partition_size]') AND TYPE IN (N'U'))

CREATE TABLE la_partition_size (
	parition_size int NULL
);

CREATE TABLE ga_org_quota_status (
	org_id varchar(150) NULL,
	is_exceeded BIT NOT NULL DEFAULT 0,
);

CREATE TABLE ga_api_partition (
    api_uuid varchar(150) NOT NULL,
    apim_environment_name VARCHAR(255) NOT NULL,
    dataplane_id varchar(150) NOT NULL,
    gateway_accessibility_type VARCHAR(100) NOT NULL,
    slot_id int NOT NULL,
    org_id varchar(150) NOT NULL,
    UNIQUE(dataplane_id, gateway_accessibility_type, slot_id),
    PRIMARY KEY (api_uuid, apim_environment_name, dataplane_id, gateway_accessibility_type, org_id)
);
