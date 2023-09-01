-- Create User
IF EXISTS (SELECT name FROM master.sys.databases WHERE name = N'choreo_testbase_db') AND NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_testbase_db_user')
BEGIN
    CREATE USER [choreo_testbase_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_testbase_db TO choreo_testbase_db_user
END;
GO

CREATE TABLE [dbo].[current_collection_info](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [nvarchar](255) NOT NULL,
	[workspace_id] [nvarchar](255) NOT NULL,
	[application_id] [nvarchar](255) NOT NULL,
	[collection_id] [nvarchar](255) NOT NULL,
	[created_at] [datetime] NOT NULL,
	[updated_at] [datetime] NOT NULL,
 CONSTRAINT [PK_current_collection_info_id] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [current_collection_info$user_app_ws_id_unique] UNIQUE NONCLUSTERED
(
	[user_id] ASC,
	[application_id] ASC,
	[workspace_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[openapi_definitions]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[openapi_definitions](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[org_handle] [nvarchar](255) NOT NULL,
	[app_handle] [nvarchar](255) NOT NULL,
	[content] [nvarchar](max) NOT NULL,
	[svc_file_digest] [nvarchar](255) NOT NULL,
	[created_at] [datetime] NOT NULL,
	[updated_at] [datetime] NOT NULL,
 CONSTRAINT [PK_openapi_definitions_id] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [openapi_definitions$swagger_def_id_unique] UNIQUE NONCLUSTERED
(
	[org_handle] ASC,
	[app_handle] ASC,
	[svc_file_digest] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[postman_settings]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[postman_settings](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [nvarchar](255) NOT NULL,
	[api_key] [nvarchar](255) NOT NULL,
	[default_workspace_id] [nvarchar](255) NULL,
	[cache_workspaces] [smallint] NOT NULL,
	[cache_collections] [smallint] NOT NULL,
	[created_at] [datetime] NOT NULL,
	[updated_at] [datetime] NOT NULL,
 CONSTRAINT [PK_postman_settings_id] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [postman_settings$user_id_unique] UNIQUE NONCLUSTERED
(
	[user_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[postman_workspaces]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[postman_workspaces](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[workspaces] [nvarchar](max) NOT NULL,
	[postman_settings_id] [int] NOT NULL,
	[created_at] [datetime] NOT NULL,
	[updated_at] [datetime] NOT NULL,
 CONSTRAINT [PK_postman_workspaces_id] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [postman_workspaces$postman_settings_id_UNIQUE] UNIQUE NONCLUSTERED
(
	[postman_settings_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[testcases]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[testcases](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [nvarchar](255) NOT NULL,
	[organization_id] [nvarchar](255) NOT NULL,
	[application_id] [nvarchar](255) NOT NULL,
	[test_case_name] [nvarchar](255) NOT NULL,
	[display_name] [nvarchar](255) NOT NULL,
	[working_file] [nvarchar](255) NOT NULL,
	[created_at] [datetime] NOT NULL,
	[updated_at] [datetime] NOT NULL,
 CONSTRAINT [PK_testcases_id] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [testcases$test_case_id_unique] UNIQUE NONCLUSTERED
(
	[user_id] ASC,
	[organization_id] ASC,
	[application_id] ASC,
	[test_case_name] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[current_collection_info] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[current_collection_info] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[openapi_definitions] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[openapi_definitions] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[postman_settings] ADD  DEFAULT (NULL) FOR [default_workspace_id]
GO
ALTER TABLE [dbo].[postman_settings] ADD  DEFAULT ((1)) FOR [cache_workspaces]
GO
ALTER TABLE [dbo].[postman_settings] ADD  DEFAULT ((1)) FOR [cache_collections]
GO
ALTER TABLE [dbo].[postman_settings] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[postman_settings] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[postman_workspaces] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[postman_workspaces] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[testcases] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[testcases] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[postman_workspaces]  WITH NOCHECK ADD  CONSTRAINT [postman_workspaces$fk_postmant_settings_id] FOREIGN KEY([postman_settings_id])
REFERENCES [dbo].[postman_settings] ([id])
GO
ALTER TABLE [dbo].[postman_workspaces] CHECK CONSTRAINT [postman_workspaces$fk_postmant_settings_id]
GO
/****** Object:  Trigger [dbo].[current_collection_info_Audit]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

    CREATE TRIGGER [dbo].[current_collection_info_Audit] ON [dbo].[current_collection_info]
    FOR INSERT, UPDATE AS
    BEGIN

        SET NOCOUNT ON;

	        UPDATE tble

	        SET updated_at = GETDATE()

	        FROM current_collection_info AS tble

	        INNER JOIN inserted AS i

		        ON tble.id = i.id;
    END
GO
ALTER TABLE [dbo].[current_collection_info] ENABLE TRIGGER [current_collection_info_Audit]
GO
/****** Object:  Trigger [dbo].[openapi_definitions_Audit]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

    CREATE TRIGGER [dbo].[openapi_definitions_Audit] ON [dbo].[openapi_definitions]
    FOR INSERT, UPDATE AS
    BEGIN

        SET NOCOUNT ON;

	        UPDATE tble

	        SET updated_at = GETDATE()

	        FROM openapi_definitions AS tble

	        INNER JOIN inserted AS i

		        ON tble.id = i.id;
    END
GO
ALTER TABLE [dbo].[openapi_definitions] ENABLE TRIGGER [openapi_definitions_Audit]
GO
/****** Object:  Trigger [dbo].[postman_settings_Audit]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

    CREATE TRIGGER [dbo].[postman_settings_Audit] ON [dbo].[postman_settings]
    FOR INSERT, UPDATE AS
    BEGIN

        SET NOCOUNT ON;

	        UPDATE tble

	        SET updated_at = GETDATE()

	        FROM postman_settings AS tble

	        INNER JOIN inserted AS i

		        ON tble.id = i.id;
    END
GO
ALTER TABLE [dbo].[postman_settings] ENABLE TRIGGER [postman_settings_Audit]
GO
/****** Object:  Trigger [dbo].[postman_workspaces_Audit]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

    CREATE TRIGGER [dbo].[postman_workspaces_Audit] ON [dbo].[postman_workspaces]
    FOR INSERT, UPDATE AS
    BEGIN

        SET NOCOUNT ON;

	        UPDATE tble

	        SET updated_at = GETDATE()

	        FROM postman_workspaces AS tble

	        INNER JOIN inserted AS i

		        ON tble.id = i.id;
    END
GO
ALTER TABLE [dbo].[postman_workspaces] ENABLE TRIGGER [postman_workspaces_Audit]
GO
/****** Object:  Trigger [dbo].[testcases_Audit]    Script Date: 9/20/2021 12:53:33 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

    CREATE TRIGGER [dbo].[testcases_Audit] ON [dbo].[testcases]
    FOR INSERT, UPDATE AS
    BEGIN

        SET NOCOUNT ON;

	        UPDATE tble

	        SET updated_at = GETDATE()

	        FROM testcases AS tble

	        INNER JOIN inserted AS i

		        ON tble.id = i.id;
    END
GO
