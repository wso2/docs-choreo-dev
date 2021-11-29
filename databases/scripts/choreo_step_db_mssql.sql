-- Create User
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'choreo_step_db_user')
BEGIN
    CREATE USER [choreo_step_db_user] FOR LOGIN [choreo_step_db_user]
    GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON DATABASE::choreo_step_db TO choreo_step_db_user
END;
GO

-- create job_status table

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[job_status]') AND TYPE IN (N'U'))
CREATE TABLE job_status (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  from_timestamp DATETIME2(3) NOT NULL,
  to_timestamp DATETIME2(3) NOT NULL,
  worker_id VARCHAR(64) NOT NULL,
  job_start DATETIME2(3) NOT NULL,  -- TODO: Must check if millisecond precision is enough
  job_end DATETIME2(3),
  job_type VARCHAR(64) NOT NULL,
  successful BIT NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

-- create periodic step count tables

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[api_proxy_step_count]') AND TYPE IN (N'U'))
CREATE TABLE api_proxy_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  job_id UNIQUEIDENTIFIER NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  api_id VARCHAR(100) NOT NULL,
  deployment_id VARCHAR(255) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (job_id) REFERENCES job_status (id) ON DELETE CASCADE
);

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[app_request_step_count]') AND TYPE IN (N'U'))
CREATE TABLE app_request_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  job_id UNIQUEIDENTIFIER NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  app_id VARCHAR(100) NOT NULL,
  obs_id VARCHAR(100) NOT NULL,
  obs_version VARCHAR(100) NOT NULL,
  request_type VARCHAR(15) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (job_id) REFERENCES job_status (id) ON DELETE CASCADE
);

-- create daily step count tables

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[daily_api_proxy_step_count]') AND TYPE IN (N'U'))
CREATE TABLE daily_api_proxy_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  last_job_id UNIQUEIDENTIFIER NOT NULL,
  day_start DATETIME2(0) NOT NULL,
  to_timestamp DATETIME2(3) NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  api_id VARCHAR(100) NOT NULL,
  deployment_id VARCHAR(255) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (last_job_id) REFERENCES job_status (id)
);

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[daily_app_request_step_count]') AND TYPE IN (N'U'))
CREATE TABLE daily_app_request_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  last_job_id UNIQUEIDENTIFIER NOT NULL,
  day_start DATETIME2(0) NOT NULL,
  to_timestamp DATETIME2(3) NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  app_id VARCHAR(100) NOT NULL,
  obs_id VARCHAR(100) NOT NULL,
  obs_version VARCHAR(100) NOT NULL,
  request_type VARCHAR(15) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (last_job_id) REFERENCES job_status (id)
);

-- create total daily step count table

IF NOT  EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[daily_total_step_count]') AND TYPE IN (N'U'))
CREATE TABLE daily_total_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  last_job_id UNIQUEIDENTIFIER NOT NULL,
  day_start DATETIME2(0) NOT NULL,
  to_timestamp DATETIME2(3) NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (last_job_id) REFERENCES job_status (id)
);

IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[threshold_event_status]') AND TYPE IN (N'U'))
CREATE TABLE threshold_event_status (
	org_uuid varchar(100) NOT NULL,
	billing_month int NOT NULL,
	threshold_1_event_sent bit DEFAULT 0,
	threshold_2_event_sent bit DEFAULT 0,
	billing_cycle_reset bit DEFAULT 0,
	PRIMARY KEY (org_uuid, billing_month)
);

-- --------------------------- INDEX CREATION -----------------------------

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'daily_api_proxy_ind_by_day_start')
DROP INDEX daily_api_proxy_step_count.daily_api_proxy_ind_by_day_start
CREATE INDEX daily_api_proxy_ind_by_day_start ON daily_api_proxy_step_count(day_start);

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'daily_app_request_ind_by_day_start')
DROP INDEX daily_app_request_step_count.daily_app_request_ind_by_day_start
CREATE INDEX daily_app_request_ind_by_day_start ON daily_app_request_step_count(day_start);

IF EXISTS (SELECT NAME FROM SYSINDEXES WHERE NAME = 'daily_total_ind_by_day_start')
DROP INDEX daily_total_step_count.daily_total_ind_by_day_start
CREATE INDEX daily_total_ind_by_day_start ON daily_total_step_count(day_start);

