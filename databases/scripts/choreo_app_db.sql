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
ALTER TABLE `application` ADD COLUMN `pre_built` TINYINT(1) NULL DEFAULT 0 AFTER `git_remote`;
ALTER TABLE `application` ADD COLUMN `sample_reference` VARCHAR(255) NULL DEFAULT '' AFTER `pre_built`;
ALTER TABLE `application` ADD COLUMN `docker_image` VARCHAR(255) NULL DEFAULT '' AFTER `sample_reference`;
ALTER TABLE `application` ADD COLUMN `project_id` VARCHAR(255) NOT NULL DEFAULT '' AFTER `handle`;
ALTER TABLE `application` ADD COLUMN `application_id` VARCHAR(255) NOT NULL DEFAULT '' AFTER `project_id`;

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

CREATE TABLE `group`
(
    id                  int      NOT NULL AUTO_INCREMENT,
    display_name        varchar(255) NOT NULL,
    handle              varchar(255) NOT NULL,
    description         varchar(255),
    default_group       boolean NOT NULL DEFAULT FALSE,
    organization_id     int NOT NULL,
    created_by          int      NOT NULL,
    updated_by          int,
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT group_org_id_fk FOREIGN KEY (organization_id) REFERENCES organization(id),
    CONSTRAINT group_key_created_by_fk FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT group_key_updated_by_fk FOREIGN KEY (updated_by) REFERENCES user(id),
    CONSTRAINT unique_group_handle   UNIQUE    KEY (organization_id,handle)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE group_member_mapping
(
    id                  int      NOT NULL AUTO_INCREMENT,
    group_id            int NOT NULL,
    user_id             int NOT NULL,
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT group_member_mapping_group_id_fk FOREIGN KEY (group_id) REFERENCES `group`(id) ON DELETE CASCADE,
    CONSTRAINT group_member_mapping_user_id_fk FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT unique_group_user_mapping   UNIQUE    KEY (group_id,user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE group_tag
(
    id                  int      NOT NULL AUTO_INCREMENT,
    group_id            int NOT NULL,
    organization_id     int NOT NULL,
    handle              varchar(255) NOT NULL,
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          int      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT tag_org_id_fk FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT tag_group_id_fk FOREIGN KEY (group_id) REFERENCES `group`(id) ON DELETE CASCADE,
    CONSTRAINT tag_group_key_created_by_fk FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT unique_group_tag_mapping   UNIQUE    KEY (group_id,handle)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE role
(
    id                  int      NOT NULL AUTO_INCREMENT,
    display_name        varchar(255) NOT NULL,
    handle              varchar(255) NOT NULL,
    description         varchar(255),
    organization_id     int NOT NULL,
    created_by          int      NOT NULL,
    updated_by          int,
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT role_org_id_fk FOREIGN KEY (organization_id) REFERENCES organization(id),
    CONSTRAINT role_key_created_by_fk FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT role_key_updated_by_fk FOREIGN KEY (updated_by) REFERENCES user(id),
    CONSTRAINT unique_role_handle   UNIQUE    KEY (organization_id,handle)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE group_role_mapping
(
    id                  int      NOT NULL AUTO_INCREMENT,
    group_id            int NOT NULL,
    role_id             int NOT NULL,
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT role_id_fk FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT group_id_fk FOREIGN KEY (group_id) REFERENCES `group`(id) ON DELETE CASCADE,
    CONSTRAINT unique_group_role_mapping   UNIQUE    KEY (group_id,role_id)
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
    CONSTRAINT fk_app_env_app_id FOREIGN KEY (application_id) REFERENCES application (id),
    UNIQUE KEY app_env_mapper_key_uindex (environment_id, application_id)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE support_user_creation_status
(
    id              int(11)      NOT NULL AUTO_INCREMENT,
    idp_id     varchar(255) NOT NULL,
    status ENUM('completed', 'incomplete') DEFAULT 'incomplete',
    created_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY idp_id_unique_key (idp_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE onprem_key
(
    id                  int(11)      NOT NULL AUTO_INCREMENT,
    display_name        varchar(255) NOT NULL,
    handle              varchar(255) NOT NULL,
    key_value               varchar(255) NOT NULL,
    status ENUM('ACTIVE', 'REVOKED', 'EXPIRED') DEFAULT 'ACTIVE',
    organization_id     int(11),
    created_by          int(11)      NOT NULL,
    updated_by          int(11),
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT onprem_key_org_id_fk FOREIGN KEY (organization_id) REFERENCES organization(id),
    CONSTRAINT onprem_key_created_by_fk FOREIGN KEY (created_by) REFERENCES user(id),
    CONSTRAINT onprem_key_updated_by_fk FOREIGN KEY (updated_by) REFERENCES user(id),
    UNIQUE KEY key_value_unique (key_value)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE onprem_key_subscription
(
    id                  int(11)      NOT NULL AUTO_INCREMENT,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    plan  varchar(255)  NOT NULL,
    organization_id     int(11),
    start_date          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_date            timestamp    NOT NULL,
    updated_date        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT subscription_org_id_fk FOREIGN KEY (organization_id) REFERENCES organization(id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS member_invitation
(
    invitation_id     int NOT NULL AUTO_INCREMENT,
    uuid                VARCHAR(255) NOT NULL,
    organization_id     int          NOT NULL,
    user_email          VARCHAR(255) NOT NULL,
    invited_groups       VARCHAR(255) NOT NULL,
    invited_application VARCHAR(255) NOT NULL,
    created_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (invitation_id),
    UNIQUE KEY email_org_unique (user_email, organization_id, invited_application),
    CONSTRAINT inv_organization_id_fk
        FOREIGN KEY (organization_id) REFERENCES organization (id) ON DELETE CASCADE
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

ALTER TABLE `configuration`
    ADD COLUMN `configuration_group_id` INT NOT NULL DEFAULT 0 AFTER `organization_id`;
ALTER TABLE `configuration`
    ADD COLUMN `encrypt_key_version` VARCHAR(255) NULL DEFAULT '' AFTER `type`;

CREATE TABLE configuration_group
(
    id              INT AUTO_INCREMENT,
    organization_id INT           NOT NULL,
    `name`          VARCHAR(255)  NOT NULL,
    display_name    VARCHAR(500)  NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY org_name_unique (organization_id, `name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE connection_info
(
    id                     INT AUTO_INCREMENT,
    handle                 VARCHAR(255)  NOT NULL,
    display_name           VARCHAR(255)  NOT NULL,
    organization_id        INT NOT NULL,
    configuration_group_id INT NOT NULL,
    connector_name         VARCHAR(255)  NOT NULL,
    created_at             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT config_group_id_fk FOREIGN KEY (configuration_group_id) REFERENCES configuration_group (id),
    UNIQUE KEY unique_config_group (configuration_group_id),
    UNIQUE KEY unique_org_handle (organization_id, handle)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `connection_info` ADD COLUMN `user_account_identifier` VARCHAR(4000) NULL DEFAULT '' AFTER `display_name`;
ALTER TABLE `connection_info` ADD COLUMN `owner_id` INT NOT NULL DEFAULT 0 AFTER `connector_name`;
ALTER TABLE `connection_info` ADD COLUMN `is_shared` BOOLEAN NOT NULL DEFAULT FALSE AFTER `owner_id`;

ALTER TABLE `connection_info` ADD COLUMN `encrypt_key_version` VARCHAR(255) NULL DEFAULT '' AFTER `user_account_identifier`;

ALTER TABLE `app_environment_mapping` DROP FOREIGN KEY `fk_app_env_app_id`;
ALTER TABLE `app_environment_mapping` DROP INDEX `fk_app_env_app_id`;

CREATE TABLE config_mapping_info
(
    id              INT AUTO_INCREMENT,
    display_name    VARCHAR(255)  NOT NULL,
    app_id          INT NOT NULL,
    env_id          INT NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT app_id_fk FOREIGN KEY (app_id) REFERENCES application (id) ON DELETE CASCADE,
    CONSTRAINT env_id_fk FOREIGN KEY (env_id) REFERENCES environment (id),
    UNIQUE KEY unique_app_env_ids (app_id, env_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `config_mapping_info` ADD COLUMN `user_id` INT NULL DEFAULT NULL AFTER `env_id`;
ALTER TABLE `config_mapping_info` DROP FOREIGN KEY app_id_fk,
                                  DROP FOREIGN KEY env_id_fk,
                                  DROP INDEX unique_app_env_ids,
                                  ADD FOREIGN KEY (app_id) REFERENCES application (id) ON DELETE CASCADE,
                                  ADD FOREIGN KEY (env_id) REFERENCES environment (id),
                                  ADD UNIQUE KEY `unique_user_app_env_ids` (`user_id`,`app_id`,`env_id`);

CREATE TABLE config_mapping
(
    id                      INT AUTO_INCREMENT,
    config_mapping_info_id  INT NOT NULL,
    config_id               INT NULL,
    connection_id           INT NULL,
    secret                  BOOLEAN NOT NULL,
    key_type                VARCHAR(255)  NOT NULL,
    config_key_name         VARCHAR(255)  NOT NULL,
    created_at              TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT config_mapping_info_id_fk FOREIGN KEY (config_mapping_info_id) REFERENCES config_mapping_info (id) ON DELETE CASCADE,
    CONSTRAINT config_id_fk FOREIGN KEY (config_id) REFERENCES configuration (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `config_mapping` DROP FOREIGN KEY config_id_fk;
ALTER TABLE `config_mapping` DROP INDEX config_id_fk;

ALTER TABLE `connection_info` ADD COLUMN `type` VARCHAR(255) NOT NULL DEFAULT 'sso' AFTER `configuration_group_id`;

ALTER TABLE `user` ADD COLUMN `is_anonymous` BOOLEAN NOT NULL DEFAULT FALSE AFTER `idp_id`;

CREATE TABLE configuration_mount
(
    id                INT AUTO_INCREMENT,
    config_key_name   VARCHAR(255)  NOT NULL,
    organization_id   VARCHAR(50)   NOT NULL,
    project_id        VARCHAR(50)   NOT NULL,
    component_id      VARCHAR(50)   NOT NULL,
    environment_id    VARCHAR(50)   NOT NULL,
    component_version VARCHAR(50)   NOT NULL,
    value_type        VARCHAR(50)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY org_project_component_version_env_key_unique (organization_id, project_id, component_id, component_version, environment_id, config_key_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE configuration_value
(
    id                INT AUTO_INCREMENT,
    config_mount_id   INT           NOT NULL,
    key_name          VARCHAR(255)  NOT NULL,
    value_ref         VARCHAR(255)  NOT NULL,
    user_id           VARCHAR(50)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT config_mount_key_id_fk FOREIGN KEY (config_mount_id) REFERENCES configuration_mount (id),
    UNIQUE KEY key_unique (key_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `configuration_value` RENAME COLUMN `user_id` TO `user_idp_id`;
ALTER TABLE `configuration_value` DROP INDEX `key_unique`;
ALTER TABLE `configuration_value` DROP COLUMN `key_name`;

ALTER TABLE `configuration_mount` ADD COLUMN `is_system` BOOLEAN NOT NULL DEFAULT FALSE AFTER `value_type`;

CREATE TABLE component_data
(
    id                  INT AUTO_INCREMENT,
    uuid                VARCHAR(50)   NOT NULL,
    organization_handle VARCHAR(50)   NOT NULL,
    project_uuid        VARCHAR(50)   NOT NULL,
    component_uuid      VARCHAR(50)   NOT NULL,
    environment_uuid    VARCHAR(50)   NOT NULL,
    component_version   VARCHAR(50)   NOT NULL,
    release_uuid        VARCHAR(50)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uuid_unique (uuid),
    UNIQUE KEY release_id_unique (release_uuid)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `configuration_mount` DROP INDEX `org_project_component_version_env_key_unique`;
ALTER TABLE `configuration_mount` DROP COLUMN `organization_id`,
    DROP COLUMN `project_id`,
    DROP COLUMN `component_id`,
    DROP COLUMN `environment_id`,
    DROP COLUMN `component_version`;
ALTER TABLE `configuration_mount` ADD COLUMN `component_data_uuid` VARCHAR(50) NOT NULL AFTER `config_key_name`;
ALTER TABLE `configuration_mount` ADD CONSTRAINT `component_data_uuid_fk` FOREIGN KEY (component_data_uuid) REFERENCES component_data (uuid),
    ADD UNIQUE KEY `component_data_uuid_key_unique` (`component_data_uuid`,`config_key_name`);
