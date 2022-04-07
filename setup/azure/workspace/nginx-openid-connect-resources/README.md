## Description

This module is used to provide SSO support to code-server with the support of Asgardeo. The files are related to https://github.com/nginxinc/nginx-openid-connect and compatible only with nginx-plus

### Files
- openid_connect.js
  - Contains authentication logic for nginx-openid-connect module
- openid_connect.server.conf
  - Contains `/locations` required for redirections
- openid_connect_configuration.conf
  - Contains the configuration information related to basic settings and key-value stores
- zone_sync.conf
  - Contains information related to key-value zone synchronization. The synchronization helps to keep all the nginx-controller pods synced to same level otherwise the token can not be found in the key-value store when the request hits another pod. 

### Supportive Files
- headless-service.yaml
  - Used to create a synchronization connection for key-value stores

The module files are updated on top of(https://github.com/nginxinc/nginx-openid-connect/commit/db991ecb83e1b388e1ae74bf5e16dd4a6eb4e6d0) to overcome some of the limitations while configuring the module to use with Asgardeo for the workspace cluster.

