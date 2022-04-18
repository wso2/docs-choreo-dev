IF NOT EXISTS(SELECT * FROM sys.databases WHERE name = 'choreo_codeserver_db')
CREATE DATABASE [choreo_codeserver_db]
GO

USE choreo_codeserver_db;
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[clusters]') AND TYPE IN (N'U'))
CREATE TABLE [clusters](
    [cluster_id] VARCHAR(50) NOT NULL,
    [region] VARCHAR(40) NOT NULL,
    [hostname] VARCHAR(50) NOT NULL UNIQUE,
    [total] INT NOT NULL,
    [time_stamp] DATETIME DEFAULT GETUTCDATE(),
    CONSTRAINT PK_clusters PRIMARY KEY( [cluster_id] )
)
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[codeservers]') AND TYPE IN (N'U'))
CREATE TABLE [codeservers]( 
    [id] INT NOT NULL IDENTITY(1,1),
    [userid] NVARCHAR(60) NOT NULL,
    [orgid]  NVARCHAR(60) NOT NULL,
    [compid] NVARCHAR(60) NOT NULL,
    [projid] NVARCHAR(60) NOT NULL,
    [cluster_id] VARCHAR(50) NOT NULL,
    CONSTRAINT PK_codeservers PRIMARY KEY ([id]),
    CONSTRAINT UC_codeservers UNIQUE ([userid],[orgid],[compid],[projid]),
    CONSTRAINT FK_clusters_codeservers FOREIGN KEY ([cluster_id]) REFERENCES [clusters]([cluster_id])
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
GO

CREATE TRIGGER [TRG_deducte_codeserver_total] 
ON [codeservers]
AFTER DELETE
AS BEGIN 
    SET NOCOUNT ON;
    UPDATE [clusters]
    SET [total] = [total] - 1
    WHERE [cluster_id] IN (SELECT [cluster_id] FROM deleted) AND [total] >= 1
END
GO
