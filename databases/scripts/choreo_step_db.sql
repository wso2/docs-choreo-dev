CREATE DATABASE IF NOT EXISTS choreo_step_db;
USE choreo_step_db;

CREATE TABLE IF NOT EXISTS job_status (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  from_timestamp datetime2(6) NOT NULL,
  to_timestamp datetime2(6) NOT NULL,
  worker_id VARCHAR(10) NOT NULL,
  job_start datetime2(6) NOT NULL,
  job_end datetime2(6) NOT NULL,
  job_type VARCHAR(10) NOT NULL,
  successful BOOLEAN NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS api_proxy_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  job_id UNIQUEIDENTIFIER NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  api_id VARCHAR(100) NOT NULL,
  deployment_id VARCHAR(255) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (job_id) REFERENCES job_status (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS app_request_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  job_id UNIQUEIDENTIFIER NOT NULL,
  obs_id VARCHAR(100) NOT NULL,
  obs_version VARCHAR(100) NOT NULL,
  request_type VARCHAR(15) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (job_id) REFERENCES job_status (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS monthly_api_proxy_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  last_job_id UNIQUEIDENTIFIER NOT NULL,
  month_start datetime2(0) NOT NULL,
  to_timestamp datetime2(6) NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  api_id VARCHAR(100) NOT NULL,
  deployment_id VARCHAR(255) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (last_job_id) REFERENCES job_status (id)
);

CREATE TABLE IF NOT EXISTS monthly_app_request_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  last_job_id UNIQUEIDENTIFIER NOT NULL,
  month_start datetime2(0) NOT NULL,
  to_timestamp datetime2(6) NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  app_id VARCHAR(100) NOT NULL,
  obs_id VARCHAR(100) NOT NULL,
  obs_version VARCHAR(100) NOT NULL,
  request_type VARCHAR(15) NOT NULL,
  app_type VARCHAR(100) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (last_job_id) REFERENCES job_status (id)
);

CREATE TABLE IF NOT EXISTS monthly_total_step_count (
  id UNIQUEIDENTIFIER DEFAULT NEWSEQUENTIALID(),
  last_job_id UNIQUEIDENTIFIER NOT NULL,
  month_start datetime2(0) NOT NULL,
  month_end datetime2(0) NOT NULL,
  to_timestamp datetime2(6) NOT NULL,
  org_uuid VARCHAR(100) NOT NULL,
  count INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (last_job_id) REFERENCES job_status (id)
);



