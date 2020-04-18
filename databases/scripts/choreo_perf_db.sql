CREATE TABLE IF NOT EXISTS api_metrics (
  `id`              int(11)       NOT NULL AUTO_INCREMENT,
  `time_stamp`      bigint(20)    NOT NULL,
  `api_name`        varchar(256)  NOT NULL,
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
