IF NOT EXISTS(SELECT * FROM sys.databases WHERE name = 'choreo_codeserver_db')
CREATE DATABASE [choreo_codeserver_db]
GO

USE choreo_codeserver_db;
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[cluster]') AND TYPE IN (N'U'))
CREATE TABLE [cluster](
    [cluster_id] VARCHAR(40) NOT NULL,
    [region] VARCHAR(40) NOT NULL,
    [hostname] VARCHAR(253) NOT NULL UNIQUE,
    [codeserver_count] INT NOT NULL,
    [last_clean_timestamp] DATETIME DEFAULT GETUTCDATE(),
    CONSTRAINT PK_cluster PRIMARY KEY ([cluster_id])
)
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[codeserver]') AND TYPE IN (N'U'))
CREATE TABLE [codeserver]( 
    [id] INT NOT NULL IDENTITY(1,1),
    [user_idp_id] NVARCHAR(50) NOT NULL,
    [organization_uuid]  NVARCHAR(50) NOT NULL,
    [component_uuid] NVARCHAR(50) NOT NULL,
    [project_uuid] NVARCHAR(50) NOT NULL,
    [cluster_id] VARCHAR(40) NOT NULL,
    CONSTRAINT PK_codeserver PRIMARY KEY ([id]),
    CONSTRAINT UC_codeserver UNIQUE ([user_idp_id], [organization_uuid], [component_uuid], [project_uuid]),
    CONSTRAINT FK_cluster_codeserver FOREIGN KEY ([cluster_id]) REFERENCES [cluster]([cluster_id])
    ON DELETE CASCADE ON UPDATE CASCADE
)
GO

CREATE TRIGGER [TRG_deduct_codeserver_total] 
ON [codeserver]
AFTER DELETE AS
BEGIN 
    SET NOCOUNT ON;
    UPDATE [cluster]
    SET [codeserver_count] = [codeserver_count] - 1
    WHERE [cluster_id] IN (SELECT [cluster_id] FROM deleted) AND [codeserver_count] >= 1
END
GO
