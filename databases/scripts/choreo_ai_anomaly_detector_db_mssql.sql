-- Create User
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'choreo_ai_anomaly_detector_db') AND NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_ai_anomaly_detector_db_user')
BEGIN
    CREATE USER [choreo_ai_anomaly_detector_db_user] with password = N'xxxxxxxxxxxxx'
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_ai_anomaly_detector_db TO choreo_ai_anomaly_detector_db_user
END;
GO

-- Create anomaly detector code_version table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[CODE_VERSION]') AND TYPE IN (N'U'))
CREATE TABLE CODE_VERSION (
             CODE_VERSION_ID INTEGER IDENTITY(1,1) NOT NULL,
             ORG_ID VARCHAR (255)  NOT NULL,
             OBS_ID VARCHAR (255)  NOT NULL,
             OBS_VERSION VARCHAR (255) NOT NULL,
             RELEASE_ID VARCHAR (255) NOT NULL,
             PRIMARY KEY (CODE_VERSION_ID)
);

-- Create anomaly detector alert table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[RESOURCE]') AND TYPE IN (N'U'))
CREATE TABLE RESOURCE (
             RESOURCE_ID INTEGER IDENTITY(1,1) NOT NULL,
             CODE_VERSION_ID INTEGER,
             SERVICE_NAME VARCHAR (255) NOT NULL,
             FUNCTION_NAME VARCHAR (255) NOT NULL,
             RESOURCE_ACCESSOR VARCHAR (255) NOT NULL,
             PRIMARY KEY (RESOURCE_ID)
);

-- Create anomaly detector alert table
IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[ALERT]') AND TYPE IN (N'U'))
CREATE TABLE ALERT (
             ALERT_ID INTEGER IDENTITY(1,1) NOT NULL,
             RESOURCE_ID INTEGER,
             ANOMALY_TIMESTAMP VARCHAR (255) NOT NULL,
             ALERT_TYPE VARCHAR (255) NOT NULL,
             PROPERTIES VARCHAR (2000) NOT NULL,
             PRIMARY KEY (ALERT_ID)
);


