-- Create User
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'choreo_app_db') AND NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_app_db_user')
BEGIN
    CREATE USER [choreo_app_db_user] with password = N'${choreo_app_db_mssql_password}'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_app_db TO choreo_app_db_user
END;
GO

/* DB Objects that are related to App DB to be used by choreo-runtime */
CREATE FUNCTION [dbo].[enum2str$onprem_key$status]
(
   @setval tinyint
)
RETURNS nvarchar(max)
AS
BEGIN
RETURN
    CASE @setval
        WHEN 1 THEN 'ACTIVE'
        WHEN 2 THEN 'REVOKED'
        WHEN 3 THEN 'EXPIRED'
        ELSE ''
        END
END
GO
/****** Object:  UserDefinedFunction [dbo].[enum2str$onprem_key_subscription$status]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[enum2str$onprem_key_subscription$status]
(
   @setval tinyint
)
RETURNS nvarchar(max)
AS
BEGIN
RETURN
    CASE @setval
        WHEN 1 THEN 'ACTIVE'
        WHEN 2 THEN 'INACTIVE'
        ELSE ''
        END
END
GO
/****** Object:  UserDefinedFunction [dbo].[enum2str$support_user_creation_status$status]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[enum2str$support_user_creation_status$status]
(
   @setval tinyint
)
RETURNS nvarchar(max)
AS
BEGIN
RETURN
    CASE @setval
        WHEN 1 THEN 'completed'
        WHEN 2 THEN 'incomplete'
        ELSE ''
        END
END
GO
/****** Object:  UserDefinedFunction [dbo].[norm_enum$onprem_key$status]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[norm_enum$onprem_key$status]
(
   @setval nvarchar(max)
)
RETURNS nvarchar(max)
AS
BEGIN
RETURN dbo.enum2str$onprem_key$status(dbo.str2enum$onprem_key$status(@setval))
END
GO
/****** Object:  UserDefinedFunction [dbo].[norm_enum$onprem_key_subscription$status]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[norm_enum$onprem_key_subscription$status]
(
   @setval nvarchar(max)
)
RETURNS nvarchar(max)
AS
BEGIN
RETURN dbo.enum2str$onprem_key_subscription$status(dbo.str2enum$onprem_key_subscription$status(@setval))
END
GO
/****** Object:  UserDefinedFunction [dbo].[norm_enum$support_user_creation_status$status]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[norm_enum$support_user_creation_status$status]
(
   @setval nvarchar(max)
)
RETURNS nvarchar(max)
AS
BEGIN
RETURN dbo.enum2str$support_user_creation_status$status(dbo.str2enum$support_user_creation_status$status(@setval))
END
GO
/****** Object:  UserDefinedFunction [dbo].[str2enum$onprem_key$status]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[str2enum$onprem_key$status]
(
   @setval nvarchar(max)
)
RETURNS tinyint
AS
BEGIN
RETURN
    CASE @setval
        WHEN 'ACTIVE' THEN 1
        WHEN 'REVOKED' THEN 2
        WHEN 'EXPIRED' THEN 3
        ELSE 0
        END
END
GO
/****** Object:  UserDefinedFunction [dbo].[str2enum$onprem_key_subscription$status]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[str2enum$onprem_key_subscription$status]
(
   @setval nvarchar(max)
)
RETURNS tinyint
AS
BEGIN
RETURN
    CASE @setval
        WHEN 'ACTIVE' THEN 1
        WHEN 'INACTIVE' THEN 2
        ELSE 0
        END
END
GO
/****** Object:  UserDefinedFunction [dbo].[str2enum$support_user_creation_status$status]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[str2enum$support_user_creation_status$status]
(
   @setval nvarchar(max)
)
RETURNS tinyint
AS
BEGIN
RETURN
    CASE @setval
        WHEN 'completed' THEN 1
        WHEN 'incomplete' THEN 2
        ELSE 0
        END
END
GO
/****** Object:  Table [dbo].[app_environment_mapping]    Script Date: 9/7/2021 5:33:08 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[app_environment_mapping](
    [environment_id] [int] NOT NULL,
    [application_id] [int] NOT NULL,
    [status] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    [deployment_build_id] [nvarchar](255) NULL,
    CONSTRAINT [app_environment_mapping$app_env_mapper_key_uindex] UNIQUE CLUSTERED
(
    [environment_id] ASC,
[application_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[application]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[application](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [handle] [nvarchar](255) NOT NULL,
    [display_name] [nvarchar](255) NOT NULL,
    [working_file] [nvarchar](500) NOT NULL,
    [display_type] [nvarchar](255) NULL,
    [deploy_type] [nvarchar](255) NULL,
    [cron_schedule] [nvarchar](100) NULL,
    [template] [nvarchar](255) NOT NULL,
    [git_remote] [nvarchar](255) NOT NULL,
    [pre_built] [smallint] NULL,
    [sample_reference] [nvarchar](255) NULL,
    [docker_image] [nvarchar](255) NULL,
    [organization_id] [int] NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_application_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [application$organization_id_handle_unique] UNIQUE NONCLUSTERED
(
    [handle] ASC,
[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[beta_invitation]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[beta_invitation](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [invitation_code] [nvarchar](255) NOT NULL,
    [correlation_key] [nvarchar](255) NOT NULL,
    [invited_email] [nvarchar](255) NOT NULL,
    [signed_up_email] [nvarchar](255) NULL,
    [user_id] [int] NULL,
    [status] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_beta_invitation_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [beta_invitation$beta_invitation_correlation_key_uindex] UNIQUE NONCLUSTERED
(
[correlation_key] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[config_mapping]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[config_mapping](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [config_mapping_info_id] [int] NOT NULL,
    [config_id] [int] NULL,
    [connection_id] [int] NULL,
    [secret] [smallint] NOT NULL,
    [key_type] [nvarchar](255) NOT NULL,
    [config_key_name] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_config_mapping_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[config_mapping_info]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[config_mapping_info](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [display_name] [nvarchar](255) NOT NULL,
    [app_id] [int] NOT NULL,
    [env_id] [int] NOT NULL,
    [user_id] [int] NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_config_mapping_info_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [config_mapping_info$unique_user_app_env_ids] UNIQUE NONCLUSTERED
(
    [user_id] ASC,
    [app_id] ASC,
[env_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[configuration]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[configuration](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [organization_id] [int] NOT NULL,
    [configuration_group_id] [int] NOT NULL,
    [key] [nvarchar](255) NOT NULL,
    [value] [nvarchar](4000) NULL,
    [scope] [nvarchar](255) NOT NULL,
    [type] [nvarchar](50) NOT NULL,
    [encrypt_key_version] [nvarchar](255) NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_configuration_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [configuration$org_scoped_key_unique] UNIQUE NONCLUSTERED
(
    [organization_id] ASC,
    [key] ASC,
[scope] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[configuration_group]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[configuration_group](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [organization_id] [int] NOT NULL,
    [name] [nvarchar](255) NOT NULL,
    [display_name] [nvarchar](500) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_configuration_group_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [configuration_group$org_name_unique] UNIQUE NONCLUSTERED
(
    [organization_id] ASC,
[name] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[connection_info]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[connection_info](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [handle] [nvarchar](255) NOT NULL,
    [display_name] [nvarchar](255) NOT NULL,
    [user_account_identifier] [nvarchar](4000) NULL,
    [encrypt_key_version] [nvarchar](255) NULL,
    [organization_id] [int] NOT NULL,
    [configuration_group_id] [int] NOT NULL,
    [type] [nvarchar](255) NOT NULL,
    [connector_name] [nvarchar](255) NOT NULL,
    [owner_id] [int] NOT NULL,
    [is_shared] [smallint] NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_connection_info_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [connection_info$unique_config_group] UNIQUE NONCLUSTERED
(
[configuration_group_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [connection_info$unique_org_handle] UNIQUE NONCLUSTERED
(
    [organization_id] ASC,
[handle] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[environment]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[environment](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [display_name] [nvarchar](300) NOT NULL,
    [handle] [nvarchar](300) NOT NULL,
    [k8s_cluster_id] [int] NOT NULL,
    [k8s_namespace] [nvarchar](300) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    [organization_id] [int] NOT NULL,
    CONSTRAINT [PK_environment_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [environment$environment_name_unique] UNIQUE NONCLUSTERED
(
    [handle] ASC,
[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[group]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[group](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [display_name] [nvarchar](255) NOT NULL,
    [handle] [nvarchar](255) NOT NULL,
    [description] [nvarchar](255) NULL,
    [default_group] [smallint] NOT NULL,
    [organization_id] [int] NOT NULL,
    [created_by] [int] NOT NULL,
    [updated_by] [int] NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_group_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [group$unique_group_handle] UNIQUE NONCLUSTERED
(
    [organization_id] ASC,
[handle] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[group_member_mapping]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[group_member_mapping](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [group_id] [int] NOT NULL,
    [user_id] [int] NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT group_member_mapping_group_id_fk FOREIGN KEY (group_id) REFERENCES [group](id) ON DELETE CASCADE,
    CONSTRAINT group_member_mapping_user_id_fk FOREIGN KEY (user_id) REFERENCES [user](id) ON DELETE CASCADE,
    CONSTRAINT [PK_group_member_mapping_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [group_member_mapping$unique_group_user_mapping] UNIQUE NONCLUSTERED
(
    [group_id] ASC,
[user_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[group_role_mapping]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[group_role_mapping](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [group_id] [int] NOT NULL,
    [role_id] [int] NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_group_role_mapping_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [group_role_mapping$unique_group_role_mapping] UNIQUE NONCLUSTERED
(
    [group_id] ASC,
[role_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[group_tag]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[group_tag](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [group_id] [int] NOT NULL,
    [organization_id] [int] NOT NULL,
    [handle] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    [created_by] [int] NOT NULL,
    CONSTRAINT [PK_group_tag_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [group_tag$unique_group_tag_mapping] UNIQUE NONCLUSTERED
(
    [group_id] ASC,
[handle] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[member_invitation]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[member_invitation](
    [invitation_id] [int] IDENTITY(1,1) NOT NULL,
    [uuid] [nvarchar](255) NOT NULL,
    [organization_id] [int] NOT NULL,
    [user_email] [nvarchar](255) NOT NULL,
    [invited_groups] [nvarchar](255) NOT NULL,
    [invited_application] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_member_invitation_invitation_id] PRIMARY KEY CLUSTERED
(
[invitation_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [member_invitation$email_org_unique] UNIQUE NONCLUSTERED
(
    [user_email] ASC,
    [organization_id] ASC,
[invited_application] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[member_invitation_v2]    Script Date: 2/12/2021 10:13:48 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[member_invitation_v2](
    [invitation_id] [int] IDENTITY(1290,1) NOT NULL,
    [uuid] [nvarchar](255) NOT NULL,
    [organization_id] [int] NOT NULL,
    [user_email] [nvarchar](255) NOT NULL,
    [invited_roles] [nvarchar](255) NOT NULL,
    [invited_application] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_member_invitation_v2_invitation_id] PRIMARY KEY CLUSTERED
(
[invitation_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [member_invitation_v2$email_org_unique] UNIQUE NONCLUSTERED
(
    [user_email] ASC,
    [organization_id] ASC,
[invited_application] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[onprem_key]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[onprem_key](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [display_name] [nvarchar](255) NOT NULL,
    [handle] [nvarchar](255) NOT NULL,
    [key_value] [nvarchar](255) NOT NULL,
    [status] [nvarchar](7) NULL,
    [organization_id] [int] NULL,
    [created_by] [int] NOT NULL,
    [updated_by] [int] NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_onprem_key_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [onprem_key$key_value_unique] UNIQUE NONCLUSTERED
(
[key_value] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[onprem_key_subscription]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[onprem_key_subscription](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [status] [nvarchar](8) NULL,
    [plan] [nvarchar](255) NOT NULL,
    [organization_id] [int] NULL,
    [start_date] [datetime] NOT NULL,
    [end_date] [datetime] NOT NULL,
    [updated_date] [datetime] NOT NULL,
    CONSTRAINT [PK_onprem_key_subscription_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[organization]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[organization](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [uuid] [nvarchar](255) NOT NULL,
    [name] [nvarchar](255) NOT NULL,
    [handle] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_organization_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [organization$handle_unique] UNIQUE NONCLUSTERED
(
[handle] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[organization_user_mapping]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[organization_user_mapping](
    [user_id] [int] NOT NULL,
    [organization_id] [int] NOT NULL,
    [user_roles] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_organization_user_mapping_user_id] PRIMARY KEY CLUSTERED
(
    [user_id] ASC,
[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[role]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[role](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [display_name] [nvarchar](255) NOT NULL,
    [handle] [nvarchar](255) NOT NULL,
    [description] [nvarchar](255) NULL,
    [organization_id] [int] NOT NULL,
    [default_role] [smallint] NOT NULL,
    [created_by] [int] NOT NULL,
    [updated_by] [int] NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_role_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [role$unique_role_handle] UNIQUE NONCLUSTERED
(
    [organization_id] ASC,
[handle] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[support_user_creation_status]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[support_user_creation_status](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [idp_id] [nvarchar](255) NOT NULL,
    [status] [nvarchar](10) NULL,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_support_user_creation_status_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [support_user_creation_status$idp_id_unique_key] UNIQUE NONCLUSTERED
(
[idp_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[user]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[user](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [idp_id] [nvarchar](255) NOT NULL,
    [is_anonymous] [smallint] NOT NULL,
    [is_enterprise] [bit] NOT NULL DEFAULT 0,
    [created_at] [datetime] NOT NULL,
    [updated_at] [datetime] NOT NULL,
    CONSTRAINT [PK_user_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [user$idp_id_unique] UNIQUE NONCLUSTERED
(
[idp_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[org_env_mapping]    Script Date: 11/10/2021 4:20:00 AM ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[org_env_mapping](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [organization_id] [int] NOT NULL,
    [environment_id] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL DEFAULT getdate(),
    [updated_at] [datetime] NOT NULL DEFAULT getdate(),
    CONSTRAINT [PK_org_env_mapping_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [org_env_mapping$org_env_ids_unique] UNIQUE NONCLUSTERED
(
    [organization_id] ASC,
[environment_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Index [index_organization_id]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [index_organization_id] ON [dbo].[application]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [beta_invitation_user_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [beta_invitation_user_id_fk] ON [dbo].[beta_invitation]
(
	[user_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [config_mapping_info_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [config_mapping_info_id_fk] ON [dbo].[config_mapping]
(
	[config_mapping_info_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [app_id]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [app_id] ON [dbo].[config_mapping_info]
(
	[app_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [env_id]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [env_id] ON [dbo].[config_mapping_info]
(
	[env_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [fk_orgz_id]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [fk_orgz_id] ON [dbo].[environment]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [group_key_created_by_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [group_key_created_by_fk] ON [dbo].[group]
(
	[created_by] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [group_key_updated_by_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [group_key_updated_by_fk] ON [dbo].[group]
(
	[updated_by] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [group_member_mapping_user_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [group_member_mapping_user_id_fk] ON [dbo].[group_member_mapping]
(
	[user_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [role_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [role_id_fk] ON [dbo].[group_role_mapping]
(
	[role_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [tag_group_key_created_by_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [tag_group_key_created_by_fk] ON [dbo].[group_tag]
(
	[created_by] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [tag_org_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [tag_org_id_fk] ON [dbo].[group_tag]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [inv_organization_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [inv_organization_id_fk] ON [dbo].[member_invitation]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [inv_organization_idv2_fk]    Script Date: 2/12/2021 10:13:48 AM ******/
