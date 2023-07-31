# Configuring ServiceNow as the Incident Management System

## Step 01: Add a Label to Distinguish a Choreo Related Incident in ServiceNow Instance

1. Go to your ServiceNow instance.
2. Follow the document [Add a new field with the form designer](https://docs.servicenow.com/bundle/utah-platform-administration/page/administer/form-administration/concept/c_FormDesign.html#title_t_AddANewField) and add a **True/False**type field.
3. Enter the following values and create the field. 

| **Field**       |  **Value**                  |
|-----------------|-----------------------------|
| **Label**       | Choreo Incident             |
| **Name**        | u_is_choreo                 |
    
4. Click **Save**.

Once the label is added, when you create a Choreo incident, you need to check the **Choreo Incident** checkbox.

## Step 02: Create a Service Account and an OAuth API endpoint in the ServiceNow Instance

 ### Via the ServiceNow user interface

1. Create a Service account
     1. Follow the document [Create a User](https://docs.servicenow.com/bundle/utah-platform-administration/page/administer/users-and-groups/task/t_CreateAUser.html) and create a user for Choreo.
     2. Select the newly created user and navigate to the **Roles** tab. 
     3. Click **Edit** on the **Roles**  tab and add a the role `sn_incident_read` to the user. 
     4. Click **Save**.
     5. Click **Update**.
   
2. Create an OAuth API endpoint for external clients
   
   Follow the document [Create an endpoint for clients to access the instance](https://docs.servicenow.com/bundle/utah-platform-security/page/administer/security/task/t_CreateEndpointforExternalClients.html), give a name, and create an OAuth API endpoint for external clients.


### Via a script

1. Go to your ServiceNow instance and navigate to **All** and search for **Scripts - Background**.
2. In the **Run script** pane, copy and paste the provided script.

   ``` javascript
    var instanceId = "instanceId"; //ServiceNow instance id

    var userName = "choreo-user"; //user name
    var userFirstName = "Choreo"; //user first name
    var userLastName = "User"; //user last name
    var userPassword = Math.random().toString(36); //returns random string - user password

    var clientName = "choreo-oauth-client"; //user name for client
    var clientId = Math.random().toString(36); //returns random string - oauth client id
    var clientSecret = Math.random().toString(36); //returns random string - oauth client secret
    var refreshTokenLifespan = 3600; // refresh token lifespan in seconds

    //create user account

    var grUser = new GlideRecord('sys_user');

    grUser.user_name = userName;
    grUser.first_name = userFirstName;
    grUser.last_name = userLastName;
    grUser.user_password.setDisplayValue(userPassword);

    grUser.insert();

    gs.log("User created - UserName : " + grUser.user_name);

    //create oauth account

    var grOAuth = new GlideRecord('oauth_entity');
    grOAuth.get('sys_id_of_record');
    grOAuth.name = clientName;
    grOAuth.client_id = clientId;
    grOAuth.client_secret.setDisplayValue(clientSecret);
    grOAuth.refresh_token_lifespan = refreshTokenLifespan;
    grOAuth.update();

    gs.log("Oauth client user created : " + grOAuth.name);

    //Get access token and refresh token

    //Request body
    var body = "grant_type=password&client_id=" + grOAuth.client_id + "&client_secret=" + clientSecret + "&username=" + grUser.user_name + "&password=" + userPassword;

    var request = new sn_ws.RESTMessageV2();
    request.setEndpoint("https://" + instanceId + ".service-now.com/oauth_token.do");
    request.setRequestHeader("Accept", "application/json");
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.setRequestBody(body);
    request.setHttpMethod('POST');
    var response = request.execute();
    var responseBody = response.getBody();
    var httpStatus = response.getStatusCode();

    // array to collect what needs to be stored in file
    var fileRecord = [];

    //Needed data
    fileRecord.push("instance_id : " + instanceId);
    fileRecord.push("user_name : " + grUser.user_name);
    fileRecord.push("user_password : " + userPassword);
    fileRecord.push("client_id : " + grOAuth.client_id);
    fileRecord.push("client_secret : " + clientSecret);
    fileRecord.push("refresh_token_lifespan : "+ refreshTokenLifespan);
    fileRecord.push(responseBody);

    var ga = new GlideSysAttachment();

    ga.write(grUser, 'accessDataChoreo.json', 'text', JSON.stringify(fileRecord));

    var usr = grUser.user_name; //username for user table

    //Give sn_incident_read role to user
    var roleGR = new GlideRecord('sys_user_role');
    roleGR.addQuery('name', 'sn_incident_read');
    roleGR.query();
    if (roleGR.next()) {
        var grUserRoleGR = new GlideRecord('sys_user_has_role');
        grUserRoleGR.intialize();
        grUserRoleGR.user = grUser.sys_id; //sys_id of user to give the sn_incident_read role
        grUserRoleGR.role = roleGR.sys_id; //sys_id of sn_incident_read user role
        grUserRoleGR.state = 'Active';
        grUserRoleGR.inherits = true;
        grUserRoleGR.insert();
    }

   ```
3. Replace the following placeholders with appropriate values: 
   
   | Field                 | Vaue                                                                                                               |
   |-----------------------|--------------------------------------------------------------------------------------------------------------------|
   | instanceId            | You can find the `instanceId` in the URL : https://<instanceId>.service-now.com                                    |
   | userName              | By deafult, the value is set to `choreo-user` in the script. You can replace it with an appropriate value.         |
   | userFirstName         | By deafult, the value is set to `Choreo` in the script. You can replace it with an appropriate value.              |
   | userLastName          | By deafult, the value is set to `User` in the script. You can replace it with an appropriate value.                |
   | userPassword          | The script has a random numbre generator that generates a password by default.                                     |
   | clientName            | By deafult, the value is set to `choreo-oauth-client` in the script. You can replace it with an appropriate value. |
   | clientId              | The script has a random numbre generator that generates a client id by default.                                    |
   | clientSecret          | The script has a random numbre generator that generates a client secret by default.                                |
   |  refreshTokenLifespan | By Default, the value is set to 3600 seconds in the script.                                                        |

4. Click **Run script**.


Then navigate to All > System Security > Users and Groups > Users.

Open the user record with the name `choreo-user` (or the `userName` you used in the script) and download the attachment "Access_Data_Choreo.json". This attachment will contain the required data to configure the CIO dashboard.

## Step 03: Go to the Configure Incident Management System and Select ServiceNow in the Choreo Console

1. Sign in to Choreo using your Google, GitHub, or Microsoft account.
2. On the left navigation menu, click Delivery Insights.
3. Click on the DORA Metrics tab.
4. Scroll to the bottom of the dashboard and click Configure.
5. Select ServiceNow as the incident management system.

## Step 04: Add the ServiceNow Instance Details

1. Fill in the details and click Next.
2. The instance connection will be validated. If there is any issue, a notification will be shown; otherwise, it will get directed to the next step.

| Field                   | Description                                                 | Value                                                               |
|-------------------------|-------------------------------------------------------------|---------------------------------------------------------------------|
| Data Plane              | Choreo collects incident details by running a scheduled job which invokes the ServiceNow REST API periodically. This job runs on the user's data plane. This configuration allows users to specify a preferred data plane to run the job, especially when they have multiple dataplanes. | Select preferred data plane from Data Plane list.                 |
| ServiceNow Instance ID  | The ID of your ServiceNow instance.                        | The ID is in your ServiceNow base URL (Gor example, `https://<instanceID>.service-now.com/`) |
| Service Account User ID | The User ID of the Service account in the Users table to be used. | The User ID of the User record created in Step 02.                |
| Service Account Password| The password of the Service account in the Users table to be used. | The password of the User record created in Step 02.                |
| OAuth Client ID         | The Client ID of the OAuth Client account in the Application Registries table to be used. | The Client ID of the OAuth Client record created in Step 02.      |
| OAuth Client Secret     | The Client secret of the OAuth Client account in the Application Registries table to be used. | The Client Secret of the OAuth Client record created in Step 02.  |

## Step 05: Filter Labels

This label lets the incident scraper know that the incident should be scraped. Enter the "Name" property, which was under properties when creating the label in Step 01, as the ServiceNow incident Label. Then click on Save.

Saving will deploy the incident scraper for your ServiceNow instance, and DORA metric charts for Mean Time To Recover and the Change Failure Rate will be populated in the CIO dashboard. If there are any issues when configuring the integration, an error message will be shown. Then the configure banner will appear, and the user can reconfigure.

## Step 06: Enriching ServiceNow Incident Records with Deployment Information

Choreo extracts deployment information from the relevant incident and generates DORA metrics that help you analyze the deployment statistics related to the incidents. Therefore, you need to manually update the ServiceNow incident record description with the relevant deployment-related information. Follow the steps below to add the deployment information to the ServiceNow incident record.

1. Get deployment details
   - On the Choreo console header, select the project and the component for which the incident was reported.
   - On the left navigation menu, click Deploy.
   - On the Production Environment card, click on Deployment History.
   - On the right-hand side panel, select the relevant deployment, and click Release details to copy the deployment details to clipboard.

2. Add deployment information to the ServiceNow incident
   - Paste the deployment details you copied (in step 06 under section Get deployment details) at the beginning of the incident description.
   - If the incident is related to a deployed component that is in production, add `isProd:true` at the end of the copied deployment information; if not, add `isProd:false`. If this is not done, by default, it will be considered as an `isProd:true` incident.
   - Add the rest of the description after the above information.
   - Click Submit.

Please note that this conversion to Mkdocs format is for documentation purposes. The actual implementation of the script and steps should be done within the ServiceNow instance.
