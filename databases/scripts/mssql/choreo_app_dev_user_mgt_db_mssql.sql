-- Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 LLC. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.


-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_app_dev_user_mgt_db_user')
BEGIN
    CREATE USER [choreo_app_dev_user_mgt_db_user] with password = N'${choreo_app_dev_user_mgt_db_mssql_password}'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_app_dev_user_mgt_db TO choreo_app_dev_user_mgt_db_user
END;
GO

-- Create user stores table.
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[USER_STORES]') AND TYPE IN (N'U'))
CREATE TABLE USER_STORES (
    ID varchar(255) PRIMARY KEY,
    ORG_ID varchar(255) NOT NULL,
    NAME varchar(255) NOT NULL,
    FILE_CONTENT varbinary(max) NOT NULL,
    UNIQUE (ORG_ID, NAME)
);

-- Create user store associations table.
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[ORG_USER_STORE_ASSOCIATION]') AND TYPE IN (N'U'))
CREATE TABLE ORG_USER_STORE_ASSOCIATION (
    ORG_ID varchar(255) NOT NULL,
    USER_STORE_ID varchar(255) NOT NULL,
    ENVIRONMENT_ID varchar(255) NOT NULL,
    PRIORITY INT DEFAULT 1,
    UNIQUE (USER_STORE_ID, ENVIRONMENT_ID),
    FOREIGN KEY (USER_STORE_ID) REFERENCES USER_STORES(ID) ON DELETE CASCADE
);
