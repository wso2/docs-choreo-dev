CREATE DATABASE IF NOT EXISTS choreo_app_db;
USE choreo_app_db;

CREATE TABLE organization
(
    id         int(11)      NOT NULL AUTO_INCREMENT,
    uuid       varchar(255) NOT NULL,
    name       varchar(255) NOT NULL,
    handle     varchar(255) NOT NULL,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY handle_unique (handle)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE user
(
    id              int(11)      NOT NULL AUTO_INCREMENT,
    idp_id          varchar(255) NOT NULL,
    created_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY idp_id_unique (idp_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE organization_user_mapping
(
    user_id         int          NOT NULL,
    organization_id int          NOT NULL,
    user_roles      varchar(255) NOT NULL,
    created_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, organization_id),
    CONSTRAINT user_id_fk
        FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT organization_id_fk
        FOREIGN KEY (organization_id) REFERENCES organization (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE application
(
    id              int(11)      NOT NULL AUTO_INCREMENT,
    handle          varchar(255) NOT NULL,
    display_name    varchar(255) NOT NULL,
    working_file    varchar(500) NOT NULL,
    template        varchar(255) NOT NULL,
    git_remote      varchar(255) NOT NULL DEFAULT '',
    organization_id int(11)      NOT NULL,
    created_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY organization_id_handle_unique (organization_id, handle),
    KEY index_organization_id (organization_id),
    CONSTRAINT fk_organization_id FOREIGN KEY (organization_id) REFERENCES organization(id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


ALTER TABLE `application` ADD COLUMN `display_type` VARCHAR(255) NULL DEFAULT '' AFTER `working_file`;
ALTER TABLE `application` ADD COLUMN `deploy_type` VARCHAR(255) NULL DEFAULT '' AFTER `display_type`;
ALTER TABLE `application` ADD COLUMN `cron_schedule` VARCHAR(100) NULL DEFAULT '' AFTER `deploy_type`;

CREATE TABLE beta_invitation
(
    id              int(11)      NOT NULL AUTO_INCREMENT,
    invitation_code varchar(255) NOT NULL,
    correlation_key varchar(255) NOT NULL,
    invited_email   varchar(255) NOT NULL,
    signed_up_email varchar(255),
    user_id         int(11) DEFAULT NULL,
    status          varchar(255) NOT NULL,
    created_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY beta_invitation_correlation_key_uindex (correlation_key),
    KEY beta_invitation_user_id_fk (user_id),
    CONSTRAINT beta_invitation_user_id_fk FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE environment
(
    id                  INT AUTO_INCREMENT,
    display_name        VARCHAR(300) NOT NULL,
    handle              VARCHAR(300) NOT NULL,
    k8s_cluster_id      INT          NOT NULL DEFAULT -1,
    k8s_namespace       VARCHAR(300) NOT NULL,
    organization_id     INT          NOT NULL,
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT environment_pk
        PRIMARY KEY (id),
        UNIQUE KEY environment_name_unique (handle,organization_id),
    CONSTRAINT fk_orgz_id FOREIGN KEY (organization_id) REFERENCES organization (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE app_environment_mapping
(
    environment_id      INT NOT NULL,
    application_id      int(11) NOT NULL,
    status              varchar(255) NOT NULL,
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deployment_build_id varchar(255) NULL DEFAULT '',
    CONSTRAINT fk_app_env_env_id FOREIGN KEY (environment_id) REFERENCES environment (id),
    CONSTRAINT fk_app_env_app_id FOREIGN KEY (application_id) REFERENCES application (id)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE configuration
(
    id              INT AUTO_INCREMENT,
    organization_id INT           NOT NULL,
    `key`           VARCHAR(255)  NOT NULL,
    value           VARCHAR(4000) NULL,
    scope           VARCHAR(255)  NOT NULL,
    type            VARCHAR(50)   NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY org_scoped_key_unique (organization_id, `key`, scope)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