CREATE NONCLUSTERED INDEX [inv_organization_idv2_fk] ON [dbo].[member_invitation_v2]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [onprem_key_created_by_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [onprem_key_created_by_fk] ON [dbo].[onprem_key]
(
	[created_by] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [onprem_key_org_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [onprem_key_org_id_fk] ON [dbo].[onprem_key]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [onprem_key_updated_by_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [onprem_key_updated_by_fk] ON [dbo].[onprem_key]
(
	[updated_by] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [subscription_org_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [subscription_org_id_fk] ON [dbo].[onprem_key_subscription]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [organization_id_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [organization_id_fk] ON [dbo].[organization_user_mapping]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [role_key_created_by_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [role_key_created_by_fk] ON [dbo].[role]
(
	[created_by] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [role_key_updated_by_fk]    Script Date: 9/7/2021 5:33:08 AM ******/
CREATE NONCLUSTERED INDEX [role_key_updated_by_fk] ON [dbo].[role]
(
	[updated_by] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [dbo].[org_env_mapping]    Script Date: 11/10/2021 4:20:00 AM ******/
CREATE NONCLUSTERED INDEX [org_env_mapping_organization_id_fk] ON [dbo].[org_env_mapping]
(
	[organization_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[app_environment_mapping] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[app_environment_mapping] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[app_environment_mapping] ADD  DEFAULT (N'') FOR [deployment_build_id]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT (N'') FOR [display_type]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT (N'') FOR [deploy_type]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT (N'') FOR [cron_schedule]
    GO
ALTER TABLE [dbo].[application] ADD  CONSTRAINT [df_template]  DEFAULT ('') FOR [template]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT (N'') FOR [git_remote]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT ((0)) FOR [pre_built]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT (N'') FOR [sample_reference]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT (N'') FOR [docker_image]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[application] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[application] ADD project_id NVARCHAR (255) NOT NULL DEFAULT (N'')
    GO
ALTER TABLE [dbo].[application] ADD application_id NVARCHAR (255) NOT NULL DEFAULT (N'')
    GO
ALTER TABLE [dbo].[beta_invitation] ADD  DEFAULT (NULL) FOR [signed_up_email]
    GO
ALTER TABLE [dbo].[beta_invitation] ADD  DEFAULT (NULL) FOR [user_id]
    GO
ALTER TABLE [dbo].[beta_invitation] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[beta_invitation] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[config_mapping] ADD  DEFAULT (NULL) FOR [config_id]
    GO
ALTER TABLE [dbo].[config_mapping] ADD  DEFAULT (NULL) FOR [connection_id]
    GO
ALTER TABLE [dbo].[config_mapping] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[config_mapping] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[config_mapping_info] ADD  DEFAULT (NULL) FOR [user_id]
    GO
ALTER TABLE [dbo].[config_mapping_info] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[config_mapping_info] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[configuration] ADD  DEFAULT ((0)) FOR [configuration_group_id]
    GO
ALTER TABLE [dbo].[configuration] ADD  DEFAULT (NULL) FOR [value]
    GO
ALTER TABLE [dbo].[configuration] ADD  DEFAULT (N'') FOR [encrypt_key_version]
    GO
ALTER TABLE [dbo].[configuration] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[configuration] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[configuration_group] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[configuration_group] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[connection_info] ADD  DEFAULT (N'') FOR [user_account_identifier]
    GO
ALTER TABLE [dbo].[connection_info] ADD  DEFAULT (N'') FOR [encrypt_key_version]
    GO
ALTER TABLE [dbo].[connection_info] ADD  DEFAULT (N'sso') FOR [type]
    GO
ALTER TABLE [dbo].[connection_info] ADD  DEFAULT ((0)) FOR [owner_id]
    GO
ALTER TABLE [dbo].[connection_info] ADD  DEFAULT ((0)) FOR [is_shared]
    GO
ALTER TABLE [dbo].[connection_info] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[connection_info] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[environment] ADD  DEFAULT (N'') FOR [display_name]
    GO
ALTER TABLE [dbo].[environment] ADD  DEFAULT (N'') FOR [handle]
    GO
ALTER TABLE [dbo].[environment] ADD  DEFAULT ((-1)) FOR [k8s_cluster_id]
    GO
ALTER TABLE [dbo].[environment] ADD  DEFAULT (N'Default-Namespace') FOR [k8s_namespace]
    GO
ALTER TABLE [dbo].[environment] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[environment] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[group] ADD  DEFAULT (NULL) FOR [description]
    GO
ALTER TABLE [dbo].[group] ADD  DEFAULT ((0)) FOR [default_group]
    GO
ALTER TABLE [dbo].[group] ADD  DEFAULT (NULL) FOR [updated_by]
    GO
ALTER TABLE [dbo].[group] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[group] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[group] ADD uuid nvarchar(255) DEFAULT LOWER(newid()) NOT null UNIQUE
    GO
ALTER TABLE [dbo].[group_member_mapping] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[group_member_mapping] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[group_role_mapping] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[group_role_mapping] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[group_tag] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[group_tag] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[member_invitation] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[member_invitation] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[member_invitation_v2] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[member_invitation_v2] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[onprem_key] ADD  DEFAULT (N'ACTIVE') FOR [status]
    GO
ALTER TABLE [dbo].[onprem_key] ADD  DEFAULT (NULL) FOR [organization_id]
    GO
ALTER TABLE [dbo].[onprem_key] ADD  DEFAULT (NULL) FOR [updated_by]
    GO
ALTER TABLE [dbo].[onprem_key] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[onprem_key] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[onprem_key_subscription] ADD  DEFAULT (N'ACTIVE') FOR [status]
    GO
ALTER TABLE [dbo].[onprem_key_subscription] ADD  DEFAULT (NULL) FOR [organization_id]
    GO
ALTER TABLE [dbo].[onprem_key_subscription] ADD  DEFAULT (getdate()) FOR [start_date]
    GO
ALTER TABLE [dbo].[onprem_key_subscription] ADD  DEFAULT (getdate()) FOR [end_date]
    GO
ALTER TABLE [dbo].[onprem_key_subscription] ADD  DEFAULT (getdate()) FOR [updated_date]
    GO
ALTER TABLE [dbo].[organization] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[organization] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[organization_user_mapping] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[organization_user_mapping] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[role] ADD  DEFAULT (NULL) FOR [description]
    GO
ALTER TABLE [dbo].[role] ADD  DEFAULT (NULL) FOR [updated_by]
    GO
ALTER TABLE [dbo].[role] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[role] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[role] ADD uuid nvarchar(255) DEFAULT LOWER(newid()) NOT null UNIQUE
    GO
ALTER TABLE [dbo].[support_user_creation_status] ADD  DEFAULT (N'incomplete') FOR [status]
    GO
ALTER TABLE [dbo].[support_user_creation_status] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[support_user_creation_status] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
ALTER TABLE [dbo].[user] ADD  DEFAULT ((0)) FOR [is_anonymous]
    GO
ALTER TABLE [dbo].[user] ADD  DEFAULT (getdate()) FOR [created_at]
    GO
ALTER TABLE [dbo].[user] ADD  DEFAULT (getdate()) FOR [updated_at]
    GO
/****** Object:  Trigger [dbo].[app_environment_mapping_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TRIGGER [dbo].[app_environment_mapping_UpdateTimeTrigger] ON  [dbo].[app_environment_mapping]
FOR INSERT, UPDATE AS
BEGIN
	SET NOCOUNT ON;
UPDATE tble
SET updated_at = GETDATE()
    FROM app_environment_mapping AS tble
	INNER JOIN inserted AS i
ON tble.application_id = i.application_id AND  tble.environment_id = i.environment_id;
END;


GO
ALTER TABLE [dbo].[app_environment_mapping] ENABLE TRIGGER [app_environment_mapping_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[application_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[application_UpdateTimeTrigger] ON [dbo].[application]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [application] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[application] ENABLE TRIGGER [application_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[beta_invitation_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[beta_invitation_UpdateTimeTrigger] ON [dbo].[beta_invitation]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [beta_invitation] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[beta_invitation] ENABLE TRIGGER [beta_invitation_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[config_mapping_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[config_mapping_UpdateTimeTrigger] ON [dbo].[config_mapping]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [config_mapping] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[config_mapping] ENABLE TRIGGER [config_mapping_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[config_mapping_info_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[config_mapping_info_UpdateTimeTrigger] ON [dbo].[config_mapping_info]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [config_mapping_info] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[config_mapping_info] ENABLE TRIGGER [config_mapping_info_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[configuration_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[configuration_UpdateTimeTrigger] ON [dbo].[configuration]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [configuration] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[configuration] ENABLE TRIGGER [configuration_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[configuration_group_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[configuration_group_UpdateTimeTrigger] ON [dbo].[configuration_group]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [configuration_group] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[configuration_group] ENABLE TRIGGER [configuration_group_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[connection_info_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[connection_info_UpdateTimeTrigger] ON [dbo].[connection_info]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [connection_info] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[connection_info] ENABLE TRIGGER [connection_info_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[environment_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[environment_UpdateTimeTrigger] ON [dbo].[environment]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [environment] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[environment] ENABLE TRIGGER [environment_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[group_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[group_UpdateTimeTrigger] ON [dbo].[group]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [group] AS tble

    INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[group] ENABLE TRIGGER [group_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[group_member_mapping_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[group_member_mapping_UpdateTimeTrigger] ON [dbo].[group_member_mapping]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [group_member_mapping] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[group_member_mapping] ENABLE TRIGGER [group_member_mapping_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[group_role_mapping_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[group_role_mapping_UpdateTimeTrigger] ON [dbo].[group_role_mapping]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [group_role_mapping] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[group_role_mapping] ENABLE TRIGGER [group_role_mapping_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[group_tag_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[group_tag_UpdateTimeTrigger] ON [dbo].[group_tag]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [group_tag] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[group_tag] ENABLE TRIGGER [group_tag_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[member_invitation_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TRIGGER [dbo].[member_invitation_UpdateTimeTrigger] ON  [dbo].[member_invitation]
FOR INSERT, UPDATE AS
BEGIN
	SET NOCOUNT ON;
UPDATE tble
SET updated_at = GETDATE()
    FROM member_invitation AS tble
	INNER JOIN inserted AS i
ON tble.invitation_id = i.invitation_id;
END;

GO
ALTER TABLE [dbo].[member_invitation] ENABLE TRIGGER [member_invitation_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[member_invitation_v2_UpdateTimeTrigger]    Script Date: 2/12/2021 10:13:48 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TRIGGER [dbo].[member_invitation_v2_UpdateTimeTrigger] ON  [dbo].[member_invitation_v2]
FOR INSERT, UPDATE AS
BEGIN
	SET NOCOUNT ON;
UPDATE tble
SET updated_at = GETDATE()
    FROM member_invitation_v2 AS tble
	INNER JOIN inserted AS i
ON tble.invitation_id = i.invitation_id;
END;

GO
ALTER TABLE [dbo].[member_invitation_v2] ENABLE TRIGGER [member_invitation_v2_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[onprem_key_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[onprem_key_UpdateTimeTrigger] ON [dbo].[onprem_key]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [onprem_key] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[onprem_key] ENABLE TRIGGER [onprem_key_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[onprem_key_subscription_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TRIGGER [dbo].[onprem_key_subscription_UpdateTimeTrigger] ON  [dbo].[onprem_key_subscription]
FOR INSERT, UPDATE AS
BEGIN
	SET NOCOUNT ON;
UPDATE tble
SET updated_date = GETDATE()
    FROM onprem_key_subscription AS tble
	INNER JOIN inserted AS i
ON tble.id = i.id;
END;


GO
ALTER TABLE [dbo].[onprem_key_subscription] ENABLE TRIGGER [onprem_key_subscription_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[organization_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[organization_UpdateTimeTrigger] ON [dbo].[organization]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [organization] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[organization] ENABLE TRIGGER [organization_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[organization_user_mapping_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TRIGGER [dbo].[organization_user_mapping_UpdateTimeTrigger] ON  [dbo].[organization_user_mapping]
FOR INSERT, UPDATE AS
BEGIN
	SET NOCOUNT ON;
UPDATE tble
SET updated_at = GETDATE()
    FROM organization_user_mapping AS tble
	INNER JOIN inserted AS i
ON tble.user_id = i.user_id AND  tble.organization_id = i.organization_id;
END
GO
ALTER TABLE [dbo].[organization_user_mapping] ENABLE TRIGGER [organization_user_mapping_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[role_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[role_UpdateTimeTrigger] ON [dbo].[role]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [role] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[role] ENABLE TRIGGER [role_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[support_user_creation_status_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[support_user_creation_status_UpdateTimeTrigger] ON [dbo].[support_user_creation_status]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [support_user_creation_status] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[support_user_creation_status] ENABLE TRIGGER [support_user_creation_status_UpdateTimeTrigger]
    GO
/****** Object:  Trigger [dbo].[user_UpdateTimeTrigger]    Script Date: 9/7/2021 5:33:08 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[user_UpdateTimeTrigger] ON [dbo].[user]
    FOR INSERT, UPDATE AS
BEGIN

        SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

    FROM [user] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[user] ENABLE TRIGGER [user_UpdateTimeTrigger]
GO
/****** Object:  Trigger [dbo].[org_env_mapping_UpdateTimeTrigger]    Script Date: 11/10/2021 4:20:00 AM ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[org_env_mapping_UpdateTimeTrigger] ON [dbo].[org_env_mapping]
    FOR INSERT, UPDATE AS
BEGIN

SET NOCOUNT ON;

UPDATE tble

SET updated_at = GETDATE()

FROM [org_env_mapping] AS tble

	        INNER JOIN inserted AS i

ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[org_env_mapping] ENABLE TRIGGER [org_env_mapping_UpdateTimeTrigger]
    GO
CREATE TABLE [dbo].[component_data](
    [id] [int] IDENTITY(1172,1) NOT NULL,
    [uuid] [nvarchar](50) NOT NULL,
    [organization_handle] [nvarchar](50) NOT NULL,
    [project_uuid] [nvarchar](50) NOT NULL,
    [component_uuid] [nvarchar](50) NOT NULL,
    [environment_uuid] [nvarchar](50) NOT NULL,
    [component_version] [nvarchar](50) NOT NULL,
    [release_uuid] [nvarchar](50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT component_data$uuid_unique UNIQUE(uuid),
    CONSTRAINT component_data$release_id_unique UNIQUE(release_uuid)
)

CREATE TABLE [dbo].[configuration_mount](
    [id] [int] IDENTITY(1172,1) NOT NULL,
    [config_key_name] [nvarchar](255) NOT NULL,
    [component_data_uuid] [nvarchar](50) NOT NULL,
    [value_type] [nvarchar](50) NOT NULL,
    [is_system] [bit] NOT NULL DEFAULT 0,
    [is_required] [bit] NOT NULL DEFAULT 0,
    [metadata] [nvarchar](255) NULL,
    PRIMARY KEY (id),
    CONSTRAINT configuration_mount$component_data_uuid_fk FOREIGN KEY (component_data_uuid) REFERENCES [component_data](uuid) ON DELETE CASCADE,
    CONSTRAINT configuration_mount$component_data_uuid_key_unique UNIQUE(component_data_uuid,config_key_name)
)

CREATE TABLE [dbo].[configuration_value](
    [id] [int] IDENTITY(10893,1) NOT NULL,
    [config_mount_id] [int] NOT NULL,
    [value_ref] [nvarchar](255) NOT NULL,
    [user_idp_id] [nvarchar](50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT config_mount_id_fk FOREIGN KEY (config_mount_id) REFERENCES [configuration_mount](id) ON DELETE CASCADE
)

CREATE TABLE [dbo].[tos_consent](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [idp_id] [nvarchar](255) NOT NULL,
    [accepted] [bit] NOT NULL DEFAULT 0,
    [version] [nvarchar](10) NOT NULL,
    [service_name] [nvarchar](50) NOT NULL,
    [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT id_service_name_uk UNIQUE(idp_id, service_name)
)

CREATE TABLE [dbo].[permission]
(
    [id] [int] IDENTITY(1,1) NOT NULL ,
    [handle][varchar](255) NOT NULL,
    [display_name][varchar](255) NOT NULL,
    [domain_area][varchar](50) NOT NULL CHECK (domain_area IN('APIM-ADMIN','APIM-PUBLISHER','APIM-SUBSCRIBER','BILLING','CHOREO-DEVOPS','COMPONENT-MANAGEMENT','CONFIGURATIONS-MANAGEMENT','CUSTOM-DOMAINS', 'ENVIRONMENT-MANAGEMENT','LOG-MANAGEMENT','OBSERVABILITY-MANAGEMENT','ON-PREM-KEYS','ORGANIZATION-MANAGEMENT','PROJECT-MANAGEMENT','USER-MANAGEMENT','ACCOUNT-MANAGE','URL-MANAGEMENT')),
    [description] [varchar](255) NULL,
    [created_at] [datetime]   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at] [datetime]   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [parent_id] [int] DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT parent_id_fk FOREIGN KEY (parent_id) REFERENCES permission(id),
    CONSTRAINT unique_handle   UNIQUE(handle)
)

CREATE TABLE [dbo].[role_permission_mapping]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [role_id] [int] NOT NULL,
    [permission_id] [int] NOT NULL,
    [created_at] [datetime]   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at]   [datetime]    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT role_permission_mapping_role_id_fk FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT role_permission_mapping_permission_id_fk FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE,
    CONSTRAINT unique_role_permission_mapping   UNIQUE(role_id,permission_id)
)

CREATE TABLE [dbo].[role_member_mapping]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [role_id] [int] NOT NULL,
    [user_id] [int] NOT NULL,
    [created_at] [datetime]   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at]   [datetime]    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT role_member_mapping_role_id_fk FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT role_member_mapping_user_id_fk FOREIGN KEY (user_id) REFERENCES [user](id) ON DELETE CASCADE,
    CONSTRAINT unique_role_member_mapping   UNIQUE(role_id,user_id)
)

CREATE TABLE [dbo].[role_tag]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [role_id] [int] NOT NULL,
    [organization_id] [int] NOT NULL,
    [handle][varchar](255) NOT NULL,
    [created_at] [datetime]   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at]   [datetime]    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [created_by] [int] NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT tag_org_id_fk FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT tag_role_id_fk FOREIGN KEY (role_id) REFERENCES [role](id) ON DELETE CASCADE,
    CONSTRAINT tag_role_key_created_by_fk FOREIGN KEY (created_by) REFERENCES [user](id),
    CONSTRAINT unique_role_tag_mapping   UNIQUE(role_id,handle)
)

CREATE TABLE [dbo].[org_custom_theme]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [organization_id] [int] NOT NULL,
    [organization_uuid] [nvarchar](255) NOT NULL,
    [theme_name] [varchar](50) NOT NULL DEFAULT (N'default'),
    [config] [nvarchar](4000) NOT NULL,
    [version] [varchar](15) NOT NULL,
    [is_live] [bit] NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT theme_org_id_fk FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE
)

CREATE TABLE [dbo].[org_self_signup_config]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [organization_uuid] [nvarchar](255) NOT NULL,
    [is_enabled] [bit] NOT NULL DEFAULT 1,
    [is_auto_approval_enabled] [bit] NOT NULL DEFAULT 1,
    [is_custom_impl] [bit] NOT NULL DEFAULT 0,
    [custom_endpoint] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT unique_org_self_signup_config UNIQUE (organization_uuid),
    CONSTRAINT signup_config_org_uuid_fk FOREIGN KEY (organization_uuid) REFERENCES organization(uuid) ON DELETE CASCADE
);

CREATE TABLE [dbo].[org_self_signup_approval_request]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [organization_uuid] [nvarchar](255) NOT NULL,
    [user_idp_id] [nvarchar](255) NOT NULL,
    [email] [nvarchar](255) NOT NULL,
    [status] [nvarchar](255) NOT NULL CHECK (status IN('pending', 'approved', 'rejected')) DEFAULT 'pending',
    [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT unique_org_self_signup_approval_request UNIQUE(organization_uuid, user_idp_id)
    CONSTRAINT signup_config_org_uuid_fk FOREIGN KEY (organization_uuid) REFERENCES organization(uuid) ON DELETE CASCADE
)

CREATE TABLE [dbo].[enterprise_group_role_mapping]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [organization_id] [int] NOT NULL,
    [role_list] [nvarchar](255) NOT NULL,
    [group_name] [nvarchar](1000) NOT NULL,
    [created_at] [datetime]   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at]   [datetime]    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT enterprise_group_role_mapping_org_id_fk FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE
)

/****** Object:  Trigger [dbo].[enterprise_group_role_mapping_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[enterprise_group_role_mapping_UpdateTimeTrigger] ON [dbo].[enterprise_group_role_mapping]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [enterprise_group_role_mapping] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[enterprise_group_role_mapping] ENABLE TRIGGER [enterprise_group_role_mapping_UpdateTimeTrigger]
    GO

CREATE TABLE [dbo].[org_enterprise_login_config]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [organization_uuid] [nvarchar](255) NOT NULL,
    [is_eidp_enabled] [bit] NOT NULL DEFAULT 0,
    [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT unique_org_enterprise_login_config UNIQUE(organization_uuid)
    CONSTRAINT signup_config_org_uuid_fk FOREIGN KEY (organization_uuid) REFERENCES organization(uuid) ON DELETE CASCADE
)

CREATE TABLE [dbo].[org_activity]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [org_id] [int] NOT NULL,
    [last_login] [datetime],
    [last_job_run] [datetime],
    [last_api_invocation] [datetime],
    [last_ballerina_pkg_update] [datetime],
    [marked_for_deletion] [bit] NOT NULL DEFAULT 0,
    [is_deleted] [bit] NOT NULL DEFAULT 0,
    [deleted_time] [datetime],
    PRIMARY KEY (id),
    CONSTRAINT unique_org_activity UNIQUE(org_id)
);

/****** Object:  Trigger [dbo].[org_enterprise_login_config_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[org_enterprise_login_config_UpdateTimeTrigger] ON [dbo].[org_enterprise_login_config]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [org_enterprise_login_config] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[org_enterprise_login_config] ENABLE TRIGGER [org_enterprise_login_config_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[org_self_signup_config_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[org_self_signup_config_UpdateTimeTrigger] ON [dbo].[org_self_signup_config]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [org_self_signup_config] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[org_self_signup_config] ENABLE TRIGGER [org_self_signup_config_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[org_self_signup_approval_request_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[org_self_signup_approval_request_UpdateTimeTrigger] ON [dbo].[org_self_signup_approval_request]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [org_self_signup_approval_request] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[org_self_signup_approval_request] ENABLE TRIGGER [org_self_signup_approval_request_UpdateTimeTrigger]
    GO

/****** Object:  Table [dbo].[user_migration_info]    Script Date: 12/07/2021 5:40:00 PM ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[user_migration_info](
    [email] [nvarchar](255) NOT NULL,
    [v1_idpid] [nvarchar](255) NOT NULL,
    [selected] [smallint] NOT NULL DEFAULT 0,
    [is_complete] [smallint] NOT NULL DEFAULT 0,
    CONSTRAINT [user_migration$email_vi_idpid_unique] UNIQUE NONCLUSTERED
(
    [email] ASC,
[v1_idpid] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO

CREATE TABLE [dbo].[global_configuration](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [uuid] [nvarchar](50) NOT NULL,
    [name] [nvarchar](255) NOT NULL,
    [description] [nvarchar](255) NULL,
    [config_type] [nvarchar](50) NOT NULL CHECK (config_type IN('Pre-defined', 'User-defined')),
    [config_type_metadata] [nvarchar](255) NOT NULL,
    [org_uuid] [nvarchar](50) NOT NULL,
    [project_uuid] [nvarchar](50) NOT NULL,
    [environment_uuid] [nvarchar](50) NOT NULL,
    [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT [global_configuration$uuid_unique] UNIQUE (uuid)
)

CREATE TABLE [dbo].[global_configuration_data](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [config_key] [nvarchar](255)  NOT NULL,
    [value_type] [nvarchar](50) NOT NULL,
    [config_uuid] [nvarchar](50) NOT NULL,
    [value_ref] [nvarchar](255) NOT NULL,
    [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT [global_configuration_data$config_uuid_fk] FOREIGN KEY (config_uuid) REFERENCES dbo.global_configuration(uuid)
)

CREATE TABLE [dbo].[suspended_members]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    [user_idp_id] [nvarchar](255) NOT NULL,
    [organization_uuid] [nvarchar](255) NOT NULL,
    [user_email] [nvarchar](255),
    [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT unique_suspended_members UNIQUE(user_idp_id, organization_uuid)
)

CREATE TABLE [dbo].[enterprise_group_mapping]
(
    [id] [int] IDENTITY(1,1) NOT NULL,
    enterprise_group_name [nvarchar](255) NOT NULL,
    choreo_group_uuid [nvarchar](255) NOT NULL,
    [organization_id] [int] NOT NULL,
    [created_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    [updated_at] [datetime] NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT unique_enterprise_group_mapping UNIQUE(enterprise_group_name, choreo_group_uuid, organization_id),
    CONSTRAINT enterprise_group_mapping$group_uuid_fk FOREIGN KEY (choreo_group_uuid) REFERENCES [group](uuid) ON DELETE CASCADE
)

/****** Object:  Trigger [dbo].[enterprise_group_mapping_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[enterprise_group_mapping_UpdateTimeTrigger] ON [dbo].[enterprise_group_mapping]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [enterprise_group_mapping] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[enterprise_group_mapping] ENABLE TRIGGER [enterprise_group_mapping_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[global_configuration_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[global_configuration_UpdateTimeTrigger] ON [dbo].[global_configuration]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [global_configuration] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[global_configuration] ENABLE TRIGGER [global_configuration_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[global_configuration_data_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[global_configuration_data_UpdateTimeTrigger] ON [dbo].[global_configuration_data]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [global_configuration_data] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[global_configuration_data] ENABLE TRIGGER [global_configuration_data_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[permission_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[permission_UpdateTimeTrigger] ON [dbo].[permission]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [permission] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[permission] ENABLE TRIGGER [permission_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[role_permission_mapping_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[role_permission_mapping_UpdateTimeTrigger] ON [dbo].[role_permission_mapping]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [role_permission_mapping] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[role_permission_mapping] ENABLE TRIGGER [role_permission_mapping_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[role_member_mapping_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[role_member_mapping_UpdateTimeTrigger] ON [dbo].[role_member_mapping]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [role_member_mapping] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[role_member_mapping] ENABLE TRIGGER [role_member_mapping_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[role_tag_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[role_tag_UpdateTimeTrigger] ON [dbo].[role_tag]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [role_tag] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[role_tag] ENABLE TRIGGER [role_tag_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[tos_consent] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[tos_consent_UpdatedTimeTrigger] ON [dbo].[tos_consent]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [tos_consent] AS tble
             INNER JOIN inserted AS i
                        ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[tos_consent] ENABLE TRIGGER [tos_consent_UpdatedTimeTrigger]
GO

/****** Add default Permission list ******/
-- APIM-ADMIN
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Admin Operations','apim:admin','APIM-ADMIN','Manage all admin operations');

-- APIM-PUBLISHER
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage API Developer Portal','apim:publisher_settings','APIM-PUBLISHER','Manage API Developer portal settings');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage APIs','apim:api_manage','APIM-PUBLISHER','View, create, delete and publish APIs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage API Tiers','apim:tier_manage','APIM-ADMIN','View, update and delete throttling policies');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage API Subscriptions','apim:subscription_manage','APIM-PUBLISHER','View and block API subscriptions');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Internal Application Management','apim:dcr:app_manage','APIM-PUBLISHER','Manage internal applications');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Publish API only','apim:api_publish','APIM-PUBLISHER','Manage API Publication');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View APIs','apim:api_view','APIM-PUBLISHER','View APIs');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View API Tiers','apim:tier_view','APIM-PUBLISHER','View API Tiers');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Generate Internal Key','apim:api_generate_key','APIM-PUBLISHER','Generate internal key');

-- APIM-SUBSCRIBER
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Manage API documentations','apim:document_manage','APIM-SUBSCRIBER','Manage API documentations');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Manage Settings for APIs','apim:api_settings','APIM-SUBSCRIBER','Manage API settings');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('API Subscriptions view only','apim:subscription_view','APIM-SUBSCRIBER','View only API subscriptions');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Production Application Credentials','apim:prod_key_manage','APIM-SUBSCRIBER','View, generate and update production credentials of an application');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Sandbox Application Credentials','apim:sand_key_manage','APIM-SUBSCRIBER','View, generate and update sandbox credentials of an application');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('API Subscribe','apim:subscribe', 'APIM-SUBSCRIBER','Subscribe to APIs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Application Management','apim:app_manage', 'APIM-SUBSCRIBER','Retrieve and manage applications');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Subscription Management','apim:sub_manage', 'APIM-SUBSCRIBER','Retrieve and manage subscriptions');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Application Import and Export','apim:app_import_export', 'APIM-SUBSCRIBER','Import and export applications');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Generate API Keys','apim:api_key', 'APIM-SUBSCRIBER','Generate API keys');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Production Environment','environments:view_prod','APIM-SUBSCRIBER','View production environment');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Development Environment','environments:view_dev','APIM-SUBSCRIBER','View development environment');

-- BILLING
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Subscription Tiers','billing:tier_view','BILLING','View subscription tiers');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Invoices','billing:invoice_view','BILLING','View invoices');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Subscriptions','billing:subscription_manage','BILLING','Manage subscriptions');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Payment Methods','billing:payment_method_manage','BILLING','Manage payment methods');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Billing Account','billing:org_manage','BILLING','Manage billing account');

-- CHOREO-DEVOPS
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Deployment Manage','choreo:deployment_manage','CHOREO-DEVOPS','Manage Component Deployment to Prod');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View Devops Deployment','urn:choreosystem:choreodevopsportalapi:deployment_view','CHOREO-DEVOPS','View devops deployment');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Manage Devops Deployment','urn:choreosystem:choreodevopsportalapi:deployment_manage','CHOREO-DEVOPS','Manage devops deployment');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Manage Devops Component','urn:choreosystem:choreodevopsportalapi:component_manage','CHOREO-DEVOPS','Manage devops component');

