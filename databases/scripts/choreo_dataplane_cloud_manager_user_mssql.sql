IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_cloud_manager_admin_db_user')
BEGIN
    CREATE USER [choreo_cloud_manager_admin_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT ALTER, SELECT, INSERT, UPDATE, DELETE, EXECUTE, REFERENCES ON DATABASE::choreo_cloud_manager_db TO choreo_cloud_manager_admin_db_user
END;
GO
