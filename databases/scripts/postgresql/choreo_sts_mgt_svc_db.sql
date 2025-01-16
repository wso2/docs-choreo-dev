/*
 *   Copyright (c) 2024 
 *   All rights reserved.
 */
-- Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
--
-- This software is the property of WSO2 LLC. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

CREATE TABLE dp_sts_mappings (
	id serial4 NOT NULL,
	organization_id varchar(50) NOT NULL,
	environment_id varchar(50) NOT NULL,
	idp_id varchar(50) NOT NULL,
	CONSTRAINT dp_sts_mappings_pkey PRIMARY KEY (id),
	CONSTRAINT dp_sts_mappings_unique UNIQUE (organization_id, environment_id)
);