-- COMPONENT-MANAGEMENT
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View Component Utilities Files','urn:choreosystem:componentutils:component_file_view','COMPONENT-MANAGEMENT','View component utilities files');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Trigger Component Utilities','urn:choreosystem:componentutils:component_trigger','COMPONENT-MANAGEMENT','Trigger component utilities');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Manage Component Utilities','urn:choreosystem:componentutils:component_manage','COMPONENT-MANAGEMENT','Manage component utilities');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Trigger Component','urn:choreosystem:componentsmanagement:component_trigger','COMPONENT-MANAGEMENT','Trigger component');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Create Component','urn:choreosystem:componentsmanagement:component_create','COMPONENT-MANAGEMENT','Create component');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View Component Configuration','urn:choreosystem:componentsmanagement:component_config_view','COMPONENT-MANAGEMENT','View component configuration');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View Component Logs','urn:choreosystem:componentsmanagement:component_logs_view','COMPONENT-MANAGEMENT','View component logs');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View Component Initialization Groups','urn:choreosystem:componentsmanagement:component_init_view','COMPONENT-MANAGEMENT','View component initialization');INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View Component Files','urn:choreosystem:componentsmanagement:component_file_view','COMPONENT-MANAGEMENT','View component files');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Manage Component','urn:choreosystem:componentsmanagement:component_manage','COMPONENT-MANAGEMENT','Manage component');

