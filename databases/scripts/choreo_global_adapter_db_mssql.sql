--create ga_local_adapter_partition
IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[ga_local_adapter_partition]') AND TYPE IN (N'U'))

CREATE TABLE ga_local_adapter_partition (
	api_uuid varchar(150) NOT NULL,
	label_hierarchy varchar(50) NOT NULL,
	api_id int NOT NULL,
	org_id varchar(150) NULL,
CONSTRAINT PK_uuid_hierarcy PRIMARY KEY (api_uuid,label_hierarchy)
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
