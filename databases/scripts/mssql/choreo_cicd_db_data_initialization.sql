-- Add Builder
INSERT INTO builder
(id, builderImage, displayName, imageHash)
VALUES('D3525DF0-F149-42E1-95AE-3BCC8F597778', 'choreocontrolplane.azurecr.io/buildpacks/builder:google-22', 'Google', 'a1fd64eb789cb8b11c9169ea3f0cfe3741c565eb8e2a337fcd2b179cfde09231');

INSERT INTO builder 
(id, builderImage, displayName, imageHash) 
VALUES('3a78c3b2-c61b-4a84-a3fe-13203f3d1803', 'choreocontrolplane.azurecr.io/choreoipaas/choreo-buildpacks/builder:0.2.44', 'Choreo', 'f80d7e74715232e41e88b34a4430c70a7d1e2e91af05b68d204f46be31283557');

-- Add Buildpacks
INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120002', '', '8,11,17,18', 'Java', 1, '0', 'java', 'GOOGLE_RUNTIME_VERSION', 'Google', '/images/buildpacks/java.svg', 'D3525DF0-F149-42E1-95AE-3BCC8F597778');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120003', '', '3.10.x,3.11.x', 'Python', 1, '0', 'python', 'GOOGLE_PYTHON_VERSION', 'Google', '/images/buildpacks/python.svg', 'D3525DF0-F149-42E1-95AE-3BCC8F597778');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120004', '', '12.x.x,14.x.x,16.x.x,18.x.x,20.x.x', 'NodeJS', 1, '0', 'nodejs', 'GOOGLE_NODEJS_VERSION', 'Google', '/images/buildpacks/node.svg', 'D3525DF0-F149-42E1-95AE-3BCC8F597778');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120005', '', '1.x', 'Go', 1, '0', 'go', 'GOOGLE_GO_VERSION', 'Google', '/images/buildpacks/go.svg', 'D3525DF0-F149-42E1-95AE-3BCC8F597778');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120006', '', '8.1.x,8.2.x', 'PHP', 1, '0', 'php', 'GOOGLE_RUNTIME_VERSION', 'Google', '/images/buildpacks/php.svg', 'D3525DF0-F149-42E1-95AE-3BCC8F597778');

INSERT INTO master.dbo.buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120007', '', '3.1.x,3.2.x', 'Ruby', 1, '0', 'ruby', 'GOOGLE_RUNTIME_VERSION', 'Google', '/images/buildpacks/ruby.svg', 'D3525DF0-F149-42E1-95AE-3BCC8F597778');

INSERT INTO builder
(id, builderImage, displayName, imageHash)
VALUES('f1c43149-3ba1-4956-920b-f20a83e26810', 'n/a', 'Choreo', '');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120008', '', '', 'Ballerina', 1, '0', 'ballerina', '', 'Choreo', '/images/buildpacks/ballerina.svg', 'f1c43149-3ba1-4956-920b-f20a83e26810');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120009', '', '', 'Docker', 1, '0', 'docker', '', 'Choreo', '/images/buildpacks/docker.svg', 'f1c43149-3ba1-4956-920b-f20a83e26810');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120010', '', '', 'WSO2 MI', 1, '0', 'microintegrator', '', 'Choreo', '/images/buildpacks/microintegrator.svg', 'f1c43149-3ba1-4956-920b-f20a83e26810');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120011', '', '', 'React', 1, '0', 'react', '', 'Choreo', '/images/buildpacks/react.svg', 'f1c43149-3ba1-4956-920b-f20a83e26810');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120012', '', '', 'Angular', 1, '0', 'angular', '', 'Choreo', '/images/buildpacks/angular.svg', 'f1c43149-3ba1-4956-920b-f20a83e26810');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120013', '', '', 'Vue.js', 1, '0', 'vuejs', '', 'Choreo', '/images/buildpacks/vuejs.svg', 'f1c43149-3ba1-4956-920b-f20a83e26810');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120014', '', '', 'Static Website', 1, '0', 'staticweb', '', 'Choreo', '/images/buildpacks/file.svg', 'f1c43149-3ba1-4956-920b-f20a83e26810');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120015', '', '', 'Postman Collection', 1, '0', 'postman', '', 'Choreo', '/images/buildpacks/postman.svg', 'f1c43149-3ba1-4956-920b-f20a83e26810');

INSERT INTO buildpack 
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId) 
VALUES('f9e4820e-6284-11ee-8c99-0242ac120016', '', '6.x,7.x', '.NET', 1, '0', 'dotnet', 'GOOGLE_RUNTIME_VERSION', 'Google', '/images/buildpacks/dotnet.svg', 'D3525DF0-F149-42E1-95AE-3BCC8F597778');

