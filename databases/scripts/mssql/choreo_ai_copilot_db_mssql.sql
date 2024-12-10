-- Create User
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'choreo_ai_copilot_db') AND NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_ai_copilot_db_user')
BEGIN
    CREATE USER [choreo_ai_copilot_db_user] with password = N'${choreo_ai_copilot_db_mssql_password}'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_ai_copilot_db TO choreo_ai_copilot_db_user
END;
GO

CREATE TABLE COPILOT_DISABLED_ORGS
(
    TIMESTAMP DATETIME NOT NULL,
    ORG_ID VARCHAR(255) NOT NULL,
    PRIMARY KEY (ORG_ID, TIMESTAMP)
);

