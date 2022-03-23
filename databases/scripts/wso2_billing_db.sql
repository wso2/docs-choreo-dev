-- Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'billing_db_user')
BEGIN
    CREATE USER [billing_db_user] FOR LOGIN [billing_db_user]
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::billing_db TO billing_db_user
END;
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='billing_plan' and xtype='U')
BEGIN
    CREATE TABLE billing_plan (
        id VARCHAR(128) NOT NULL,
        tier_id VARCHAR(128) NOT NULL,
        product_id VARCHAR(128) NOT NULL,
        price_id VARCHAR(128) NOT NULL,
        cloud_type VARCHAR(10) NOT NULL,
        UNIQUE (product_id),
        UNIQUE (price_id),
        UNIQUE (tier_id),
        PRIMARY KEY (id)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='billing_support_plan' and xtype='U')
BEGIN
    CREATE TABLE billing_support_plan (
        id VARCHAR(128) NOT NULL,
        name VARCHAR(256) NOT NULL,
        cost INTEGER NOT NULL, 
        product_id VARCHAR(128) NOT NULL,
        price_id VARCHAR(128) NOT NULL,
        cloud_type VARCHAR(10) NOT NULL,
        UNIQUE (product_id),
        UNIQUE (price_id),
        PRIMARY KEY (id)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='billing_account' and xtype='U')
BEGIN
    CREATE TABLE billing_account (
        id VARCHAR(128) NOT NULL,
        org_id VARCHAR(128) NOT NULL,
        customer_id VARCHAR(128) NOT NULL,
        UNIQUE (org_id),
        UNIQUE (customer_id),
        PRIMARY KEY (id)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='billing_subscription' and xtype='U')
BEGIN
    CREATE TABLE billing_subscription (
        id VARCHAR(128) NOT NULL,
        billing_tier_id VARCHAR(128),
        billing_support_plan_id VARCHAR(128),
        billing_account_id VARCHAR(128) NOT NULL,
        subscription_id VARCHAR(128),
        stripe_subscription_id VARCHAR(128) NOT NULL,
        stripe_subscription_item_id VARCHAR(128) NOT NULL,
        cloud_type VARCHAR(10) NOT NULL,
        subscription_type VARCHAR(256) NOT NULL,
        UNIQUE (stripe_subscription_item_id),
        PRIMARY KEY (id, stripe_subscription_id),
        CONSTRAINT FK_Tier FOREIGN KEY (billing_tier_id) REFERENCES billing_plan(id),
        CONSTRAINT FK_Support_plan FOREIGN KEY (billing_support_plan_id) REFERENCES billing_support_plan(id),
        CONSTRAINT FK_account_subscription FOREIGN KEY (billing_account_id) REFERENCES billing_account(id) ON DELETE CASCADE ON UPDATE CASCADE
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='billing_history' and xtype='U')
BEGIN
    CREATE TABLE billing_history (
        id VARCHAR(128) NOT NULL,
        billing_account_id VARCHAR(128) NOT NULL,
        user_id VARCHAR(256) NOT NULL,
        timestamp BIGINT DEFAULT DATEDIFF_BIG(MILLISECOND,'1970-01-01 00:00:00.000', SYSUTCDATETIME()),
        operation VARCHAR(128) NOT NULL,
        cloud_type VARCHAR(10) NOT NULL,
        PRIMARY KEY (id)
    );
END
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='billing_invoice' and xtype='U')
BEGIN
    CREATE TABLE billing_invoice (
        id VARCHAR(128) NOT NULL,
        billing_account_id VARCHAR(128) NOT NULL,
        invoice_id VARCHAR(128) NOT NULL,
        invoice_line_id VARCHAR(128) NOT NULL,
        cloud_type VARCHAR(10) NOT NULL,
        PRIMARY KEY (invoice_id,invoice_line_id),
        CONSTRAINT FK_account_innvoice FOREIGN KEY (billing_account_id) REFERENCES billing_account(id) ON DELETE CASCADE ON UPDATE CASCADE
    );
END
GO
