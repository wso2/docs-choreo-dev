-- Create User
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'choreo_ai_anomaly_detector_db') AND NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_ai_anomaly_detector_db_user')
BEGIN
    CREATE USER [choreo_ai_anomaly_detector_db_user] FOR LOGIN [choreo_ai_anomaly_detector_db_user]
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_ai_anomaly_detector_db TO choreo_ai_anomaly_detector_db_user
END;
GO

-- Create anomaly detector code_version table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[code_version]') AND TYPE IN (N'U'))
CREATE TABLE code_version (
             code_version_id INTEGER IDENTITY(1,1) NOT NULL,
             org_id VARCHAR (255)  NOT NULL,
             obs_id VARCHAR (255)  NOT NULL,
             obs_version VARCHAR (255) NOT NULL,
             release_id VARCHAR (255) NOT NULL,
             PRIMARY KEY (code_version_id)
);

-- Create anomaly detector alert table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[resource]') AND TYPE IN (N'U'))
CREATE TABLE alert (
             resource_id INTEGER IDENTITY(1,1) NOT NULL,
             code_version_id INTEGER,
             service_name VARCHAR (255) NOT NULL,
             function_name VARCHAR (255) NOT NULL,
             resource_accessor VARCHAR (255) NOT NULL,
             PRIMARY KEY (resource_id)
);

-- Create anomaly detector alert table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[alert]') AND TYPE IN (N'U'))
CREATE TABLE alert (
             alert_id INTEGER IDENTITY(1,1) NOT NULL,
             resource_id INTEGER,
             anomaly_timestamp DATETIME NOT NULL,
             alert_type VARCHAR (255) NOT NULL,
             properties JSON NOT NULL,
             PRIMARY KEY (code_version_id)
);