INSERT INTO buildpack
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId)
VALUES('f9e4820e-6284-11ee-8c99-0242ac120017', '', '8,11,17,18', 'Spring Boot', 1, '0', 'java', 'GOOGLE_RUNTIME_VERSION', 'Google', '/images/buildpacks/spring.svg', 'D3525DF0-F149-42E1-95AE-3BCC8F597778');

INSERT INTO buildpack 
(id, buildpackImage, supportedVersions, displayName, isDefault, buidpackProviderOrgUuid, [language], versionEnvVariable, provider, iconUrl, builderId) 
VALUES('f9e4820e-6284-11ee-8c99-0242ac120018', '', '', 'Ballerina Buildpack', 1, '0', 'ballerina', 'BALLERINA_VERSION', 'Choreo Managed', '/images/buildpacks/ballerina.svg', '3a78c3b2-c61b-4a84-a3fe-13203f3d1803');

-- Template Types

INSERT INTO component_type
(id, [type], displayName)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'service', 'Service');

INSERT INTO component_type
(id, [type], displayName)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'webApp', 'Web Application');

INSERT INTO component_type
(id, [type], displayName)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'scheduleTask', 'Scheduled Task');

INSERT INTO component_type
(id, [type], displayName)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'manualTask', 'Manual Task');

INSERT INTO component_type
(id, [type], displayName)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'webhook', 'Webhook');

INSERT INTO component_type
(id, [type], displayName)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'eventHandler', 'Event Handler');


INSERT INTO component_type
(id, [type], displayName)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'testRunner', 'Test Runner');

-- Component Buildpack Mapping

-- Service
INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120002');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120003');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120004');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120005');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120006');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120007');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120008');


INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120009');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120010');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120018');

-- Web App
INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120003');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120004');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120005');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120006');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120007');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120009');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120011');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120012');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120013');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120014');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120017');

-- Schedule Task
INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120002');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120003');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120004');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120005');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120006');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120007');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120008');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120009');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120010');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120018');

-- Manual Task
INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120002');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120003');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120004');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120005');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120006');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120007');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120008');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120009');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120010');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120018');

-- Event Handler

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120002');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120003');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120004');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120005');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120006');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120007');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120008');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120009');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120010');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120018');


-- Test Runner
INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120015');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120002');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120003');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120004');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120005');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120006');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120007');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120009');


-- Webhook

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120008');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120002');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120003');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120004');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120005');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120006');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120007');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120009');

INSERT INTO buildpack_component_type_mapping
(typeId, buildpackId)
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120010');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e5', 'f9e4820e-6284-11ee-8c99-0242ac120018');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e1', 'f9e4820e-6284-11ee-8c99-0242ac120016');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e2', 'f9e4820e-6284-11ee-8c99-0242ac120016');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e3', 'f9e4820e-6284-11ee-8c99-0242ac120016');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e4', 'f9e4820e-6284-11ee-8c99-0242ac120016');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e6', 'f9e4820e-6284-11ee-8c99-0242ac120016');

INSERT INTO buildpack_component_type_mapping 
(typeId, buildpackId) 
VALUES('33315c0e-b6ef-4df4-b159-854b5eb650e7', 'f9e4820e-6284-11ee-8c99-0242ac120016');

-- env variables
INSERT INTO buildpack_enviornment_variable 
(id, envVariableKey, displayName, defaultValue, isOptional, shouldTakeUserInput, placeholder, buildpackId) 
VALUES('F3E3C113-560D-4422-8464-19393A20966E', 'BALLERINA_DEV_CENTRAL', 'Bcentral Env Var', 'true', 1, 0, '', 'f9e4820e-6284-11ee-8c99-0242ac120018');

INSERT INTO buildpack_enviornment_variable 
(id, envVariableKey, displayName, defaultValue, isOptional, shouldTakeUserInput, placeholder, buildpackId) 
VALUES('F3E3C113-560D-4422-8464-19393A20968E', 'DISABLE_BAL_OBSERVABILITY', 'Disable Ballerina Observability', 'true', 1, 0, '', 'f9e4820e-6284-11ee-8c99-0242ac120018');

INSERT INTO buildpack_enviornment_variable 
(id, envVariableKey, displayName, defaultValue, isOptional, shouldTakeUserInput, placeholder, buildpackId) 
VALUES('F3E3C113-560D-4422-8464-19393A20969E', 'PRIVATE_APP_TOKEN', 'Private App Token', '$PRIVATE_APP_TOKEN', 1, 0, '', 'f9e4820e-6284-11ee-8c99-0242ac120018');
