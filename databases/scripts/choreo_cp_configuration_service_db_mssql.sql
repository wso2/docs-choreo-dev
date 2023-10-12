-- Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create configuration groups table
CREATE TABLE configuration_groups (
  [id] [int] IDENTITY(1,1) NOT NULL,
  [group_uuid] [nvarchar](50) NOT NULL,
  [group_name] [nvarchar](255) NOT NULL,
  [description] [nvarchar](255) DEFAULT NULL,
  [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  [is_system] [smallint] NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT [configuration_groups$group_uuid_unique] UNIQUE (group_uuid)
);

-- Create configuration scopes table
CREATE TABLE configuration_scopes (
  [id] [int] IDENTITY(1,1) NOT NULL,
  [deployment_track_uuid] [nvarchar](50) DEFAULT NULL,
  [component_uuid] [nvarchar](50) DEFAULT NULL,
  [project_uuid] [nvarchar](50) DEFAULT NULL,
  [organization_uuid] [nvarchar](50) NOT NULL,
  [group_id] [int] NOT NULL,
  [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT [configuration_scopes$group_id_fk] FOREIGN KEY (group_id) REFERENCES [dbo].[configuration_groups](id)
);

-- Create configuration keys table
CREATE TABLE configuration_keys (
  [id] [int] IDENTITY(1,1) NOT NULL,
  [key_uuid] [nvarchar](50) NOT NULL,
  [key_name] [nvarchar](255) NOT NULL,
  [group_id] [int] NOT NULL,
  [is_sensitive] [smallint] NOT NULL DEFAULT 0,
  [is_file] [smallint] NOT NULL DEFAULT 0,
  [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT [configuration_keys$key_uuid_unique] UNIQUE (key_uuid),
  CONSTRAINT [configuration_keys$group_id_fk] FOREIGN KEY (group_id) REFERENCES [dbo].[configuration_groups](id)
);

-- Create configuration values table
CREATE TABLE configuration_values (
  [id] [int] IDENTITY(1,1) NOT NULL,
  [value_ref] [nvarchar](255) NOT NULL,
  [key_id] [int] NOT NULL,
  [environment_uuid] [nvarchar](50) NOT NULL,
  [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT [configuration_values$key_id_fk] FOREIGN KEY (key_id) REFERENCES configuration_keys(id)
);