-- CONFIGURATIONS-MANAGEMENT
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Global Configs','urn:choreosystem:configmanagement:global_config_manage','CONFIGURATIONS-MANAGEMENT','Create, Edit and Delete Global Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Global Configs','urn:choreosystem:configmanagement:global_config_view','CONFIGURATIONS-MANAGEMENT','View Global Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Create Global Configs','urn:choreosystem:configmanagement:global_config_create','CONFIGURATIONS-MANAGEMENT','Create Global Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete Global Configs','urn:choreosystem:configmanagement:global_config_delete','CONFIGURATIONS-MANAGEMENT','Delete Global Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Update Global Configs','urn:choreosystem:configmanagement:global_config_update','CONFIGURATIONS-MANAGEMENT','Update Global Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Configs','urn:choreosystem:configmanagement:config_manage','CONFIGURATIONS-MANAGEMENT','Create, Edit and Delete Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Configs','urn:choreosystem:configmanagement:config_view','CONFIGURATIONS-MANAGEMENT','View Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Create Configs','urn:choreosystem:configmanagement:config_create','CONFIGURATIONS-MANAGEMENT','Create Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete Configs','urn:choreosystem:configmanagement:config_delete','CONFIGURATIONS-MANAGEMENT','Delete Configs');

-- CUSTOM-DOMAINS
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Custom Domains','urn:choreosystem:customdomainapi:custom_domain_manage','CUSTOM-DOMAINS','Create, Edit and Delete Custom Domains');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Custom Domains','urn:choreosystem:customdomainapi:custom_domain_view','CUSTOM-DOMAINS','View Custom Domains');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Create Custom Domains','urn:choreosystem:customdomainapi:custom_domain_create','CUSTOM-DOMAINS','Create Custom Domains');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete Custom Domains','urn:choreosystem:customdomainapi:custom_domain_delete','CUSTOM-DOMAINS','Delete Custom Domains');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Update Custom Domains','urn:choreosystem:customdomainapi:custom_domain_update','CUSTOM-DOMAINS','Update Custom Domains');

