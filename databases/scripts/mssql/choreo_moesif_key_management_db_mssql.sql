IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_moesif_key_management_db_user')
BEGIN
    CREATE USER [choreo_moesif_key_management_db_user] with password = N'${choreo_moesif_key_management_db_mssql_password}'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_moesif_key_management_db TO choreo_moesif_key_management_db_user
END;
GO

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[orgID_moesifKey]') AND TYPE IN (N'U'))
CREATE TABLE orgID_moesifKey
(
    uuid VARCHAR (1000) NOT NULL,
    organization_id VARCHAR (128)  NOT NULL,
    moesif_key VARCHAR (1000) NOT NULL,
    env VARCHAR (50) NOT NULL DEFAULT 'ALL',
    PRIMARY KEY (uuid)
);
