-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_app_db_user')
BEGIN
    CREATE USER [choreo_app_db_user] FOR LOGIN [choreo_app_db_user]
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
CREATE TABLE [dbo].[configuration_mount](
    [id] [int] IDENTITY(1172,1) NOT NULL,
    [config_key_name] [nvarchar](255) NOT NULL,
    [organization_id] [nvarchar](50) NOT NULL,
    [project_id] [nvarchar](50) NOT NULL,
    [component_id] [nvarchar](50) NOT NULL,
    [environment_id] [nvarchar](50) NOT NULL,
    [component_version] [nvarchar](50) NOT NULL,
    [value_type] [nvarchar](50) NOT NULL,
    CONSTRAINT [PK_configuration_mount_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [configuration_mount$org_project_component_version_env_key_unique] UNIQUE NONCLUSTERED
(
    [organization_id] ASC,
    [project_id] ASC,
    [component_id] ASC,
    [component_version] ASC,
    [environment_id] ASC,
    [config_key_name] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[configuration_mount] ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[configuration_value](
    [id] [int] IDENTITY(10893,1) NOT NULL,
    [config_mount_id] [int] NOT NULL,
    [key_name] [nvarchar](255) NOT NULL,
    [value_ref] [nvarchar](255) NOT NULL,
    [user_id] [nvarchar](50) NOT NULL,
    CONSTRAINT [PK_configuration_value_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [configuration_value$key_unique] UNIQUE NONCLUSTERED
(
    [key_name] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[configuration_value] ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
/****** Object:  Index [config_mount_id_fk] ******/
CREATE NONCLUSTERED INDEX [config_mount_id_fk] ON [dbo].[configuration_value]
(
	[config_mount_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[configuration_value] DROP COLUMN [user_id]
    GO
ALTER TABLE [dbo].[configuration_value] ADD user_idp_id NVARCHAR (50) NOT NULL
    GO
ALTER TABLE [dbo].[configuration_value] DROP CONSTRAINT [configuration_value$key_unique]
    GO
ALTER TABLE [dbo].[configuration_value] DROP COLUMN [key_name]
    GO
ALTER TABLE [dbo].[configuration_mount] ADD is_system BIT NOT NULL DEFAULT 0
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
    CONSTRAINT [PK_component_data_uuid] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [component_data$uuid_unique] UNIQUE NONCLUSTERED
(
[uuid] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [component_data$release_id_unique] UNIQUE NONCLUSTERED
(
[release_uuid] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[component_data] ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
ALTER TABLE [dbo].[configuration_mount] DROP CONSTRAINT [configuration_mount$org_project_component_version_env_key_unique]
    GO
ALTER TABLE [dbo].[configuration_mount] DROP COLUMN [organization_id]
    GO
ALTER TABLE [dbo].[configuration_mount] DROP COLUMN [project_id]
    GO
ALTER TABLE [dbo].[configuration_mount] DROP COLUMN [component_id]
    GO
ALTER TABLE [dbo].[configuration_mount] DROP COLUMN [environment_id]
    GO
ALTER TABLE [dbo].[configuration_mount] DROP COLUMN [component_version]
    GO
ALTER TABLE [dbo].[configuration_mount] ADD component_data_uuid NVARCHAR (50) NOT NULL
    GO
ALTER TABLE [dbo].[configuration_mount]  WITH CHECK ADD  CONSTRAINT [configuration_mount$component_data_uuid_fk] FOREIGN KEY([component_data_uuid])
    REFERENCES [dbo].[component_data] ([uuid])
    GO
ALTER TABLE [dbo].[configuration_mount] CHECK CONSTRAINT [configuration_mount$component_data_uuid_fk]
    GO
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
ALTER TABLE [dbo].[configuration_mount]  WITH CHECK ADD  CONSTRAINT [configuration_mount$component_data_uuid_key_unique] UNIQUE NONCLUSTERED
    (
    [component_data_uuid] ASC,
    [config_key_name] ASC
    )WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
ALTER TABLE [dbo].[configuration_mount] CHECK CONSTRAINT [configuration_mount$component_data_uuid_key_unique]
    GO
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO

CREATE TABLE [dbo].[permission]
(
    [id] [int] IDENTITY(1,1) NOT NULL ,
    [handle][varchar](255) NOT NULL,
    [display_name][varchar](255) NOT NULL,
    [domain_area][varchar](50) NOT NULL CHECK (domain_area IN('APIM-ADMIN','APIM-PUBLISHER','APIM-SUBSCRIBER','BC','AI','BILLINNG')),
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

/****** Add default Permission list ******/
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('api manage','apim:api_manage','APIM-PUBLISHER','manage api');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('tier manage','apim:tier_manage','APIM-PUBLISHER','manage api tier');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('subscription manage','apim:subscription_manage','APIM-PUBLISHER','api subscription manage');

INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('api subscribe','apim:subscribe', 'APIM-SUBSCRIBER','subscribe apis');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('app manage','apim:app_manage', 'APIM-SUBSCRIBER','manage applications');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('sub manage','apim:sub_manage', 'APIM-SUBSCRIBER','manage api subscriptions');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('app import_export','apim:app_import_export', 'APIM-SUBSCRIBER','app import export');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('api key','apim:api_key', 'APIM-SUBSCRIBER','api key gen');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('prod view','environments:view_prod','APIM-SUBSCRIBER','prod environment view');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('dev view','environments:view_dev','APIM-SUBSCRIBER','dev environment view');

INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('apim:admin','apim:admin','APIM-ADMIN','apim admin');

INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('billing tier view','billing:tier_view','BILLINNG','view billing tier');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('billing org create','billing:org_create','BILLINNG','create billing org');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('billing org view','billing:org_view','BILLINNG','view billing org');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('billing invoice view','billing:invoice_view','BILLINNG','view billing invoice');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('billing subscription create','billing:subscription_create','BILLINNG','create billiling subscription');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('billing subscription view','billing:subscription_view','BILLINNG','view billing subscription');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('billing payment method create','billing:payment_method_create','BILLINNG','create billing payment method');
INSERT INTO permission (display_name,handle,domain_area,description) VALUES ('billing payment method view','billing:payment_method_view','BILLINNG','view billing payment method');

ALTER TABLE [dbo].[configuration_mount] DROP CONSTRAINT [configuration_mount$component_data_uuid_key_unique]
    GO
ALTER TABLE [dbo].[configuration_mount]  WITH CHECK ADD  CONSTRAINT [configuration_mount$component_data_uuid_key_unique] UNIQUE NONCLUSTERED ON DELETE CASCADE
    (
    [component_data_uuid] ASC,
    [config_key_name] ASC
    )WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
ALTER TABLE [dbo].[configuration_mount] CHECK CONSTRAINT [configuration_mount$component_data_uuid_key_unique]
    GO
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
ALTER TABLE [dbo].[configuration_mount] ADD is_required BIT NOT NULL DEFAULT 0
    GO
