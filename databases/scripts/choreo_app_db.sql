CREATE TABLE IF NOT EXISTS application (
   id               	int(11)         NOT NULL AUTO_INCREMENT,
   handle            	varchar(255)    NOT NULL,
   display_name      	varchar(255)    NOT NULL,
   working_file      	varchar(255)    NOT NULL,
   template          	varchar(255)    NOT NULL,
   git_remote        	varchar(255)    NOT NULL DEFAULT '',
   organization_id     varchar(255)    NOT NULL,
   PRIMARY KEY (id),
   UNIQUE KEY organization_id_handle_unique (organization_id,handle)
);

CREATE TABLE IF NOT EXISTS organization (
   id       	int(11)         NOT NULL AUTO_INCREMENT,
   name      	varchar(255)    NOT NULL,
   handle    	varchar(255)    NOT NULL,
   PRIMARY KEY (id),
   UNIQUE KEY handle_unique (handle)
);

CREATE TABLE IF NOT EXISTS environment (
   id                	int(11)         NOT NULL AUTO_INCREMENT,
   invocation_url    	varchar(300)    DEFAULT NULL,
   observability_url 	varchar(300)    DEFAULT NULL,
   deployment_status 	varchar(255)    DEFAULT NULL,
   test_status       	varchar(255)    DEFAULT NULL,
   application_id    	int(11)         NOT NULL,
   PRIMARY KEY (id),
   KEY environment_application_id_fk (application_id),
   CONSTRAINT environment_application_id_fk FOREIGN KEY (application_id) REFERENCES application (id)
);

CREATE TABLE IF NOT EXISTS user (
   id                	int(11)         	NOT NULL AUTO_INCREMENT,
   idp_id            	varchar(255)    	NOT NULL,
   organization_id   	int(11)         	NOT NULL,
   PRIMARY KEY (id)
);
