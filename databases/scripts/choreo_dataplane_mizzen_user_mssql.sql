IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_mizzen_admin_db_user')
BEGIN
    CREATE USER [choreo_mizzen_admin_db_user] FOR LOGIN [choreo_mizzen_admin_db_user]
    EXEC sp_addrolemember N'db_owner', N'choreo_mizzen_admin_db_user'
END;
GO
