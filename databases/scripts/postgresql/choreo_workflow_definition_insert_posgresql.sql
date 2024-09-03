INSERT INTO public.workflow_definition
(id, "name", description, approver_types, execute_upon_approval, allow_parallel_requests, request_format_schema)
VALUES('ENV_PROMOTION', 'Environment Promotion','Promotion of a build from one environment to another', 'ROLE,USER', false, false,
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
        "buildId": {
            "displayName": "Build ID",
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
        "envFromId": {
            "displayName": "Source Environment ID",
            "dataType": "string",
            "required": true,
            "extractfrom": "envFrom.id"
        },
        "envToName": {
            "displayName": "Target Environment Name",
            "dataType": "string",
            "required": true,
            "extractfrom": "envTo.name"
        },
        "envToId": {
            "displayName": "Target Environment ID",
            "dataType": "string",
            "required": true,
            "extractfrom": "envTo.id"
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
