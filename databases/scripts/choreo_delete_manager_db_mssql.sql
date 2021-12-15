-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_delete_manager_db_user')
BEGIN
    CREATE USER [choreo_delete_manager_db_user] FOR LOGIN [choreo_delete_manager_db_user]
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_delete_manager_db TO choreo_delete_manager_db_user
END;
GO
