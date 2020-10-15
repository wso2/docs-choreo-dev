CREATE DATABASE IF NOT EXISTS choreo_testbase_db;
USE choreo_testbase_db;

CREATE TABLE postman_settings (
  id int(11) NOT NULL AUTO_INCREMENT,
  user_id varchar(255) NOT NULL,
  api_key varchar(255) NOT NULL,
  default_workspace_id varchar(255),
  cache_workspaces boolean NOT NULL DEFAULT true,
  cache_collections boolean NOT NULL DEFAULT true,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY user_id_unique (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE postman_workspaces (
  id int(11) NOT NULL AUTO_INCREMENT,
  workspaces json NOT NULL,
  postman_settings_id int(11) NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY postman_settings_id_UNIQUE (postman_settings_id),
  CONSTRAINT fk_postmant_settings_id FOREIGN KEY (postman_settings_id) REFERENCES postman_settings (id)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE current_collection_info (
  id int(11) NOT NULL AUTO_INCREMENT,
  user_id varchar(255) NOT NULL,
  workspace_id varchar(255) NOT NULL,
  application_id varchar(255) NOT NULL,
  collection_id varchar(255) NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY user_app_ws_id_unique (user_id, application_id, workspace_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
