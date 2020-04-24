CREATE TABLE IF NOT EXISTS trace_spans (
  spanId                VARCHAR(36)     NOT NULL,
  parentSpanId          VARCHAR(36)     NOT NULL,
  traceId               VARCHAR(36)     NOT NULL,
  uuid                  VARCHAR(72)     NOT NULL,
  startTime             DATETIME(3)     NOT NULL,
  duration              INT UNSIGNED    DEFAULT 0,
  invocationFQN         VARCHAR(255)    NOT NULL,
  isError               BOOL            DEFAULT FALSE,
  isService             BOOL            DEFAULT FALSE,
  httpStatusCodeGroup   VARCHAR(36),
  httpStatusCode        INT UNSIGNED,
  connectorName         VARCHAR(36),
  service               VARCHAR(36),
  resource              VARCHAR(36),
  httpUrl               VARCHAR(2048),
  httpMethod            VARCHAR(36),
  action                VARCHAR(36),
  PRIMARY KEY (spanId, traceId)
);
CREATE INDEX uuid_index_ts ON trace_spans (uuid);
CREATE INDEX start_time_index_ts ON trace_spans (startTime);
