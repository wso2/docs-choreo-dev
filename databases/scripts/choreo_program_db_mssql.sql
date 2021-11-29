-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_program_db_user')
BEGIN
    CREATE USER [choreo_program_db_user] FOR LOGIN [choreo_program_db_user]
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_program_db TO choreo_program_db_user
END;
GO

/* DB Objects that are related to Program DB to be used by negotiator */
CREATE TABLE [dbo].[program](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [obs_id] [varchar](255) NOT NULL,
    [project_secret] [varchar](255) NOT NULL,
    [latest_version_id] [int] NULL,
    [app_id] [varchar](255) NULL,
    [inserted_at] [datetime] NULL,
    [is_shared] [smallint] NULL,
    CONSTRAINT [PK_program_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [program$uc_obs_id] UNIQUE NONCLUSTERED
(
[obs_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [program$uc_obs_id_latest_version_id] UNIQUE NONCLUSTERED
(
    [obs_id] ASC,
[latest_version_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [program$uc_project_secret] UNIQUE NONCLUSTERED
(
[project_secret] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
ALTER TABLE dbo.program ADD release_id varchar(255) DEFAULT NULL NULL
    GO
/****** Object:  Table [dbo].[version]    Script Date: 9/10/2021 7:36:09 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[version](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [version] [varchar](255) NOT NULL,
    [program_id] [int] NULL,
    [ast_hash] [varchar](255) NOT NULL,
    [ast] [varchar](max) NULL,
    [inserted_at] [datetime] NULL,
    [last_active] [datetime] NULL,
    CONSTRAINT [PK_version_id] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
    CONSTRAINT [version$uc_version_pid] UNIQUE NONCLUSTERED
(
    [program_id] ASC,
[version] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
    GO
/****** Object:  Index [fk_last_version_id]    Script Date: 9/10/2021 7:36:09 AM ******/
CREATE NONCLUSTERED INDEX [fk_last_version_id] ON [dbo].[program]
(
	[latest_version_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [obsid_index]    Script Date: 9/10/2021 7:36:09 AM ******/
CREATE NONCLUSTERED INDEX [obsid_index] ON [dbo].[program]
(
	[obs_id] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [program$uc_app_id]    Script Date: 9/10/2021 7:36:09 AM ******/
CREATE UNIQUE NONCLUSTERED INDEX [program$uc_app_id] ON [dbo].[program]
(
	[app_id] ASC
)
WHERE ([app_id] IS NOT NULL)
WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [program$uc_release_id] ******/
CREATE UNIQUE NONCLUSTERED INDEX [program$uc_release_id] ON [dbo].[program]
(
	[release_id] ASC
)
WHERE ([release_id] IS NOT NULL)
WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [version_index]    Script Date: 9/10/2021 7:36:09 AM ******/
CREATE NONCLUSTERED INDEX [version_index] ON [dbo].[version]
(
	[program_id] ASC,
	[version] ASC
)WITH (STATISTICS_NORECOMPUTE = OFF, DROP_EXISTING = OFF, ONLINE = OFF, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[program] ADD  DEFAULT (NULL) FOR [latest_version_id]
    GO
ALTER TABLE [dbo].[program] ADD  DEFAULT (NULL) FOR [app_id]
    GO
ALTER TABLE [dbo].[program] ADD  DEFAULT (getdate()) FOR [inserted_at]
    GO
ALTER TABLE [dbo].[program] ADD  DEFAULT ((0)) FOR [is_shared]
    GO
ALTER TABLE [dbo].[version] ADD  DEFAULT (NULL) FOR [program_id]
    GO
ALTER TABLE [dbo].[version] ADD  DEFAULT (getdate()) FOR [inserted_at]
    GO
ALTER TABLE [dbo].[version] ADD  DEFAULT (getdate()) FOR [last_active]
    GO
ALTER TABLE [dbo].[program]  WITH CHECK ADD  CONSTRAINT [program$fk_last_version_id] FOREIGN KEY([latest_version_id])
    REFERENCES [dbo].[version] ([id])
    GO
ALTER TABLE [dbo].[program] CHECK CONSTRAINT [program$fk_last_version_id]
    GO
/****** Object:  StoredProcedure [dbo].[DeleteVersion]    Script Date: 9/10/2021 7:36:09 AM ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE PROCEDURE [dbo].[DeleteVersion]
   @versionId int,
   @obsId nvarchar(255)
AS
BEGIN

      SET  XACT_ABORT  ON

      SET  NOCOUNT  OFF

DELETE
FROM dbo.version
WHERE version.id = @versionId AND version.id NOT IN
                                  (
                                      SELECT program.latest_version_id
                                      FROM dbo.program
                                      WHERE program.obs_id = @obsId
                                  )

END
GO
/****** Object:  StoredProcedure [dbo].[GetObsId]    Script Date: 9/10/2021 7:36:09 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/*
*   SSMA informational messages:
*   M2SS0003: The following SQL clause was ignored during conversion:
*   DEFINER = `shairam`@`%`.
*/

CREATE PROCEDURE [dbo].[GetObsId]
   @projsec nvarchar(255),
   @appid nvarchar(255),
   @pid int  OUTPUT,
   @obsid nvarchar(255)  OUTPUT
AS
BEGIN

      SET  XACT_ABORT  ON

      SET  NOCOUNT  ON

      SET @obsid = NULL

      SET @pid = NULL

      IF (@appid = '')
         SET @appid = NULL

      INSERT dbo.program(obs_id, project_secret, app_id)
SELECT newid(), @projsec, @appid
    WHERE NOT EXISTS
            (
               SELECT TOP (1) program.id
               FROM dbo.program
               WHERE program.project_secret = @projsec
            )

/*
*   SSMA warning messages:
*   M2SS0240: The behaviour of Standard Function SCOPE_IDENTITY may not be same as in MySQL
*/

SELECT @pid = scope_identity()

SELECT @pid = program.id, @obsid = program.obs_id
FROM dbo.program
WHERE program.project_secret = @projsec

END
GO
/****** Object:  StoredProcedure [dbo].[GetVersion]    Script Date: 9/10/2021 7:36:09 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/*
*   SSMA informational messages:
*   M2SS0003: The following SQL clause was ignored during conversion:
*   DEFINER = `shairam`@`%`.
*/

CREATE PROCEDURE [dbo].[GetVersion]
   @programid int,
   @asthash nvarchar(255),
   @vid int  OUTPUT,
   @vn nvarchar(255)  OUTPUT,
   @rowcount int  OUTPUT
AS
BEGIN

      SET  XACT_ABORT  ON

      SET  NOCOUNT  ON

      SET @rowcount = NULL

      SET @vn = NULL

      SET @vid = NULL

      INSERT dbo.version(version, program_id, ast_hash)
SELECT newid(), @programid, @asthash
    WHERE NOT EXISTS
            (
               SELECT version.id
               FROM dbo.version
               WHERE version.program_id = @programid AND version.ast_hash = @asthash
            )

SELECT @rowcount = @@rowcount

/*
*   SSMA warning messages:
*   M2SS0240: The behaviour of Standard Function SCOPE_IDENTITY may not be same as in MySQL
*/

SELECT @vid = scope_identity()

SELECT @vid = @vid, @vn = version.version
FROM dbo.version
WHERE version.program_id = @programid AND version.ast_hash = @asthash

END
GO
/****** Object:  StoredProcedure [dbo].[Register]    Script Date: 9/10/2021 7:36:09 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[Register]
   @projsec nvarchar(255),
   @asthash nvarchar(255),
   @appid nvarchar(255),
   @obsid nvarchar(255)  OUTPUT,
   @vn nvarchar(255)  OUTPUT,
   @astchanged bit  OUTPUT

AS
BEGIN
      SET  XACT_ABORT  ON
      SET  NOCOUNT  ON
      SET @astchanged = NULL
      SET @vn = NULL
      SET @obsid = NULL

BEGIN TRANSACTION
	  DECLARE @programid INT;
	  DECLARE @versionid INT;
	  DECLARE @versionrows INT;

	  SET @programid = 0;
	  SET @versionid = 0
	  SET @versionrows = 0
	  SET @astchanged = 0;

EXECUTE dbo.GetObsId @projsec, @appid, @programid OUTPUT, @obsid OUTPUT
	  EXECUTE dbo.GetVersion @programid, @asthash, @versionid OUTPUT, @vn OUTPUT, @versionrows OUTPUT

	  IF (@versionrows = 1)
BEGIN
UPDATE dbo.program
SET latest_version_id = @versionid WHERE program.id = @programid
    SET @astchanged = 0x1
END

      WHILE @@TRANCOUNT > 0
         COMMIT
END
GO
/****** Object:  StoredProcedure [dbo].[GetObsIdByReleaseId] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[GetObsIdByReleaseId]
   @projsec nvarchar(255),
   @releaseid nvarchar(255),
   @pid int  OUTPUT,
   @obsid nvarchar(255)  OUTPUT
AS
BEGIN

      SET  XACT_ABORT  ON

      SET  NOCOUNT  ON

      SET @obsid = NULL

      SET @pid = NULL

      IF (@releaseid = '')
         SET @releaseid = NULL

      INSERT dbo.program(obs_id, project_secret, release_id)
SELECT newid(), @projsec, @releaseid
    WHERE NOT EXISTS
            (
               SELECT TOP (1) program.id
               FROM dbo.program
               WHERE program.project_secret = @projsec
            )

SELECT @pid = scope_identity()

SELECT @pid = program.id, @obsid = program.obs_id
FROM dbo.program
WHERE program.project_secret = @projsec

END
GO
/****** Object:  StoredProcedure [dbo].[RegisterV2]    Script Date: 9/10/2021 7:36:09 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[RegisterV2]
   @projsec nvarchar(255),
   @asthash nvarchar(255),
   @releaseid nvarchar(255),
   @obsid nvarchar(255)  OUTPUT,
   @vn nvarchar(255)  OUTPUT,
   @astchanged bit  OUTPUT

AS
BEGIN
      SET  XACT_ABORT  ON
      SET  NOCOUNT  ON
      SET @astchanged = NULL
      SET @vn = NULL
      SET @obsid = NULL

BEGIN TRANSACTION
	  DECLARE @programid INT;
	  DECLARE @versionid INT;
	  DECLARE @versionrows INT;

	  SET @programid = 0;
	  SET @versionid = 0
	  SET @versionrows = 0
	  SET @astchanged = 0;

EXECUTE dbo.GetObsIdByReleaseId @projsec, @releaseid, @programid OUTPUT, @obsid OUTPUT
	  EXECUTE dbo.GetVersion @programid, @asthash, @versionid OUTPUT, @vn OUTPUT, @versionrows OUTPUT

	  IF (@versionrows = 1)
BEGIN
UPDATE dbo.program
SET latest_version_id = @versionid WHERE program.id = @programid
    SET @astchanged = 0x1
END

      WHILE @@TRANCOUNT > 0
         COMMIT
END
GO

/****** Object:  StoredProcedure [dbo].[GetObsIdByProjectSecret]     Script Date: 11/11/2021 4:36:09 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[GetObsIdByProjectSecret]
   @projsec nvarchar(255),
   @pid int  OUTPUT,
   @obsid nvarchar(255)  OUTPUT,
   @releaseid nvarchar(255)  OUTPUT
AS
BEGIN

      SET  XACT_ABORT  ON

      SET  NOCOUNT  ON

      SET @obsid = NULL

      SET @pid = NULL

      SET @releaseid = NULL

      INSERT dbo.program(obs_id, project_secret)
SELECT newid(), @projsec
      WHERE NOT EXISTS
            (
               SELECT TOP (1) program.id
               FROM dbo.program
               WHERE program.project_secret = @projsec
            )

SELECT @pid = scope_identity()

SELECT @pid = program.id, @obsid = program.obs_id, @releaseid = program.release_id
FROM dbo.program
WHERE program.project_secret = @projsec

END
GO

/****** Object:  StoredProcedure [dbo].[Handshake]    Script Date: 11/11/2021 4:36:09 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[Handshake]
   @projsec nvarchar(255),
   @asthash nvarchar(255),
   @obsid nvarchar(255)  OUTPUT,
   @vn nvarchar(255)  OUTPUT,
   @astchanged bit  OUTPUT,
   @releaseid nvarchar(255)  OUTPUT
AS
BEGIN
      SET  XACT_ABORT  ON
      SET  NOCOUNT  ON
      SET @astchanged = NULL
      SET @vn = NULL
      SET @obsid = NULL
      SET @releaseid = NULL

BEGIN TRANSACTION
	  DECLARE @programid INT;
	  DECLARE @versionid INT;
	  DECLARE @versionrows INT;

	  SET @programid = 0;
	  SET @versionid = 0
	  SET @versionrows = 0
	  SET @astchanged = 0;

EXECUTE dbo.GetObsIdByProjectSecret @projsec, @programid OUTPUT, @obsid OUTPUT, @releaseid OUTPUT
	  EXECUTE dbo.GetVersion @programid, @asthash, @versionid OUTPUT, @vn OUTPUT, @versionrows OUTPUT

	  IF (@versionrows = 1)
BEGIN
UPDATE dbo.program
SET latest_version_id = @versionid WHERE program.id = @programid
    SET @astchanged = 0x1
END

      WHILE @@TRANCOUNT > 0
         COMMIT
END
GO
