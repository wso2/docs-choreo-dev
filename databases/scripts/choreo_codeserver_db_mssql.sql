IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_codeserver_db_user') 
BEGIN 
    CREATE USER [choreo_codeserver_db_user] FOR LOGIN [choreo_codeserver_db_user] 
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_codeserver_db TO choreo_codeserver_db_user 
END; 
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[cluster]') AND TYPE IN (N'U'))
CREATE TABLE [cluster](
    [cluster_id] VARCHAR(40) NOT NULL,
    [codeserver_count] INT NOT NULL,
    [last_clean_timestamp] DATETIME DEFAULT GETUTCDATE(),
    CONSTRAINT PK_cluster PRIMARY KEY ([cluster_id])
)
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[codeserver]') AND TYPE IN (N'U'))
CREATE TABLE [codeserver]( 
    [id] INT NOT NULL IDENTITY(1,1),
    [user_idp_id] NVARCHAR(50) NOT NULL,
    [organization_id] INT NOT NULL,
    [component_uuid] NVARCHAR(50) NOT NULL,
    [project_uuid] NVARCHAR(50) NOT NULL,
    [cluster_id] VARCHAR(40) NOT NULL,
    CONSTRAINT PK_codeserver PRIMARY KEY ([id]),
    CONSTRAINT UC_codeserver UNIQUE ([user_idp_id], [organization_id], [component_uuid], [project_uuid])
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

CREATE TRIGGER [TRG_increment_codeserver_total]
ON [codeserver]
AFTER INSERT AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @clusterid AS VARCHAR(40) 
    SET @clusterid = (SELECT [cluster_id] FROM inserted)
    IF NOT EXISTS (SELECT [cluster_id] FROM [cluster] WHERE [cluster_id] = @clusterid)
        BEGIN
            INSERT INTO [cluster] ([cluster_id], [codeserver_count])
            VALUES (@clusterid, 1) 
        END
    ELSE 
        BEGIN
            UPDATE [cluster]
            SET [codeserver_count] = [codeserver_count] + 1 
            WHERE [cluster_id] = @clusterid
        END
END
GO

CREATE TRIGGER [TRG_remove_codeservers] 
ON [cluster]
AFTER DELETE AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM [codeserver]
    WHERE [cluster_id] IN (SELECT [cluster_id] FROM deleted)
END
GO
