-- Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create user
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_configuration_mapping_service_admin_db_user')
BEGIN
    CREATE USER [choreo_configuration_mapping_service_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_configuration_mapping_service_db TO choreo_configuration_mapping_service_db_user
END;
GO

-- Create configuration mappings table
CREATE TABLE configuration_mappings (
  [id] [int] IDENTITY(1,1) NOT NULL,
  [mapping_uuid] [nvarchar](50) NOT NULL,
  [org_uuid] [nvarchar](50) NOT NULL,
  [project_uuid] [nvarchar](50) NOT NULL,
  [component_uuid] [nvarchar](50) NOT NULL,
  [env_template_id] [nvarchar](50) NOT NULL,
  [deployment_track_id] [nvarchar](50) NOT NULL,
  [internal_group_id] [nvarchar](50) NULL,
  [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT [configuration_mappings$mapping_uuid_unique] UNIQUE (mapping_uuid)
);

CREATE TABLE configuration_mapping_keys (
  [id] [int] IDENTITY(1,1) NOT NULL,
  [key_uuid] [nvarchar](50) NOT NULL,
  [key_name] [nvarchar](255) NOT NULL,
  [configuration_mapping_id] [int] NOT NULL,
  [cg_group_uuid] [nvarchar](50),
  [cg_key_uuid] [nvarchar](50),
  [is_dynamic] [smallint] NOT NULL DEFAULT 0,
  [is_sensitive] [smallint] NOT NULL DEFAULT 0,
  [is_file] [smallint] NOT NULL DEFAULT 0,
  [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT [configuration_mapping_keys$key_uuid_unique] UNIQUE (key_uuid),
  CONSTRAINT [configuration_mapping_keys$configuration_mapping_id_fk] FOREIGN KEY (configuration_mapping_id) REFERENCES [configuration_mappings](id)
);

CREATE TABLE configuration_mapping_values (
  [id] [int] IDENTITY(1,1) NOT NULL,
  [value_ref] [nvarchar](255) NOT NULL,
  [mapping_key_id] [int] NOT NULL,
  [environment_uuid] [nvarchar](50) NOT NULL,
  [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT [configuration_mapping_values$key_id_fk] FOREIGN KEY (mapping_key_id) REFERENCES [configuration_mapping_keys](id)
);
