-- Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create user
IF (SELECT name FROM sys.databases WHERE name = N'choreo_desired_store_db') AND NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_desired_store_db_user')
BEGIN
    CREATE USER [choreo_desired_store_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_desired_store_db TO choreo_desired_store_db_user
END;
GO

-- Create kind table
CREATE TABLE kind (
  value JSONB
);
