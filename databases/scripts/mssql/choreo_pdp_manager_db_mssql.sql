IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_pdp_manager_db_user')
BEGIN
    CREATE USER [choreo_pdp_manager_db_user] with password = N'${choreo-pdp-manager-db-mssql-password}'
    GRANT ALTER, SELECT, INSERT, UPDATE, DELETE, EXECUTE, REFERENCES ON DATABASE::choreo_pdp_manager_db TO choreo_pdp_manager_db_user
END;
GO