-- URL-MANAGEMENT
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Custom Domains','choreo:domain_manage','URL-MANAGEMENT','Manage Custom Domains');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Custom Domains','choreo:domain_view','URL-MANAGEMENT','View Custom Domains');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage URL Mappings','choreo:url_mapping_manage','URL-MANAGEMENT','Manage URL Mappings');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Approve URL Mappings','choreo:url_mapping_approve','URL-MANAGEMENT','Approve URL Mappings');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View URL Mappings','choreo:url_mapping_view','URL-MANAGEMENT','View URL Mappings');

-- ENVIRONMENT-MANAGEMENT
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Non Production Environment Manage','choreo:non_prod_env_manage','ENVIRONMENT-MANAGEMENT','Manage operations on Choreo Non Production environment');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Development Environment Manage','choreo:dev_env_manage','ENVIRONMENT-MANAGEMENT','Manage operations on Choreo Development environment'); -- deprecated
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Production Environment Manage','choreo:prod_env_manage','ENVIRONMENT-MANAGEMENT','Manage operations on Choreo Production environment');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Manage Environment','apim:environment_manage','ENVIRONMENT-MANAGEMENT','Create, Edit and Delete Environments');

-- LOG-MANAGEMENT
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Audit Logs','urn:choreosystem:choreoauditloggingapi:audit_logs_view','LOG-MANAGEMENT','View audit logs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Audit Logs','urn:choreosystem:choreoauditloggingapi:audit_logs_manage', 'LOG-MANAGEMENT','Manage audit logs');

