CREATE DATABASE IF NOT EXISTS choreo_program_db;
USE choreo_program_db;

CREATE TABLE IF NOT EXISTS program (
   id                    INT             AUTO_INCREMENT,
   obs_id                VARCHAR(255)    NOT NULL,
   project_secret        VARCHAR(255)    NOT NULL,
   latest_version_id     INT,
   app_id                VARCHAR(255),
   inserted_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   is_shared             BOOLEAN DEFAULT false,
   PRIMARY KEY (id),
   CONSTRAINT uc_obs_id UNIQUE (obs_id),
   CONSTRAINT uc_app_id UNIQUE (app_id),
   CONSTRAINT uc_project_secret UNIQUE (project_secret),
   CONSTRAINT uc_obs_id_latest_version_id UNIQUE (obs_id, latest_version_id)
);
CREATE TABLE IF NOT EXISTS version (
  id                    INT             AUTO_INCREMENT,
  version               VARCHAR(255)    NOT NULL,
  program_id            INT,
  ast_hash              VARCHAR(255)    NOT NULL,
  ast                   MEDIUMTEXT,
  inserted_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_active           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (program_id) REFERENCES program(id) ON DELETE CASCADE,
  CONSTRAINT uc_version_pid UNIQUE (program_id, version)
);
ALTER TABLE program ADD CONSTRAINT fk_last_version_id FOREIGN KEY (latest_version_id) REFERENCES version(id) ON DELETE CASCADE;
CREATE INDEX obsid_index ON program (obs_id);
CREATE INDEX version_index ON version (program_id, version);

ALTER TABLE program ADD COLUMN `release_id` VARCHAR(255) NULL DEFAULT NULL AFTER `app_id`;
CREATE UNIQUE INDEX `uc_release_id` ON program(release_id);

DELIMITER //
CREATE PROCEDURE GetObsId(IN projsec VARCHAR(255), IN appid VARCHAR(255), 
                          OUT pid INT, OUT obsid VARCHAR(255))
BEGIN
  IF(appid = '') THEN
     SET appid := NULL;
  END IF;
  INSERT INTO `program` (`obs_id`, `project_secret`, `app_id`) 
  SELECT UUID(),projsec,appid
  WHERE NOT EXISTS (SELECT id FROM `program` WHERE `project_secret`=projsec LIMIT 1);
  SELECT LAST_INSERT_ID() INTO pid;
  SELECT `id`,`obs_id` INTO pid,obsid FROM `program` WHERE `project_secret`=projsec;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetObsIdByReleaseId(IN projsec VARCHAR(255), IN releaseid VARCHAR(255), 
                          OUT pid INT, OUT obsid VARCHAR(255), OUT finalreleaseid VARCHAR(255))
BEGIN
  IF(releaseid = '') THEN
     SET releaseid := NULL;
  END IF;
  INSERT INTO `program` (`obs_id`, `project_secret`, `release_id`) 
  SELECT UUID(),projsec,releaseid
  WHERE NOT EXISTS (SELECT id FROM `program` WHERE `project_secret`=projsec LIMIT 1);
  SELECT LAST_INSERT_ID() INTO pid;
  SELECT `id`,`obs_id`,`release_id` INTO pid,obsid,finalreleaseid FROM `program` WHERE `project_secret`=projsec;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetVersion(IN programid INT, IN asthash VARCHAR(255), 
                            OUT vid INT, OUT vn VARCHAR(255), OUT rowcount INT)
BEGIN
  INSERT INTO `version` (`version`, `program_id`, `ast_hash`) 
  SELECT UUID(),programid,asthash
  WHERE NOT EXISTS (SELECT id FROM `version` WHERE `program_id`=programid AND `ast_hash`=asthash);
  SELECT ROW_COUNT() INTO rowcount;
  SELECT LAST_INSERT_ID() INTO vid;
  SELECT `vid`,`version` INTO vid,vn from `version` WHERE `program_id`=programid AND `ast_hash`=asthash;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE Register(IN projsec VARCHAR(255), IN asthash VARCHAR(255), IN appid VARCHAR(255), 
                          OUT obsid VARCHAR(255), OUT vn VARCHAR(255), OUT astchanged BOOLEAN)
BEGIN
  START TRANSACTION;
    SET @programid := 0;
    SET @versionid := 0;
    SET @versionrows := 0;
    SET @astchanged := false;
    CALL GetObsId(projsec, appid, @programid, obsid); 
    CALL GetVersion(@programid, asthash, @versionid, vn, @versionrows); 
    IF (@versionrows = 1) THEN
      UPDATE program SET latest_version_id=@versionid WHERE id=@programid;
      SET astchanged := true;
    END IF;
  COMMIT;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE RegisterV2(IN projsec VARCHAR(255), IN asthash VARCHAR(255), IN releaseid VARCHAR(255), 
                          OUT obsid VARCHAR(255), OUT vn VARCHAR(255), OUT astchanged BOOLEAN, OUT finalreleaseid VARCHAR(255))
BEGIN
  START TRANSACTION;
    SET @programid := 0;
    SET @versionid := 0;
    SET @versionrows := 0;
    SET @astchanged := false;
    CALL GetObsIdByReleaseId(projsec, releaseid, @programid, obsid, finalreleaseid); 
    CALL GetVersion(@programid, asthash, @versionid, vn, @versionrows); 
    IF (@versionrows = 1) THEN
      UPDATE program SET latest_version_id=@versionid WHERE id=@programid;
      SET astchanged := true;
    END IF;
  COMMIT;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE DeleteVersion(IN versionId INT, IN obsId VARCHAR(255))
BEGIN
  DELETE FROM version WHERE id=versionId AND id NOT IN (SELECT latest_version_id from program where obs_id=obsId);
END //
DELIMITER ;
