-- Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
--
-- This software is the property of WSO2 LLC. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create the role table
CREATE TABLE IF NOT EXISTS role (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    project_id VARCHAR(50) NOT NULL,
    org_id VARCHAR(50) NOT NULL,

    CONSTRAINT unique_name UNIQUE (name, org_id, project_id)
);

-- Drop index if exists and recreate
DROP INDEX IF EXISTS idx_role_project_id_org_id;
CREATE INDEX idx_role_project_id_org_id ON role (project_id, org_id);

-- Create the role_permission_mapping table
CREATE TABLE IF NOT EXISTS role_permission_mapping (
    role_id VARCHAR(36) NOT NULL,
    permission_name VARCHAR(255) NOT NULL,
    env_id VARCHAR(36) NOT NULL,
    org_id VARCHAR(36) NOT NULL,
    CONSTRAINT rpm_role_fkey FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_name, env_id, org_id)
);

-- Drop index if exists and recreate
DROP INDEX IF EXISTS idx_rpm_role_id_env_id;
CREATE INDEX idx_rpm_role_id_env_id ON role_permission_mapping (role_id, env_id);

-- Create the role_group_mapping table
CREATE TABLE IF NOT EXISTS role_group_mapping (
    role_id VARCHAR(36) NOT NULL,
    group_name VARCHAR(255) NOT NULL,
    org_id VARCHAR(36) NOT NULL,
    CONSTRAINT rgm_role_fkey FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, group_name, org_id)
);

-- Drop index if exists and recreate
DROP INDEX IF EXISTS idx_rgm_role_id_group_name;
CREATE INDEX idx_rgm_role_id_group_name ON role_group_mapping (role_id, group_name);
