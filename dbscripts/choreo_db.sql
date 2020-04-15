CREATE TABLE IF NOT EXISTS program_info(
  id                    INT             AUTO_INCREMENT,
  uuid                  VARCHAR(255)    NOT NULL,
  programHash           VARCHAR(255)    NOT NULL,
  programJson           MEDIUMTEXT,
  userId                VARCHAR(255),
  appId                 VARCHAR(255),
  insertedTime          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT UC_uuid UNIQUE (uuid),
  CONSTRAINT UC_appId UNIQUE (appId),
  CONSTRAINT UC_uuid_appid UNIQUE (uuid, appId)
);
CREATE INDEX uuid_index ON program_info (uuid);
CREATE INDEX appid_index ON program_info (appId);
CREATE INDEX uuid_appid_index ON program_info (uuid, appId);

CREATE TABLE IF NOT EXISTS trace_spans (
  spanId                VARCHAR(36)     NOT NULL,
  parentSpanId          VARCHAR(36)     NOT NULL,
  traceId               VARCHAR(36)     NOT NULL,
  uuid                  VARCHAR(72)     NOT NULL,
  startTime             VARCHAR(36)     NOT NULL,
  duration              INT UNSIGNED    DEFAULT 0,
  invocationFQN         VARCHAR(255)    NOT NULL,
  isError               BOOL            DEFAULT FALSE,
  isService             BOOL            DEFAULT FALSE,
  httpStatusCodeGroup   VARCHAR(36),
  connectorName         VARCHAR(36),
  service               VARCHAR(36),
  resource              VARCHAR(36),
  httpUrl               VARCHAR(36),
  httpMethod            VARCHAR(36),
  action                VARCHAR(36),
  PRIMARY KEY (spanId, traceId)
);
CREATE INDEX uuid_index ON trace_spans (uuid);
CREATE INDEX start_time_index ON trace_spans (startTime);



CREATE TABLE IF NOT EXISTS application (
   id               	int(11)         NOT NULL AUTO_INCREMENT,
   handle            	varchar(255)    NOT NULL,
   display_name      	varchar(255)    NOT NULL,
   working_file      	varchar(255)    NOT NULL,
   template          	varchar(255)    NOT NULL,
   git_remote        	varchar(255)    NOT NULL DEFAULT '',
   organization_id     varchar(255)    NOT NULL,
   PRIMARY KEY (id),
   UNIQUE KEY organization_id_handle_unique (organization_id,handle)
);

CREATE TABLE IF NOT EXISTS organization (
   id       	int(11)         NOT NULL AUTO_INCREMENT,
   name      	varchar(255)    NOT NULL,
   handle    	varchar(255)    NOT NULL,
   PRIMARY KEY (id),
   UNIQUE KEY handle_unique (handle)
);

CREATE TABLE IF NOT EXISTS environment (
   id                	int(11)         NOT NULL AUTO_INCREMENT,
   invocation_url    	varchar(300)    DEFAULT NULL,
   observability_url 	varchar(300)    DEFAULT NULL,
   deployment_status 	varchar(255)    DEFAULT NULL,
   test_status       	varchar(255)    DEFAULT NULL,
   application_id    	int(11)         NOT NULL,
   PRIMARY KEY (id),
   KEY environment_application_id_fk (application_id),
   CONSTRAINT environment_application_id_fk FOREIGN KEY (application_id) REFERENCES application (id)
);

CREATE TABLE IF NOT EXISTS user (
   id                	int(11)         	NOT NULL AUTO_INCREMENT,
   idp_id            	varchar(255)    	NOT NULL,
   organization_id   	int(11)         	NOT NULL,
   PRIMARY KEY (id)
);
