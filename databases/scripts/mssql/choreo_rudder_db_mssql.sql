IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_rudder_admin_db_user')
BEGIN
    CREATE USER [choreo_rudder_admin_db_user] with password = N'${choreo_rudder_db_mssql_password}'
    GRANT ALTER, SELECT, INSERT, UPDATE, DELETE, EXECUTE, REFERENCES ON DATABASE::choreo_rudder_db TO choreo_rudder_admin_db_user
END;
GO
