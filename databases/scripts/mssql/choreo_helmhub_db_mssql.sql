IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_helmhub_db_user')
BEGIN
    CREATE USER [choreo_helmhub_db_user] with password = N'${choreo-helmhub-db-mssql-password}'
    GRANT ALTER, SELECT, INSERT, UPDATE, DELETE, EXECUTE, REFERENCES ON DATABASE::choreo_helmhub_db TO choreo_helmhub_db_user;
END;
GO
