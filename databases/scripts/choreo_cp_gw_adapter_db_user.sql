IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_gateway_adapter_db_user')
BEGIN
    CREATE USER [choreo_gateway_adapter_db_user] FOR LOGIN [choreo_gateway_adapter_db_user]
    GRANT ALTER, SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_gateway_adapter_db TO choreo_gateway_adapter_db_user
END;
GO
