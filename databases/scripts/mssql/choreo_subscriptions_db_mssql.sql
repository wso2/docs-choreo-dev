-- Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.


-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_subscriptions_db_user')
BEGIN
    CREATE USER [choreo_subscriptions_db_user] with password = N'${choreo_subscriptions_db_mssql_password}'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_subscriptions_db TO choreo_subscriptions_db_user
END;
GO


-- TODO: Remove the DEFAULT constraint in the "is_internal" column when the addTier method is modified
-- (Deprecated)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tier' and xtype='U')
BEGIN
    CREATE TABLE tier (
        id VARCHAR(128) NOT NULL,
        name VARCHAR(256) NOT NULL,
        description VARCHAR(1024) NOT NULL,
        is_paid BIT NOT NULL,
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        is_internal BIT NOT NULL DEFAULT 1,
        PRIMARY KEY (ID)
    );
END
GO

-- (Deprecated)
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
        is_paid BIT NOT NULL DEFAULT 0,
        step_quota INTEGER NOT NULL DEFAULT 5000,
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        PRIMARY KEY (org_id, tier_id),
        UNIQUE (id),
        UNIQUE (org_id),
        UNIQUE (org_handle),
        CONSTRAINT FK_TierSubscription FOREIGN KEY (tier_id) REFERENCES tier(id)
    );
END
GO

-- (Deprecated)
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

-- (Deprecated)
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

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tierV2' and xtype='U')
BEGIN
    CREATE TABLE tierV2 (
        id VARCHAR(128) NOT NULL,
        name VARCHAR(256) NOT NULL,
        description VARCHAR(1024) NOT NULL,
        is_paid BIT NOT NULL,
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        is_internal BIT NOT NULL DEFAULT 1,
        PRIMARY KEY (ID)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='threshold' and xtype='U')
BEGIN
    CREATE TABLE threshold (
        id VARCHAR(128) NOT NULL,
        tier_id VARCHAR(128) NOT NULL,
        threshold NVARCHAR(MAX),
        billing_provider VARCHAR(128),
        PRIMARY KEY (id),

    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='subscriptionV2' and xtype='U')
BEGIN
    CREATE TABLE subscriptionV2 (
        id VARCHAR(128) NOT NULL,
        org_id VARCHAR(128) NOT NULL,
        org_handle VARCHAR(255) NOT NULL,
        tier_id VARCHAR(128) NOT NULL,
        subscription_item_id VARCHAR(128) DEFAULT NULL,
        infra_cost_subscription_item_id VARCHAR(128) DEFAULT NULL,
        subscription_type VARCHAR(128) NOT NULL DEFAULT N'choreo-subscription',
        billing_provider VARCHAR(128) DEFAULT NULL,
        billing_date BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        status VARCHAR(128) NOT NULL,
        is_paid BIT NOT NULL DEFAULT 0,
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        threshold_id VARCHAR(128) DEFAULT '01ee409e-cdfd-13d6-86c6-1a523acc861b',
        email_type VARCHAR(128) NOT NULL DEFAULT N'non-corporate',
        PRIMARY KEY (org_id, tier_id,subscription_type),
        UNIQUE (id),
        CONSTRAINT FK_TierSubscriptionV2 FOREIGN KEY (tier_id) REFERENCES tierV2(id),
        CONSTRAINT FK_ThresholdSubscriptionV2 FOREIGN KEY (threshold_id) REFERENCES threshold(id)
    );
END
GO

-- Initial data for subscriptions
-- Relevant issue: https://github.com/wso2-enterprise/choreo/issues/7627

-- (Deprecated)
INSERT INTO [attribute] (id,name,description,created_at) VALUES
	 (N'01ebea30-6199-152a-b9c8-53a5e8c83008',N'running_app_quota',N'Number of running applications quota',1627639797657),
	 (N'01ebea3b-2dba-182a-9aad-b68df13c86d0',N'component_quota',N'Number of components can be created',1627639797657);
GO

-- (Deprecated)
INSERT INTO tier (id,name,description,is_paid,created_at,is_internal) VALUES
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'Free',N'Free tier to tryout choreo',0,1627639797657,0),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'Pay As You Go',N'Tier for paid users',1,1627639797657,0),
	 (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'Enterprise',N'Tier for enterprise users',1,1627639797657,1);