-- OBSERVABILITY-MANAGEMENT
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Non-Prod Logs','choreo:log_view_non_prod','OBSERVABILITY-MANAGEMENT','View non-production environment logs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Prod Logs','choreo:log_view_prod','OBSERVABILITY-MANAGEMENT','View production environment logs');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View Organization Insights', 'choreo:insights_org_view', 'OBSERVABILITY-MANAGEMENT', 'View Organization level Insights');

-- ON-PREM-KEYS
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage OnPrem Keys','urn:choreosystem:onpremkeymanagement:on_prem_key_manage','ON-PREM-KEYS','Create, Edit and Delete OnPrem Keys');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View OnPrem Keys','urn:choreosystem:onpremkeymanagement:on_prem_key_view','ON-PREM-KEYS','View OnPrem Keys');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Create OnPrem Keys','urn:choreosystem:onpremkeymanagement:on_prem_key_create','ON-PREM-KEYS','Create OnPrem Keys');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete OnPrem Keys','urn:choreosystem:onpremkeymanagement:on_prem_key_delete','ON-PREM-KEYS','Delete OnPrem Keys');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Update OnPrem Keys','urn:choreosystem:onpremkeymanagement:on_prem_key_update','ON-PREM-KEYS','Update OnPrem Keys');

-- ORGANIZATION-MANAGEMENT
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Custom Theme','urn:choreosystem:organizationmanagement:theme_manage','ORGANIZATION-MANAGEMENT','Create, Edit and Delete Custom Theme');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Custom Theme','urn:choreosystem:organizationmanagement:theme_view','ORGANIZATION-MANAGEMENT','View Custom Theme');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Create Custom Theme','urn:choreosystem:organizationmanagement:theme_create','ORGANIZATION-MANAGEMENT','Create Custom Theme');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete Custom Theme','urn:choreosystem:organizationmanagement:theme_delete','ORGANIZATION-MANAGEMENT','Delete Custom Theme');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Deploy Custom Theme','urn:choreosystem:organizationmanagement:theme_deploy','ORGANIZATION-MANAGEMENT','Deploy Custom Theme');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Self Signup Configs and Approvals','urn:choreosystem:organizationmanagement:self_signup_manage','ORGANIZATION-MANAGEMENT','Create, Update Self Signup Configs and Approval');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Self Signup Configs','urn:choreosystem:organizationmanagement:self_signup_config_view','ORGANIZATION-MANAGEMENT','View Self Signup Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Self Signup Approvals','urn:choreosystem:organizationmanagement:self_signup_approval_view','ORGANIZATION-MANAGEMENT','View Self Signup Approvals');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Update Self Signup Approvals','urn:choreosystem:organizationmanagement:self_signup_approval_update','ORGANIZATION-MANAGEMENT','Update Self Signup Approvals');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Update Self Signup Configs','urn:choreosystem:organizationmanagement:self_signup_config_update','ORGANIZATION-MANAGEMENT','Update Self Signup Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Enterprise Login Configs','urn:choreosystem:organizationmanagement:enterprise_login_config_manage','ORGANIZATION-MANAGEMENT','Create, Edit and Delete Enterprise Login Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Enterprise Login Configs','urn:choreosystem:organizationmanagement:enterprise_login_config_view','ORGANIZATION-MANAGEMENT','View Enterprise Login Configs');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Organization','urn:choreosystem:organizationapi:org_manage','ORGANIZATION-MANAGEMENT','Create, update and delete organization');

