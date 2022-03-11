-- Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.


-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_subscriptions_db_user')
BEGIN
    CREATE USER [choreo_subscriptions_db_user] FOR LOGIN [choreo_subscriptions_db_user]
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_subscriptions_db TO choreo_subscriptions_db_user
END;
GO


-- TODO: Remove the DEFAULT constraint in the "is_internal" column when the addTier method is modified

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tier' and xtype='U')
BEGIN
    CREATE TABLE tier (
        id VARCHAR(128) NOT NULL,
        name VARCHAR(256) NOT NULL,
        description VARCHAR(1024) NOT NULL,
        cost INTEGER NOT NULL,
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        is_internal BIT NOT NULL DEFAULT 1,
        PRIMARY KEY (ID)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='subscription' and xtype='U')
BEGIN
    CREATE TABLE subscription (
        id VARCHAR(128) NOT NULL,
        org_id VARCHAR(128) NOT NULL,
        org_handle VARCHAR(255) NOT NULL,
        tier_id VARCHAR(128) NOT NULL,
        stripe_subscription_item_id VARCHAR(128) DEFAULT NULL,
        billing_date BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        status VARCHAR(128) NOT NULL,
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        PRIMARY KEY (org_id, tier_id),
        UNIQUE (id),
        UNIQUE (org_id),
        UNIQUE (org_handle),
        CONSTRAINT FK_TierSubscription FOREIGN KEY (tier_id) REFERENCES tier(id)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='quota' and xtype='U')
BEGIN
    CREATE TABLE quota (
        id INTEGER IDENTITY(1,1),
        tier_id VARCHAR(128) NOT NULL,
        attribute_name VARCHAR(256) NOT NULL,
        threshold INTEGER NOT NULL,
        PRIMARY KEY (ID),
        UNIQUE (TIER_ID, ATTRIBUTE_NAME),
        CONSTRAINT FK_TierQuota FOREIGN KEY (tier_id) REFERENCES tier(id)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='attribute' and xtype='U')
BEGIN
    CREATE TABLE attribute (
        id VARCHAR(128) NOT NULL,
        name VARCHAR(256) NOT NULL,
        description VARCHAR(256),
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        PRIMARY KEY (ID),
        UNIQUE (NAME)
    );
END
GO

-- Initial data for subscriptions
-- Relevant issue: https://github.com/wso2-enterprise/choreo/issues/7627

INSERT INTO [attribute] (id,name,description,created_at) VALUES
	 (N'01ebea30-6199-152a-b9c8-53a5e8c83008',N'service_quota',N'Number of services can be created',1627639787657),
	 (N'01ebea3b-2dba-182a-9aad-b68df13c86d0',N'api_quota',N'Number of apis can be created',1627639777657),
	 (N'01ebea3c-0b3c-1bf8-a1a7-22eb4cc3566e',N'remote_app_quota',N'Number of remote apps can be created',1627639767657),
	 (N'01ebea43-d02d-1c12-8ae0-1b5947056fc1',N'integration_quota',N'Number of integrations can be created',1627639757657),
	 (N'01ec0a51-c895-14fe-9dc7-1b40e9c0a60a',N'step_quota',N'Number of steps can be consumed',1630383873712),
     (N'01ec1c6d-956e-175a-ad64-0f27c561adb8',N'developer_count',N'Number of developers can be allocated',1627639797657);
GO

INSERT INTO tier (id,name,description,cost,created_at,is_internal) VALUES
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'Free',N'Free tier to tryout choreo',0,1627639797657,0),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'Internal Dev',N'Free tier to tryout choreo',0,1631591372000,1),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'Choreo Internal',N'Tier for choreo internal users',0,1627639797657,1),
     (N'01ec1f8e-7ba6-1f88-bd74-41709200d0c0',N'Individual',N'Tier for Individual users',50,1627639797657,0),
	 (N'01ec1d1e-0e9c-16e4-b6c9-1904e9ef9567',N'Team',N'Tier for Team users',395,1627639797657,0),
	 (N'01ec1f82-5451-1cfa-83ca-222452b503ab',N'Group',N'Tier for Group users',995,1627639797657,0),
     (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'Enterprise',N'Tier for Enterprise users',0,1627639797657,1);
