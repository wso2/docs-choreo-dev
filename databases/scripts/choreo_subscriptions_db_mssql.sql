-- Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tier' and xtype='U')
BEGIN
    CREATE TABLE tier (
        id VARCHAR(128) NOT NULL,
        name VARCHAR(256) NOT NULL,
        description VARCHAR(1024) NOT NULL,
        cost INTEGER NOT NULL,
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
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
        billing_date BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        status VARCHAR(128) NOT NULL,
        created_at BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        PRIMARY KEY (org_id, tier_id),
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
	 (N'01ec0a51-c895-14fe-9dc7-1b40e9c0a60a',N'step_quota',N'Number of steps can be consumed',1630383873712);
GO

INSERT INTO tier (id,name,description,cost,created_at) VALUES
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'Free',N'Free tier to tryout choreo',0,1627639797657),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'Internal Dev',N'Free tier to tryout choreo',0,1631591372000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'Choreo Internal',N'Tier for choreo internal users',0,1627639797657);
GO

INSERT INTO quota (tier_id,attribute_name,threshold) VALUES
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'service_quota',10),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'integration_quota',10),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'api_quota',10),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'remote_app_quota',10),
	 (N'01ebea3a-7735-10be-b3c0-ba95f991e877',N'step_quota',1000),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'service_quota',5),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'integration_quota',5),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'api_quota',5),
	 (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'remote_app_quota',5),
      (N'A2B419A7-8930-41D0-B813-829CC5A95C73',N'step_quota',100),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'service_quota',1000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'integration_quota',1000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'api_quota',1000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'remote_app_quota',1000000),
	 (N'01ebea43-be76-1d7a-b410-2d1b873c57af',N'step_quota',1000000);
GO