-- PROJECT-MANAGEMENT
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Project Management','choreo:project_manage','PROJECT-MANAGEMENT','Retrieve and manage projects');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('Component manage','choreo:component_manage','PROJECT-MANAGEMENT','Manage operations on components');
INSERT INTO permission (display_name, handle, domain_area, description) VALUES ('View Project','choreo:project_view','PROJECT-MANAGEMENT','View Project');

-- USER-MANAGEMENT
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Users','urn:choreosystem:usermanagement:user_view', 'USER-MANAGEMENT','View Users');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete Users','urn:choreosystem:usermanagement:user_delete ', 'USER-MANAGEMENT','Delete Users');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Update Users','urn:choreosystem:usermanagement:user_update', 'USER-MANAGEMENT','Update Users');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Permissions','urn:choreosystem:usermanagement:permission_view', 'USER-MANAGEMENT','View Permissions');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Groups','urn:choreosystem:usermanagement:role_mapping_view','USER-MANAGEMENT','View Group role mappings');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Create Groups','urn:choreosystem:usermanagement:role_mapping_create','USER-MANAGEMENT','Create Group role mappings');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Update Groups','urn:choreosystem:usermanagement:role_mapping_update','USER-MANAGEMENT','Update Group role mappings');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete Groups','urn:choreosystem:usermanagement:role_mapping_delete','USER-MANAGEMENT','Delete Group role mappings');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Groups','urn:choreosystem:usermanagement:role_mapping_manage','USER-MANAGEMENT','Create, Edit and Delete Group role mappings');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Roles','urn:choreosystem:usermanagement:role_view','USER-MANAGEMENT','View Roles');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Create Roles','urn:choreosystem:usermanagement:role_create','USER-MANAGEMENT','Create Roles');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Update Roles','urn:choreosystem:usermanagement:role_update','USER-MANAGEMENT','Update Roles');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete Roles','urn:choreosystem:usermanagement:role_delete','USER-MANAGEMENT','Delete Roles');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Roles','urn:choreosystem:usermanagement:role_manage','USER-MANAGEMENT','Create, update and delete roles');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('View Invitations','urn:choreosystem:usermanagement:invitation_view', 'USER-MANAGEMENT','View Invitations'); 
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Send Invitations','urn:choreosystem:usermanagement:invitation_send', 'USER-MANAGEMENT','Send Invitations'); 
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Delete Invitations','urn:choreosystem:usermanagement:invitation_delete', 'USER-MANAGEMENT','Delete Invitations');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Invitations','urn:choreosystem:usermanagement:invitation_manage','USER-MANAGEMENT','Manage Invitations');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('Manage Users','urn:choreosystem:usermanagement:user_manage','USER-MANAGEMENT','Add and remove users');
