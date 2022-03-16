## Description

This module is used to provide SSO support to code-server with the support of Asgardeo. The files are related to https://github.com/nginxinc/nginx-openid-connect and compatible only with nginx-plus

## Files
- openid_connect.js
- openid_connect.server.conf
- openid_connect_configuration.conf

The module files are updated on top of(https://github.com/nginxinc/nginx-openid-connect/commit/db991ecb83e1b388e1ae74bf5e16dd4a6eb4e6d0) to overcome some of the limitations while configuring the module to use with Asgardeo for the workspace cluster.

