-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_cp_ris_db_user')
BEGIN
    CREATE USER [choreo_cp_ris_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_cp_ris_db TO choreo_cp_ris_db_user
END;
GO

-- create invocation_details table

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[invocation_details]') AND TYPE IN (N'U'))
CREATE TABLE invocation_details (
    id int IDENTITY(1,1) NOT NULL,
    uuid varchar(50) NOT NULL,
    target varchar(max) NOT NULL,
    status varchar(50) NOT NULL,
    callback_config varchar(255) NOT NULL,
    retry_config varchar(255) NULL,
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT invocation_details$uuid_unique UNIQUE (uuid)
);

-- create executions table

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[executions]') AND TYPE IN (N'U'))
CREATE TABLE executions(
	id int IDENTITY(1,1) NOT NULL,
    invocation_uuid varchar(50) NOT NULL,
    last_response varchar(max) NOT NULL,
    attempts int NOT NULL,
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT executions$config_uuid_fk FOREIGN KEY (invocation_uuid) REFERENCES dbo.invocation_details(uuid)
);

/****** Object:  Trigger [dbo].[executions_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[executions_UpdateTimeTrigger] ON [dbo].[executions]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [executions] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[executions] ENABLE TRIGGER [executions_UpdateTimeTrigger]
    GO

/****** Object:  Trigger [dbo].[invocation_details_UpdateTimeTrigger] ******/
SET ANSI_NULLS ON
    GO
SET QUOTED_IDENTIFIER ON
    GO

CREATE TRIGGER [dbo].[invocation_details_UpdateTimeTrigger] ON [dbo].[invocation_details]
    FOR INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;
    UPDATE tble
    SET updated_at = GETDATE()
    FROM [invocation_details] AS tble
    INNER JOIN inserted AS i
    ON tble.id = i.id;
END
GO
ALTER TABLE [dbo].[invocation_details] ENABLE TRIGGER [invocation_details_UpdateTimeTrigger]
    GO

-- index creation

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'invocation_details_ind_by_uuid')
DROP INDEX invocation_details.invocation_details_ind_by_uuid
CREATE INDEX invocation_details_ind_by_uuid ON invocation_details(uuid);

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'invocation_details_ind_by_status')
DROP INDEX invocation_details.invocation_details_ind_by_status
CREATE INDEX invocation_details_ind_by_status ON invocation_details(status);

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'executions_ind_by_invocation_uuid')
DROP INDEX executions.executions_ind_by_invocation_uuid
CREATE INDEX executions_ind_by_invocation_uuid ON executions(invocation_uuid);
