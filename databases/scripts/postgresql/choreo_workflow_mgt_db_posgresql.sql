-- Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.

-- This software is the property of WSO2 Inc. and its suppliers, if any.
-- Dissemination of any information or reproduction of any material contained
-- herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
-- You may not alter or remove any copyright or other notice from copies of this content.

-- Create workflow_definition table
CREATE TABLE workflow_definition (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    approver_types VARCHAR(255) NOT NULL,
    approver_permission VARCHAR(255) NOT NULL,
    execute_upon_approval BOOLEAN NOT NULL,
    allow_parallel_requests BOOLEAN NOT NULL,
    request_format_schema TEXT NOT NULL,
    scope VARCHAR(50) NOT NULL DEFAULT 'PROJECT'
);

-- Create org_workflow_config table
CREATE TABLE org_workflow_config (
    id VARCHAR(36) PRIMARY KEY,
    org_id VARCHAR(36) NOT NULL,
    project_id VARCHAR(36),
    enabled BOOLEAN NOT NULL DEFAULT false,
    workflow_definition_id VARCHAR(50) NOT NULL REFERENCES workflow_definition(id),
    assignee_roles VARCHAR(255) NOT NULL,
    assignees VARCHAR(255) NOT NULL,
    format_request_data BOOLEAN NOT NULL,
    external_workflow_engine_endpoint TEXT
);

-- Create workflow_instance table
CREATE TABLE workflow_instance (
    id VARCHAR(36) PRIMARY KEY,
    description VARCHAR(255) NOT NULL DEFAULT '',
    org_workflow_config_id VARCHAR(36) NOT NULL REFERENCES org_workflow_config(id),
    org_id VARCHAR(36) NOT NULL,
    project_id VARCHAR(36),
    resource VARCHAR(255) NOT NULL,
    workflow_definition_id VARCHAR(50) NOT NULL REFERENCES workflow_definition(id),
    created_by VARCHAR(255) NOT NULL,
    created_time TIMESTAMPTZ NOT NULL,
    request_comment TEXT,
    data TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    reviewed_by VARCHAR(255),
    reviewer_decision VARCHAR(50),
    review_comment TEXT,
    review_time TIMESTAMPTZ
);

-- Create audit_event table
CREATE TABLE audit_event (
    id VARCHAR(36) PRIMARY KEY,
    org_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- e.g., request, review, approve, reject, cancel, execute
    timestamp TIMESTAMPTZ NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    workflow_definition_id VARCHAR(50) NOT NULL REFERENCES workflow_definition(id),
    resource VARCHAR(255) NOT NULL,
    workflow_instance_id VARCHAR(36) NOT NULL, --we do not foreign key, just kept as info
    comment TEXT
);

-- Indexes for better performance on JSONB fields (optional but recommended)
--CREATE INDEX idx_workflow_definition_request_format_schema ON workflow_definition USING GIN (request_format_schema);
--CREATE INDEX idx_workflow_instance_data ON workflow_instance USING GIN (data);

--Insert workflow definitions

-- 1. ENVIRONMENT PROMOTION

INSERT INTO public.workflow_definition
(id, "name", description, approver_types, approver_permission, execute_upon_approval, allow_parallel_requests, request_format_schema)
VALUES('ENV_PROMOTION', 'Environment Promotion','Promotion of a build from one environment to another, 'ROLE,USER', 'choreo:workflow_component_promotion_approve', false, false,
    '{
        "projectName": {
            "displayName": "Project Name",
            "dataType": "string",
            "required": true,
            "extractfrom": "projectName"
        },
        "componentName": {
            "displayName": "Component Name",
            "dataType": "string",
            "required": true,
            "extractfrom": "componentName"
        },
        "build": {
            "displayName": "Build/Image",
            "dataType": "string",
            "required": true,
            "extractfrom": "buildId"
        },
        "envFromName": {
            "displayName": "Source Environment Name",
            "dataType": "string",
            "required": true,
            "extractfrom": "envFrom.name"
        },
        "envToName": {
            "displayName": "Target Environment Name",
            "dataType": "string",
            "required": true,
            "extractfrom": "envTo.name"
        },
        "commitId": {
            "displayName": "Commit ID",
            "dataType": "string",
            "required": false,
            "extractfrom": "commitId"
        },
        "buildPageUrl": {
            "displayName": "Build Page URL",
            "dataType": "string",
            "required": false,
            "extractfrom": "buildPageUrl"
        }
    }');

-- 2. API_SUBSCRIPTION_CREATION

INSERT INTO public.workflow_definition
(id, "name", description, approver_types, approver_permission, execute_upon_approval, allow_parallel_requests, request_format_schema)
VALUES('API_SUBSCRIPTION_CREATION', 'API Subscription Creation','Create an API subscription with a given plan', 'ROLE,USER', 'choreo:workflow_subscription_approve', true, false,
    '{
        "subscriptionPolicy": {
            "displayName": "Subscription Plan",
            "dataType": "string",
            "required": true,
            "extractfrom": "subscriptionPolicy"
        },
        "applicationName": {
            "displayName": "Application Name",
            "dataType": "string",
            "required": true,
            "extractfrom": "applicationInfo.applicationName"
        },
        "apiName": {
            "displayName": "API name",
            "dataType": "string",
            "required": true,
            "extractfrom": "apiInfo.apiName"
        },
        "apiVersion": {
            "displayName": "API version",
            "dataType": "string",
            "required": true,
            "extractfrom": "apiInfo.apiVersion"
        }
    }');

-- 3. API_SUBSCRIPTION_UPDATE

INSERT INTO public.workflow_definition
(id, "name", description, approver_types, approver_permission, execute_upon_approval, allow_parallel_requests, request_format_schema)
VALUES('API_SUBSCRIPTION_UPDATE', 'API Subscription Update','Update an existing API subscription with a given plan', 'ROLE,USER', 'choreo:workflow_subscription_approve', true, false,
    '{
        "subscriptionPolicy": {
            "displayName": "Subscription Plan",
            "dataType": "string",
            "required": true,
            "extractfrom": "subscriptionPolicy"
        },
        "requestedSubscriptionPolicy": {
            "displayName": "Requested Plan",
            "dataType": "string",
            "required": true,
            "extractfrom": "requestedSubscriptionPolicy"
        },
        "applicationName": {
            "displayName": "Application Name",
            "dataType": "string",
            "required": true,
            "extractfrom": "applicationInfo.applicationName"
        },
        "apiName": {
            "displayName": "API name",
            "dataType": "string",
            "required": true,
            "extractfrom": "apiInfo.apiName"
        },
        "apiVersion": {
            "displayName": "API version",
            "dataType": "string",
            "required": true,
            "extractfrom": "apiInfo.apiVersion"
        }
    }');
