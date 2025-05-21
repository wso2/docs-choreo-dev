**Description**
This collection is created to check elevated permission for the devportal APIs.
The collection includes the operations used in the Choreo APIM devportal UI.

**How to use**

1. Import the given environment file: `Devportal access test.postman_environment.json`
2. Set `apim-host` to the testing environment or host
3. Variables starting with `manjula-` are the parameters taken from the low priviledged organization
4. Other variables should be taken from the tester's high priviledged organization
5. Set the postman environment to `Devportal access test`
6. Test the operations by changing the `org-uuid` and token with the variables starting with `manjula-` and vise versa
