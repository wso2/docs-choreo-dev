-- Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
--
-- This software is the property of WSO2 LLC. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

CREATE TABLE key (
  id CHAR(36) PRIMARY KEY,
  org_uuid CHAR(36) NOT NULL,
  key_hash VARCHAR(110) NOT NULL,
  type VARCHAR(20) NOT NULL,
  allowed_scopes TEXT NOT NULL,
  CONSTRAINT key_type_ck CHECK (type IN ('USER', 'APPLICATION')),
  CONSTRAINT unique_key_hash UNIQUE (key_hash)
);

CREATE TABLE pat (
  key_id CHAR(36) PRIMARY KEY,
  user_id CHAR(36),
  alias VARCHAR(50),
  description TEXT NULL,
  valid_until TIMESTAMPTZ,
  user_metadata TEXT NULL,
  CONSTRAINT pat_key_id_fkey FOREIGN KEY (key_id) REFERENCES key (id) ON DELETE CASCADE
);

CREATE TABLE api_key (
  key_id CHAR(36) PRIMARY KEY,
  apim_app_id CHAR(36),
  identifier VARCHAR(100),
  env_template_id CHAR(36),
  CONSTRAINT api_key_id_fkey FOREIGN KEY (key_id) REFERENCES key (id) ON DELETE CASCADE,
  CONSTRAINT api_key_unique_key UNIQUE (apim_app_id, identifier)
);