GO

-- (Deprecated)
INSERT INTO quota (tier_id,attribute_name,threshold) VALUES
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'running_app_quota',5),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'component_quota',10),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'running_app_quota',1000000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'component_quota',1000000000),
	 (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'running_app_quota',1000000000),
	 (N'01ec1f84-ce3d-122e-ac9b-f10c95fd72da',N'component_quota',1000000000);
GO

INSERT INTO tierV2 (id,name,description,is_paid,created_at,is_internal) VALUES
	 (N'8de71e7a-adc2-4de4-a1b4-5b79d450f3ff',N'Developer',N'Developer tier to tryout choreo with component based pricing',0,1667189541440,0),
	 (N'352dd60e-8e14-4bb3-9dab-395a16fbfe88',N'Pay As You Go',N'Tier for paid users with component based pricing',1,1667189541440,0),
     (N'9819cdd6-d2df-47cb-8954-8c1a80cb06cc',N'Choreo Support',N'Choreo support plan with component based pricing',1,1667189541440,0),
	 (N'4abe3757-86f6-47de-994f-f02fb0522e99',N'Enterprise',N'Tier for enterprise users with component based pricing',1,1667189541440,1),
     (N'41e29802-f4bb-456c-a92e-b14cca67c4bb',N'Developer',N'Developer Paid tier for users adding payment methods with component based pricing',1,1689683632,0),
	 (N'c971b211-bc44-4f35-90ad-4d62b313b466',N'Team',N'New PAYG tier for paid users with component based pricing',1,1689683632,0),
     (N'ac5b54f5-d665-4515-ae17-95eac201ecaa',N'Developer Infrastructure',N'Tier for infrastructure costs associated with Developer Paid users',1,1689683632,0),
     (N'd3bd7035-162d-49dd-8b20-2f8bf095a889',N'Team Infrastructure',N'Tier for infrastructure costs associated with new PAYG users',1,1689683632,0),
     (N'3fa450c2-d47f-4fcb-9711-9727815cd0ee',N'Developer Platform Services',N'Tier for platform services costs associated with Developer users',1,1689683632,0),
     (N'01ee409e-cdfd-13d6-86c6-1a523acc861b',N'Team Platform Services',N'Tier for platform services costs associated with PAYG users',1,1689683632,0),
     (N'01efff00-032a-1e3e-b071-3ac566d13e0e',N'Starter Tier',N'Bijira Starter Tier for free users',0,1741778689,0),
     (N'01efff00-032a-1e3e-a268-2f9a210a4ca1',N'Pay-As-You-Go',N'Bijira Pay-As-You-Go for paid users',1,1741778689,0),
     (N'01efff00-032a-1e3e-bfeb-8eb913027aef',N'Enterprise',N'Bijira Enterprise for enterprise users',1,1741778689,0),
     (N'01efff00-032a-1e3e-98d7-c354c2553e4b',N'Support',N'Bijira Support plan for paid users',1,1741778689,0),
     (N'01efff00-032a-1e3e-8699-213ad7ff9e4e',N'Starter Tier',N'Devant Starter Tier for free users',0,1741778689,0),
     (N'01efff00-032a-1e3e-96dd-3c225a36d60c',N'Pay-As-You-Go',N'Devant Pay-As-You-Go for paid users',1,1741778689,0),
     (N'01efff00-032a-1e3e-97b5-461ca87e0d5b',N'Enterprise',N'Devant Enterprise for enterprise users',1,1741778689,0),
     (N'01efff00-032a-1e3e-a6c0-32a167db637c',N'Support',N'Devant Support plan for paid users',1,1741778689,0);
GO

