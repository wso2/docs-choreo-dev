IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_cicd_admin_db_user')
BEGIN
    CREATE USER [choreo_cicd_admin_db_user] with password = N'${choreo-cicd-db-mssql-password}'
    GRANT ALTER, SELECT, INSERT, UPDATE, DELETE, EXECUTE, REFERENCES ON DATABASE::choreo_cicd_db TO choreo_cicd_admin_db_user
END;
GO
