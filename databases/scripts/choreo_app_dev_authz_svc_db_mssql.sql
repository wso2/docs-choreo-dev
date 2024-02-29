-- Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 LLC. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_app_dev_user_mgt_db_user')
BEGIN
    CREATE USER [choreo_app_dev_authz_svc_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_app_dev_authz_svc_db TO choreo_app_dev_authz_svc_db_user
END;
GO

-- Create the role table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[ROLE]') AND TYPE IN (N'U'))
CREATE TABLE ROLE (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    project_Id VARCHAR(50) NOT NULL,
    org_id VARCHAR(50) NOT NULL,

    CONSTRAINT unique_name_organizationId UNIQUE (name, org_id)
);

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'idx_role_project_id_org_id')
DROP INDEX ROLE.idx_role_project_id_org_id
CREATE INDEX idx_role_project_id_org_id ON ROLE (project_id, org_id);

-- Create the role_permission_mapping table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[ROLE_PERMISSION_MAPPING]') AND TYPE IN (N'U'))
CREATE TABLE ROLE_PERMISSION_MAPPING (
    role_id VARCHAR(36) NOT NULL,
    permission_name VARCHAR(255) NOT NULL,
    env_id VARCHAR(36) NOT NULL,
    org_id varchar(36) NOT NULL,
    CONSTRAINT rpm_role_fkey FOREIGN KEY (role_id) REFERENCES ROLE(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_name, env_id, org_id)
);

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'idx_rpm_role_id_env_id')
DROP INDEX ROLE_PERMISSION_MAPPING.idx_rpm_role_id_env_id
CREATE INDEX idx_rpm_role_id_env_id ON ROLE_PERMISSION_MAPPING (role_id, env_id);

-- Create the role_group_mapping table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[ROLE_GROUP_MAPPING]') AND TYPE IN (N'U'))
CREATE TABLE ROLE_GROUP_MAPPING (
    role_id VARCHAR(36) NOT NULL,
    group_name VARCHAR(255) NOT NULL,
    org_id varchar(36) NOT NULL,
    CONSTRAINT rgm_role_fkey FOREIGN KEY (role_id) REFERENCES ROLE(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, group_name, org_id)
);

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'idx_rgm_role_id_group_name')
DROP INDEX ROLE_GROUP_MAPPING.idx_rgm_role_id_group_name
CREATE INDEX idx_rgm_role_id_group_name ON ROLE_GROUP_MAPPING (role_id, group_name);
