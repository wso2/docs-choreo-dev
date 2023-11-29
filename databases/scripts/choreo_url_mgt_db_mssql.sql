-- Create database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'choreo_url_mgt_db')
    BEGIN
        CREATE DATABASE choreo_url_mgt_db;
    END
GO

-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_url_mgt_db_user')
BEGIN
    CREATE USER [choreo_url_mgt_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_url_mgt_db TO choreo_url_mgt_db_user
END;
GO

USE choreo_url_mgt_db;
GO

DROP TABLE IF EXISTS [Cluster];
DROP TABLE IF EXISTS [ApprovalStatus];
DROP TABLE IF EXISTS [URLMapping];
DROP TABLE IF EXISTS [Domain];
DROP TABLE IF EXISTS [KeyVaultSecret];

CREATE TABLE [Domain] (
	[id] VARCHAR(191) NOT NULL,
	[organization_uuid] VARCHAR(191) NOT NULL,
	[name] VARCHAR(191) NOT NULL,
	[type] VARCHAR(9) CHECK ([type] IN ('api', 'webapp', 'devportal')) NOT NULL,
	[environment_id] VARCHAR(191) NOT NULL,
	[tls_provider] VARCHAR(12) CHECK ([tls_provider] IN ('lets_encrypt', 'custom')) NOT NULL,
	[secret_name] VARCHAR(511) NOT NULL,
	[expiry_date] DATETIME2,
	[created_time] DATETIME2 NOT NULL,
	[updated_time] DATETIME2,
	CONSTRAINT domain_name_constraint UNIQUE (name),
	CONSTRAINT secret_name_unique_constraint UNIQUE(secret_name),
	PRIMARY KEY([id])
);

CREATE TABLE [Cluster] (
	[id] VARCHAR(191) NOT NULL,
	[routing_cluster_id] VARCHAR(191) NOT NULL,
	[api_cname] VARCHAR(511) NOT NULL,
	[webapp_cname] VARCHAR(511) NOT NULL,
	[namespace] VARCHAR(191) NOT NULL,
	[ingress_class_name] VARCHAR(191) NOT NULL,
	[nginx_service_name] VARCHAR(191) NOT NULL,
	[nginx_service_port] INT NOT NULL,
	[letsencrypt_issuer_name] VARCHAR(511) NOT NULL,
	CONSTRAINT cluster_id_routing_cluster_id UNIQUE ([id],[routing_cluster_id]),
	PRIMARY KEY([id])
);

CREATE TABLE [URLMapping] (
	[id] VARCHAR(191) NOT NULL,
	[organization_uuid] VARCHAR(191) NOT NULL,
	[domain_id] VARCHAR(191) NOT NULL,
	[default_path] VARCHAR(1023) NOT NULL,
	[custom_path] VARCHAR(1023) NOT NULL,
	[default_domain] VARCHAR(1023) NOT NULL,
	[component_id] VARCHAR(191) NOT NULL,
	[api_id] VARCHAR(191),
	[ingress_name] VARCHAR(511),
	[created_time] DATETIME2 NOT NULL,
	[updated_time] DATETIME2,
	FOREIGN KEY([domain_id]) REFERENCES [Domain]([id]) ON DELETE CASCADE,
	CONSTRAINT org_domain_custom_path UNIQUE ([organization_uuid],[domain_id],[custom_path]),
	PRIMARY KEY([id])
);

CREATE TABLE [ApprovalStatus] (
	[url_mapping_id] VARCHAR(191) NOT NULL,
	[requested_by] VARCHAR(191) NOT NULL,
	[updated_by] VARCHAR(191),
	[status] VARCHAR(191) CHECK ([status] IN ('approved', 'pending', 'rejected')) NOT NULL,
	[requested_time] DATETIME2 NOT NULL,
	[updated_time] DATETIME2 NOT NULL,
    FOREIGN KEY([url_mapping_id]) REFERENCES [URLMapping]([id]) ON DELETE CASCADE,
	PRIMARY KEY([url_mapping_id])
);

CREATE TABLE [KeyVaultSecret] (
	[id] VARCHAR(191) NOT NULL,
	[secret_name] VARCHAR(511) NOT NULL,
	[version] VARCHAR(191) NOT NULL,
	[secret_store_provider] VARCHAR(1023) NOT NULL,
	[secret_store_name] VARCHAR(511) NOT NULL,
	[domain_id] VARCHAR(191) NOT NULL,
	[created_time] DATETIME2 NOT NULL,
	[updated_time] DATETIME2,
	CONSTRAINT secret_name_kv_unique_constraint UNIQUE (secret_name),
	CONSTRAINT domain_id_unique_constraint UNIQUE (domain_id),
	FOREIGN KEY([domain_id]) REFERENCES [Domain]([id]) ON DELETE CASCADE,
	PRIMARY KEY([id])
);

-- This is for checking the conflicts for the custom path under the same domain

DROP TRIGGER IF EXISTS CheckURLPathConflictOnInsert;
GO

CREATE TRIGGER CheckURLPathConflictOnInsert
ON URLMapping
INSTEAD OF INSERT
AS
BEGIN
    -- Check for conflicts with existing customPath values
    IF EXISTS (
        SELECT 1
        FROM URLMapping AS t
        JOIN INSERTED AS i ON t.domain_id = i.domain_id
        LEFT JOIN ApprovalStatus AS a ON t.id = a.url_mapping_id
        WHERE (CHARINDEX(t.custom_path + '/', i.custom_path) = 1 OR CHARINDEX(i.custom_path + '/', t.custom_path) = 1)
            AND (a.status != 'rejected')
    )
    BEGIN
        RAISERROR('URL path conflict detected. Insert aborted', 16, 1);
    END
    ELSE
    BEGIN
        -- Insert the new rows if there are no conflicts
        INSERT INTO URLMapping (id, organization_uuid, domain_id, default_path, custom_path, default_domain, component_id, api_id, ingress_name, created_time, updated_time)
        SELECT id, organization_uuid, domain_id, default_path, custom_path, default_domain, component_id, api_id, ingress_name, created_time, updated_time 
        FROM INSERTED;
    END
END;
GO

DROP TRIGGER IF EXISTS CheckURLPathConflictOnUpdate;
GO

CREATE TRIGGER CheckURLPathConflictOnUpdate
ON URLMapping
INSTEAD OF UPDATE
AS
BEGIN
    -- Check for conflicts with existing customPath values
    IF EXISTS (
        SELECT 1
        FROM URLMapping AS t
        JOIN INSERTED AS i ON t.domain_id = i.domain_id
        LEFT JOIN ApprovalStatus AS a ON t.id = a.url_mapping_id
        WHERE (CHARINDEX(t.custom_path + '/', i.custom_path) = 1 OR CHARINDEX(i.custom_path + '/', t.custom_path) = 1)
            AND (a.status != 'rejected')
    )
    BEGIN
        RAISERROR('URL path conflict detected. Update aborted', 16, 1);
    END
    ELSE
    BEGIN
        -- Update the rows if there are no conflicts
        UPDATE URLMapping
        SET custom_path = (SELECT custom_path FROM INSERTED), ingress_name = (SELECT ingress_name FROM INSERTED), updated_time = (SELECT updated_time FROM INSERTED)
        WHERE id = (SELECT id FROM INSERTED)
    END
END;
GO

-- Sample Data for Clusters

INSERT INTO [Cluster] ([id], [routing_cluster_id], [api_cname], [webapp_cname], [namespace], [ingress_class_name], [nginx_service_name], [nginx_service_port], [letsencrypt_issuer_name]) VALUES ('7eca5163-6a37-ee11-b8f0-000d3adac5f0', '4F682651-D14C-EC11-981F-2818781ADACC', 'customdns.e1-us-east-azure.preview-dv.choreoapis.dev', 'customdns.e1-us-east-azure.choreoapps.dev', 'dev-choreo-apim', 'dev-choreo-apim-nginx', 'choreo-nginx-service', 9443, 'letsencrypt-prod')
GO
INSERT INTO [Cluster] ([id], [routing_cluster_id], [api_cname], [webapp_cname], [namespace], [ingress_class_name], [nginx_service_name], [nginx_service_port], [letsencrypt_issuer_name]) VALUES ('dee0f1c5-c968-ee11-9937-00224853e41c', '2955FC0F-EB6B-ED11-ADE6-CC60C8B57983', 'customdns.e1-eu-north-azure.preview-dv.choreoapis.dev','customdns.e1-eu-north-azure.choreoapps.dev', 'dev-choreo-apim', 'dev-choreo-apim-nginx', 'choreo-nginx-service', 9443, 'letsencrypt-prod')
GO