GO

INSERT INTO quota (tier_id,attribute_name,threshold) VALUES
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'service_quota',10),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'integration_quota',10),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'api_quota',10),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'remote_app_quota',10),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'step_quota',1000),
     (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'developer_count',1),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'service_quota',5),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'integration_quota',5),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'api_quota',5),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'remote_app_quota',5),
     (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'step_quota',100),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'service_quota',1000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'integration_quota',1000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'api_quota',1000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'remote_app_quota',1000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'step_quota',1000000),
     (N'01ec1f8e-7ba6-1f88-bd74-41709200d0c0',N'service_quota',100),
	 (N'01ec1f8e-7ba6-1f88-bd74-41709200d0c0',N'integration_quota',100),
	 (N'01ec1f8e-7ba6-1f88-bd74-41709200d0c0',N'api_quota',100),
	 (N'01ec1f8e-7ba6-1f88-bd74-41709200d0c0',N'step_quota',10000),
	 (N'01ec1f8e-7ba6-1f88-bd74-41709200d0c0',N'developer_count',1),
	 (N'01ec1d1e-0e9c-16e4-b6c9-1904e9ef9567',N'service_quota',100),
	 (N'01ec1d1e-0e9c-16e4-b6c9-1904e9ef9567',N'integration_quota',100),
	 (N'01ec1d1e-0e9c-16e4-b6c9-1904e9ef9567',N'api_quota',100),
	 (N'01ec1d1e-0e9c-16e4-b6c9-1904e9ef9567',N'step_quota',100000),
	 (N'01ec1d1e-0e9c-16e4-b6c9-1904e9ef9567',N'developer_count',10),
	 (N'01ec1f82-5451-1cfa-83ca-222452b503ab',N'service_quota',100),
	 (N'01ec1f82-5451-1cfa-83ca-222452b503ab',N'integration_quota',50),
	 (N'01ec1f82-5451-1cfa-83ca-222452b503ab',N'api_quota',200),
	 (N'01ec1f82-5451-1cfa-83ca-222452b503ab',N'step_quota',1000000),
	 (N'01ec1f82-5451-1cfa-83ca-222452b503ab',N'developer_count',50),
     (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'service_quota',-100),
	 (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'integration_quota',-50),
	 (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'api_quota',-200),
	 (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'step_quota',-1000000),
	 (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'developer_count',-1);
GO

INSERT INTO choreo_subscriptions_db.dbo.billing_tier (id,tier_id,product_id,price_id,currency,recurring_interval) VALUES
	 (N'01ec1491-3eff-1aec-b511-2eec6e3c92d2',N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'prod_K8pD0xG5AqXUzG',N'price_1JY0lgEeYOVsvOhWyxCaQmNy',N'USD',N'month'),
     (N'01ec1f92-e300-1a7c-b10c-16b408c2e17b',N'01ec1f8e-7ba6-1f88-bd74-41709200d0c0',N'prod_K8om1rkbBYoyxW',N'price_1JUX9OEeYOVsvOhWNPoBudVS',N'USD',N'month'),
	 (N'01ec1491-316e-1c84-9195-5bbb347a8a0b',N'01ec1d1e-0e9c-16e4-b6c9-1904e9ef9567',N'prod_K8ot6C4EbUhWIa',N'price_1JUXG4EeYOVsvOhWYOu9Turn',N'USD',N'month'),
     (N'01ec1491-316e-1c84-9195-5bbb347a8a0b',N'01ec1f82-5451-1cfa-83ca-222452b503ab',N'prod_K8owVLffK8Gzzh',N'price_1JUXIREeYOVsvOhWVyik0FRN',N'USD',N'month');
GO
