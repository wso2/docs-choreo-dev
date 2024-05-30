-- Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create user
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_database WHERE datname = 'choreo_declarative_api_db') AND 
       NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'choreo_declarative_api_db_user') THEN
        CREATE USER choreo_declarative_api_db_user WITH ENCRYPTED PASSWORD '<STRONG_PASSWORD>';
        GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO choreo_declarative_api_db_user;
    END IF;
END $$;

-- Create kind table
CREATE TABLE actual_Store (
  payload JSONB
);

-- Create kind table
CREATE TABLE desired_store (
  payload JSONB
);
