-- Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
--
-- This software is the property of WSO2 LLC. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create user stores table if it doesn't exist
CREATE TABLE IF NOT EXISTS user_stores (
    id VARCHAR(255) PRIMARY KEY,
    org_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    file_content BYTEA NOT NULL,
    UNIQUE (org_id, name)
);

-- Create user store associations table if it doesn't exist
CREATE TABLE IF NOT EXISTS org_user_store_association (
    org_id VARCHAR(255) NOT NULL,
    user_store_id VARCHAR(255) NOT NULL,
    environment_id VARCHAR(255) NOT NULL,
    priority INTEGER DEFAULT 1,
    UNIQUE (user_store_id, environment_id),
    FOREIGN KEY (user_store_id) REFERENCES user_stores(id) ON DELETE CASCADE
);
