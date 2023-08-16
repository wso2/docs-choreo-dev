# Component Management

------------

## Introduction
This directory contains the API calls related to the Choreo component experience

### How to use
1. Import the given environment file: `ComponentManagementTestUserEnv.json`
2. Set `host` to the testing environment or host.
3. Test the operations by changing the  variables.

------------
## The following details are vary according to the requirement.
As an example if you need to create a component in a root directory you don't need to provide the **dirPath**. You can keep that empty. But if you want to test the API call of creating a component to a sub directory level you need to pass a value for that.

### Details  related to the Docker component creation
1. dockerFilePath
2. dockerContextPath

### Details related to the GitHub and Bitbucket
1. appPwd - Bitbucket app password
2. bitbucketOrgName - Bitbucket organization name
3. credentialID - Generated credential ID for Bitbucket
4. bitbucketUrl - URL of the BB repository
5. secretRef - Secret form the Bitbucket webhook URL
6. dirPath - Sub direcory path in GitHub

### Details related to MI component
1. runID
2. endpointId
3. buildID

##### Note
If you execute the API call **componentCreate** you need to use those data for the rest of the API calls that related to the component. (Deploy , Promote, Versions, BuldDetails etc)