INSERT INTO threshold(id,tier_id,threshold,billing_provider) values
	(N'01ee409e-cdfd-13d6-86c6-1a523acc861b',N'8de71e7a-adc2-4de4-a1b4-5b79d450f3ff', N'{"components": 5}',NULL),
	(N'01ee40ac-d78d-1a18-aefa-7525b354b84a',N'4abe3757-86f6-47de-994f-f02fb0522e99', N'{"components": 0}',NULL),
	(N'01ee40ac-d78d-1a18-ba66-569b6fbd58f5',N'352dd60e-8e14-4bb3-9dab-395a16fbfe88', N'{"components": 5}',N'stripe'),
	(N'01ee40ac-d78d-1a18-ba37-e45d158efa93',N'352dd60e-8e14-4bb3-9dab-395a16fbfe88', N'{"components": 0}',N'stripe'),
	(N'01ee40ac-d78d-1a18-af90-996025312323',N'c971b211-bc44-4f35-90ad-4d62b313b466', N'{"components": 0}',N'stripe'),
	(N'01ee40ac-d78d-1a18-8651-eb07c06ff29b',N'01eca429-cdf8-1ece-9c04-755f5d6a2c77', N'{"components": 5}',N'stripe'),
	(N'01ee40ac-d78d-1a18-9dde-008734339a61',N'01eca429-cdf8-1ece-9c04-755f5d6a2c77', N'{"components": 0}',N'azure'),
	(N'01ee40ac-d78d-1a18-b26a-3bbb0a3feedc',N'9819cdd6-d2df-47cb-8954-8c1a80cb06cc', N'{"components": 0}',N'stripe'),
	(N'01ee40ac-d78d-1a18-a135-9f837397bb80',N'd3bd7035-162d-49dd-8b20-2f8bf095a889', N'{"components": 0}',N'stripe'),
	(N'01ee8473-9bbf-1ae4-b8a5-bc3ea34ba246',N'01ee409e-cdfd-13d6-86c6-1a523acc861b',N'{"components": 0}','stripe'),
	(N'85acb04d-c935-4c62-91c5-99837fa51985',N'41e29802-f4bb-456c-a92e-b14cca67c4bb',N'{"components": 5}','stripe'),
    (N'2982035b-2936-4604-a9f4-67b16c6135d3',N'ac5b54f5-d665-4515-ae17-95eac201ecaa',N'{"components": 0}','stripe'),
    (N'cf2fe7ca-624d-4d9d-8963-1ea4ae5a04b4',N'3fa450c2-d47f-4fcb-9711-9727815cd0ee',N'{"components": 0}','stripe'),
    (N'01ef74c3-e4ed-1a96-b720-4f010f9f2055',N'c971b211-bc44-4f35-90ad-4d62b313b466',N'{"components": 0}','aws'), 
    (N'01ef74c3-e4ed-1a96-9b98-f03cb81c2fec',N'9819cdd6-d2df-47cb-8954-8c1a80cb06cc',N'{"components": 0}','aws'), 
    (N'01ef74c3-a821-1b20-9035-de544eaf403a',N'01ee409e-cdfd-13d6-86c6-1a523acc861b',N'{"components": 0}','aws'), 
    (N'01ef74c3-a821-1b20-9b8f-68548979e518',N'd3bd7035-162d-49dd-8b20-2f8bf095a889',N'{"components": 0}','aws'),
    ('01efff35-363b-19a6-a69d-7699fba96770','01efff00-032a-1e3e-b071-3ac566d13e0e','{"apis": 5, "transactions": 10000}',NULL),
    ('01efff35-363b-19a6-9c48-465545eefbc9','01efff00-032a-1e3e-a268-2f9a210a4ca1','{"apis": 5}','stripe'),
    ('01efff35-363b-19a6-8a85-717d8a8e5710','01efff00-032a-1e3e-8699-213ad7ff9e4e','{"integrations": 5}',NULL),
    ('01f0017a-2bbe-1126-a976-a7334fe5703f','01efff00-032a-1e3e-96dd-3c225a36d60c','{"integrations": 0}','stripe'),
    ('01f0017a-2bbe-1126-8e54-56453766802b','01efff00-032a-1e3e-98d7-c354c2553e4b','{"apis": 5}','stripe'),
    ('01f0017a-2bbe-1126-bd1a-a60f3149f980','01efff00-032a-1e3e-a6c0-32a167db637c','{"integrations": 0}','stripe');
GO
