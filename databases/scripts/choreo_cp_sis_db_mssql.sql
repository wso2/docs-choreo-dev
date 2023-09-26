-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_cp_sis_db_user')
BEGIN
    CREATE USER [choreo_cp_sis_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_cp_sis_db TO choreo_cp_sis_db_user
END;
GO

-- create operation table

IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[operation]') AND TYPE IN (N'U'))
CREATE TABLE operation (
    id int IDENTITY(1,1) NOT NULL,
    uuid varchar(50) NOT NULL,
    type varchar(50) NOT NULL,
    status varchar(50) NOT NULL,
    percentage int,
    message varchar(255),
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT operation$uuid_unique UNIQUE (uuid)
);

-- index creation

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'operation_ind_by_uuid')
DROP INDEX operation.operation_ind_by_uuid
CREATE INDEX operation_ind_by_uuid ON operation(uuid);

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'operation_ind_by_type')
DROP INDEX operation.operation_ind_by_type
CREATE INDEX operation_ind_by_type ON operation(type);
