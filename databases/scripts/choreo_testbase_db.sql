CREATE DATABASE IF NOT EXISTS choreo_testbase_db;
USE choreo_testbase_db;

CREATE TABLE postman_settings (
  id int(11) NOT NULL AUTO_INCREMENT,
  user_id varchar(255) NOT NULL,
  api_key varchar(255) NOT NULL,
  default_workspace_id varchar(255),
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY user_id_unique (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
