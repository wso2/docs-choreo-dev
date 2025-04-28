# Control Access in the Choreo Console

In the Choreo Console, you have the ability to manage access to projects and the actions that can be performed within them. Administrators have the capability to restrict project access to specific user groups. This feature is useful when you need certain user groups to have access to particular projects or for a set of projects.

Choreo uses **Roles**, **Groups**, **Service Accounts**, and a **Mapping level** to control access to the Choreo Console as follows: 

- **Role** : Role is a collection of permissions. Choreo has a predefined set of roles with permissions assigned to them. [Learn more](../choreo-concepts/organization.md#roles)
- **Group** : Group is a collection of users. A user group requires a role or multiple roles to be assigned to it so that the users in those groups get the relevant permissions via the assigned roles. [Learn more](../choreo-concepts/organization.md#groups)
- **Service Account** : A Service Account is a non-human entity that can be created by a user in the organization to access resources without exposing user credentials. Service Accounts can be added to groups, and groups can be assigned to Service Accounts to manage their access. [Learn more](../choreo-concepts/organization.md#service-accounts)

- **Mapping level** : A mapping level defines the extent at which a role-group mapping can be done. Choreo has two defined resource levels.
    - **Organization** : You can assign a role to a group or associate a group with a role within the organization. This ensures that    
                         all users or Service Accounts in a group inherit the permissions granted by that role across all organizational resources.
                         For example, if a user or Service Account has edit_project permission at the organization mapping level, they can edit all the projects in the organization.
    - **Project** : You can assign a role to a group or associate a group with a role within a specific project resource. This ensures 
                    that users or Service Accounts in the group inherit the permissions granted by that role only within the context of the specified project.
                    For example, if a user or Service Account has edit_project permission at the project mapping level, they can only edit the specified project.

In Choreo, authorization operates by assigning a role to a group at a specified level. The level at which the role is assigned determines the extent of permissions granted to users or Service Accounts.

!!! warning "Important"
    Avoid assigning multiple roles to a single user or Service Account across different projects or levels (organization and project). Such assignments can grant unintended permissions to some projects, allowing them to perform tasks they shouldn't have access to. Therefore, it is recommended to assign only one role to a user or Service Account across projects or levels to ensure proper access control.

!!! info
    In Choreo, organization-level permissions take precedence over project-level permissions.

To elaborate further, refer to the following diagram. 

The following diagram depicts a role-group assignment at a specific resource level. In the diagram, an admin user has assigned the Developer role to all members of the Engineering group within the Engineering Project. This grants users and Service Accounts in the Engineering group the ability to perform all actions allowed by the Developer role within the Engineering Project.

![Console access control](../assets/img/administer/access-control-to-console.png)

## Sample scenario

Now that you understand the basic concepts of access control within the Choreo Console, let’s try out a sample scenario to manage access within a project. 

Assume you are overseeing the Engineering Project within your organization and you need to grant development access to specific users solely within this project. Here's a step-by-step guide on how to achieve this:

### Step 1: Create a project

Follow the steps given below to create a project:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the organization home page.
2. On the organization home page, click **+ Create Project**.
3. Enter a display name, unique name, and description for the project. You can enter the values given below:
    
    !!! info
         In the **Name** field, you must specify a name to uniquely identify your project in various contexts. The value is editable only at the time you create the project. You cannot change the name after you create the project.

    | **Field**                | **Value**                          |
    |--------------------------|------------------------------------|
    | **Project Display Name** | `Engineering Project`              |
    | **Name**                 | `engineering-project`              |
    | **Project Description**  | `My sample project`                |

4. Click **Create**. This creates the project and takes you to the project home page.

### Step 2: Create a new group

Follow the steps given below to create a group with the name `Engineering Project Developer`:

1. In the Choreo Console, go to the top navigation menu, click the **Organization** list, and select the organization where you created your project.
2. In the left navigation menu, click **Settings**.
3. Click the **Access Control** tab and then click the **Groups** tab.
4. Click **+ Create Group**.
5. Enter a group name and group description. You can enter the values given below:

    | **Field**                | **Value**                          |
    |--------------------------|------------------------------------|
    | **Group Name**           | `Engineering Project Developer`    |
    | **Group Description**    | `Users with development access within the engineering project`|

6. Click **Create**.

### Step 3: Assign roles to the group

Follow the steps given below to assign the **Developer** role to the **Engineering Project Developer** group that you created:

1. In the Choreo Console, go to the top navigation menu, click the **Project** list, and select the **Engineering Project** that you created.
2. In the left navigation menu, click **Settings**.
3. Click the **Access Control** tab and then click the **Groups** tab.
4. On the **Groups** tab, search for the **Engineering Project Developer** group and click the corresponding edit icon.
5. Click **+Add Roles**. 
6. In the **Add Roles to Group in Project** dialog that opens, click the **Roles** list and select **Developer**.
7. Click **Add**. This assigns the **Developer** role to the group. You should see the mapping level as **Project (Engineering Project)** as follows, indicating the scope of the mapping:

    ![Mapping level](../assets/img/administer/mapping-level.png)

   This means that you have granted developer access to users in the Engineering Project Developer group in the scope of the Engineering Project. 

Now that you have set up access control, you can proceed to add users to the new group.

### Step 4: Add users to the group

There are two approaches you can follow to add users to the group.

#### Add a new user as a project developer 

Follow the steps given below to add a new user as a project developer:

1. In the Choreo Console, go to the top navigation menu, click the **Organization** list, and select the organization where you created your project.
2. In the left navigation menu, click **Settings**.
3. Click the **Access Control** tab and then click the **Users** tab.
4. Click **+Invite Users**.
5. In the **Invite Users** dialog,
   1. Specify the email addresses of the users in the **Emails** field.
   2. Click the **Groups** list and select **Engineering Project Developer**.
6. Click **Invite**.

#### Add an existing user as a project developer 

Follow the steps given below to add an existing user as a project developer:

1. In the Choreo Console, go to the top navigation menu, click the **Organization** list, and select the organization where you created your project.
2. In the left navigation menu, click **Settings**.
3. Click the **Access Control** tab and then click the **Users** tab.
4. Search for the existing user you want to add to the **Engineering Project Developer** group.
5. Click the edit icon corresponding to the user.
6. Click **+Assign Groups**.
7. In the **Add Groups to User** dialog, click the **Groups** list and select **Engineering Project Developer**.
8. Click **Add**.

!!! tip
     Make sure to remove the user from any other groups to avoid granting organization-level access unintentionally.


!!! note
     - Existing groups are already mapped to similar roles at the organization level. Therefore, adding users to those groups or keeping users in them, will give organization-level access to the users.
     - When users are added to the **Engineering Project Developer** group, they will only have developer access to the **Engineering Project**.
     - You can invite new users or add existing users to new groups within the Engineering Project, and based on their requirements, assign roles like Developer, API Publisher, etc.

Now you have successfully set up access control within your project.

## Managing Service Accounts

### Step 1: Create a Service Account

Follow the steps below to create a Service Account:

1. In the Choreo Console, go to the top navigation menu, click the **Organization** list, and select the organization where you want to create the Service Account.
2. In the left navigation menu, click **Settings**.
3. Click the **Access Control** tab and then click the **Service Accounts** tab.
4. Click **+ Create Service Account**.
5. Enter a name and description for the Service Account. You can enter the values given below:

    | **Field**                | **Value**                          |
    |--------------------------|------------------------------------|
    | **Name** | `Example Bot`          |
    | **Description**          | `Service Account for Engineering Project automation`|

6. Click **Create**. This creates the Service Account and generates credentials that you can use to obtain an access token.

!!! warning
    The **Client Secret** will only be shown once. Make sure to securely store it. If you lose the secret, you will need to regenerate it.

### Step 2: Add Groups to the Service Account

Follow the steps below to add groups to the Service Account:

1. In the Choreo Console, go to the top navigation menu, click the **Organization** list, and select the organization where you created the Service Account.
2. In the left navigation menu, click **Settings**.
3. Click the **Access Control** tab and then click the **Service Accounts** tab.
4. Search for the Service Account you want to manage (e.g., `Example Bot`) and click the corresponding edit icon.
5. Click **+Add Groups**.
6. In the **Add Groups to Service Account** dialog, select the groups you want to associate with the Service Account (e.g., `Developer`).
7. Click **Add**. You can also remove groups from the Service Account using the delete icon in the groups list of the Service Account.

### Managing Service Accounts in Groups

When you select a group in the **Groups** tab, you can view the Service Accounts associated with that group. 

- To add a Service Account to the group, click **+Add Service Accounts**, select the desired Service Account, and click **Add**.
- To remove a Service Account from the group, use the delete icon corresponding to the Service Account in the list.

This allows you to manage the association between Service Accounts and groups effectively.

You can view the list of Service Accounts under the **Service Accounts** section of the group. You can also delete Service Accounts from the group using the delete icon.

### Step 3: Manage Service Account Details

You can update the name and description of a Service Account by following these steps:

1. In the Choreo Console, go to the **Service Accounts** tab under **Access Control**.
2. Click the edit icon corresponding to the Service Account you want to update.
3. Modify the name and description as needed.
4. Click **Save**.

### Step 4: Obtain an Access Token for a Service Account

To authenticate programmatic access using a Service Account, you need to obtain an access token. Use the following `curl` command to get the token:

```shell
curl --location 'https://auth.choreo.dev/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic Base64(client_id:client_secret)' \
--data-urlencode 'grant_type=service_account' \
--data-urlencode 'scope=apim:api_view apim:api_create apim:api_publish apim:subscribe apim:api_delete service_catalog:service_view service_catalog:service_write apim:api_generate_key'
```

Now you have successfully created and managed a Service Account for programmatic access.