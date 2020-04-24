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
  CONSTRAINT UC_uuid_appid UNIQUE (uuid, appId)
);
CREATE INDEX uuid_index ON program_info (uuid);
CREATE INDEX appid_index ON program_info (appId);
CREATE INDEX uuid_appid_index ON program_info (uuid, appId);
