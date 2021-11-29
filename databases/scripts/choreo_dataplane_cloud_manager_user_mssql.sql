IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_cloud_manager_admin_db_user')
BEGIN
    CREATE USER [choreo_cloud_manager_admin_db_user] FOR LOGIN [choreo_cloud_manager_admin_db_user]
    EXEC sp_addrolemember N'db_ddladmin', N'choreo_cloud_manager_admin_db_user'
    EXEC sp_addrolemember N'db_datawriter', N'choreo_cloud_manager_admin_db_user'
    EXEC sp_addrolemember N'db_datareader', N'choreo_cloud_manager_admin_db_user'
END;
GO
