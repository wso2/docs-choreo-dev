CREATE TABLE organization
(
    id         int(11)      NOT NULL AUTO_INCREMENT,
    uuid       varchar(255) NOT NULL,
    name       varchar(255) NOT NULL,
    handle     varchar(255) NOT NULL,
    created_at datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY handle_unique (handle)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE user
(
    id              int(11)      NOT NULL AUTO_INCREMENT,
    idp_id          varchar(255) NOT NULL,
    created_at      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY idp_id_unique (idp_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE organization_user_mapping
(
    user_id         int          NOT NULL,
    organization_id int          NOT NULL,
    user_roles      varchar(255) NOT NULL,
    created_at      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
    organization_id varchar(255) NOT NULL,
    created_at      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY organization_id_handle_unique (organization_id, handle)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE environment
(
    id                INT AUTO_INCREMENT,
    invocation_url    VARCHAR(300) NULL,
    observability_url VARCHAR(300) NULL,
    deployment_status VARCHAR(255) NULL,
    test_status       VARCHAR(255) NULL,
    application_id    INT          NOT NULL,
    created_at        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT environment_pk
        PRIMARY KEY (id),
    CONSTRAINT environment_application_id_fk
        FOREIGN KEY (application_id) REFERENCES application (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE `application` ADD COLUMN `display_type` VARCHAR(255) NULL DEFAULT '' AFTER `working_file`;
ALTER TABLE `application` ADD COLUMN `deploy_type` VARCHAR(255) NULL DEFAULT '' AFTER `display_type`;

ALTER TABLE `environment` ADD COLUMN `deployment_build_id` VARCHAR(255) NULL DEFAULT '' AFTER `test_status`;

CREATE TABLE beta_invitation
(
    id              int(11)      NOT NULL AUTO_INCREMENT,
    invitation_code varchar(255) NOT NULL,
    correlation_key varchar(255) NOT NULL,
    invited_email   varchar(255) NOT NULL,
    signed_up_email varchar(255),
    user_id         int(11) DEFAULT NULL,
    status          varchar(255) NOT NULL,
    created_at      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY beta_invitation_correlation_key_uindex (correlation_key),
    KEY beta_invitation_user_id_fk (user_id),
    CONSTRAINT beta_invitation_user_id_fk FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
