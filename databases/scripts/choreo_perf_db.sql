CREATE DATABASE IF NOT EXISTS choreo_perf_db;
USE choreo_perf_db;

CREATE TABLE IF NOT EXISTS api_metrics (
  `id`              int(11)       NOT NULL AUTO_INCREMENT,
  `time_stamp`      bigint(20)    NOT NULL,
  `api_name`        varchar(256)  NOT NULL,
  `env_name`        varchar(16)   NULL,
  `wip`             float         NOT NULL,
  `latency`         float         NOT NULL,
  `throughput`      float         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9813 DEFAULT CHARSET=latin1;

CREATE TABLE ml_models (
  `api_name`          varchar(256) NOT NULL,
  `model`             longblob     NOT NULL,
  `model_score`       float        NOT NULL,
  `data_min`          float        NOT NULL,
  `data_max`          float        NOT NULL,
  `version`           bigint(20)   NOT NULL,
  `avg_process_time`  float        NOT NULL,
  `max_process_time`  float        NOT NULL,
  `min_process_time`  float        NOT NULL,
  PRIMARY KEY (`api_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER table ml_models add column api_id int first;
ALTER table ml_models drop primary key;
ALTER table ml_models modify column api_id int not null primary key auto_increment;

ALTER table ml_models add constraint api_name_unique unique(api_name);

CREATE TABLE IF NOT EXISTS model_store (
`api_id` int NOT NULL,
`model_type` smallint NOT NULL DEFAULT 1,
`model` longblob NOT NULL,
`model_score` float NOT NULL,
PRIMARY KEY (`api_id`, `model_type`), FOREIGN KEY (`api_id`) REFERENCES ml_models(api_id) ON UPDATE CASCADE ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=latin1;

ALTER table ml_models drop column model_score;

ALTER table ml_models drop column model;
