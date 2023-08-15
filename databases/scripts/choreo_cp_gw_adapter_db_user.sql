IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'gateway_adapter_db_user')
BEGIN
    CREATE USER [gateway_adapter_db_user] FOR LOGIN [gateway_adapter_db_user]
    GRANT ALTER, SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::gateway_adapter_db TO gateway_adapter_db_user
END;
GO
