# Control Access in the Choreo Console

With Choreo, administrators can control user access to different projects and environments within the organization. 
At the finest granularity level, an administrator can restrict a user to perform a specific action on a specific project and a specific environment.

## Access Control (Authorization) Concepts

To understand how to configure access control in Choreo, it is important to understand how access control is modelled in Choreo.

### Fundamental Question in Access Control

Access control in any system ultimately comes down to answering the following question.

> *Can this **user** perform this **action** on this **resource**?*

???+ example
    Can Harry view logs of development environment of engineering project? 
    
    - `Harry` is the **user**  
    - `view logs` is the **action**  
    - `development environment of engineering project` is the **resource**   

### Permission

In Choreo access control model, a permission is the right to perform a specific action. 

???+ example
    A user can create a component only if the user has `Create Component` permission.

#### Environment Specific Permissions

In Choreo, actions such as `deploy component`, `promote component`, `create configuration groups`, and `view logs` must be performed within the context of an environment. The permissions which give the right to perform such actions are categorized as **Environment Specific** permissions.

!!! note
    For actions tied to an environment, it is sometimes necessary to allow a user to perform the action in certain environments but not in others; for example, a developer may be allowed to view logs in the development environment but not in the production environment.

### Role

Assigning permissions individually to each user is a time-consuming and error prone task. Users with similar job responsibilities often need the same set of permissions, which is why **roles** exist. A role is a collection of permissions designed to reflect real-world job responsibilities.

Roles simplify permission management. For example, instead of assigning 20 permissions to each of 50 developers (1000 assignments), you can assign the 20 permissions to a Developer role and then assign that role to each developer (20 + 50 assignments).  

!!! info
    Each organization in Choreo comes with a predefined set of roles with default permissions. Organization administrators can customize existing roles or create new ones as needed. [Learn more](../choreo-concepts/organization.md#roles)

### Group 

A group is a collection of users, usually organized by team or department.

Instead of assigning a role to each user, you assign the role to the group. Every user in the group automatically gets the group’s permissions. This makes updates easier. For example, instead of removing 50 role-to-user assignments and creating 50 new ones, you only need to remove one role-to-group assignment and add one new assignment.

!!! info
    Each organization in Choreo comes with a predefined set of groups. By default, each group is mapped to a role with the same name. Organization administrators can customize these groups or create new ones as needed. [Learn more](../choreo-concepts/organization.md#groups)

### Permission to Role Assignment

In Choreo, all permission-to-role assignments apply across the organization. You can’t restrict these assignments to specific resources. For example, you can’t configure Choreo to grant the `View Logs` permission to the `Developer` role only in the Development environment of the Engineering project.

### Role to Group Assignment

The real strength of Choreo Access Control comes from role-to-group assignments. These assignments can include resource-specific restrictions. For example, you can assign the `Developer` role to the `Engineering Project Developer` group, but limit it to the Development environment of the Engineering project.

Each role-to-group assignment has two attributes:

1. **Mapping Level** : Defines whether the assignment applies to the entire `Organization` or only to a specific `Project`.
2. **Applicable Environment** : Defines whether the assignment applies to `All` environments or only to a specific `Environment`.

By combining these attributes, you can create four types of assignments.

|Assignment Type|Mapping Level|Appicable Environment|
|-|-|-|
|`Organization Scoped`|Organization|All|
|`Project Scoped`|Project|All|
|`Environement Scoped`|Organization|Environment|
|`Project-Environment Scoped`|Project|Environment|

!!! warning "Important"
    Avoid assigning multiple roles to the same group across different projects or mapping levels (organization and project). Doing so can give users unintended permissions in some projects, allowing access to tasks they shouldn’t perform. To ensure proper access control, assign only one role to a group across projects or mapping levels.

### Extent of Access granted through different Role to Group assignment types

Recall the [Fundamental Question in Access Control](#fundamental-question-in-access-control). 

> *Can this **user** perform this **action** on this **resource**?*

Based on this question, the extent of access has two parts.

1. Allowed **actions**

2. Allowed **resources** (the resources on which those actions can be performed)

Next, let’s go through each role-to-group assignment type to see the extent of access it grants. We’ll look at both the allowed **actions** and the allowed **resources**. 

#### Organization Scoped Assignments

- Actions permitted by the role are allowed on resources within the organization. 
- All other actions are not allowed.

!!! note
    Actions permitted by a role are the actions linked to the permissions assigned to the role.

#### Project Scoped Assignments

- Actions permitted by the role are allowed on resources within the specific project. 
- All other actions are not allowed.

#### Environement Scoped Assignments

- Environment specific actions permitted by the role are allowed on resources within the specific environment of the organization.
- Other actions permitted by the role are allowed on resources within the organization.
- All other actions are not allowed.

!!! note
    **Environment specific actions** permitted by a role are the actions linked to the [**environment specific permissions**](#permission) assigned to the role.

For environement scoped assignments, allowing **non environment specific** actions on resources across organization is intentional. This design aligns with real world Access Control use cases. 

!!! example
    Suppose you assign the Developer role to the Engineering Developer group, but only for the Development environment of the Engineering project. The purpose of this assignment is not to prevent developers from performing actions like `build component`, which don’t depend on a specific environment. Instead, the goal is to restrict developers from performing actions such as `promote component` on environments they aren’t authorized to access, like Production.

!!! warning "Important"
    Exercise care when creating environment scoped role-to-group assignments. Only environment specific actions are restricted to that environment; all other actions remain allowed on resources across the organization.

#### Project-Environment Scoped Assignments

- Environment Specific actions permitted by the role are allowed on resources within the specific Environment of the specific Project.
- Other actions permitted by the role are allowed on resources within the specific Project.
- All other actions are not allowed.

Similar to previous case, allowing **non environment specific** actions on resources across project is intentional. This design aligns with real world Access Control use cases.

!!! warning "Important"
    Exercise care when creating project-environment scoped role-to-group assignments. Only environment specific actions are restricted to that environment; all other actions remain allowed on resources across the project.


## Configure Access Control in Choreo

Now that you understand the basic concepts of access control within Choreo, let’s walk through a sample scenario where we need to configure access to a specific environment of a specific project.

Assume you are overseeing the Engineering Project within your organization and you need to grant development access to specific users solely within this project. As they are developers, you further need to restrict their access to the Development environment of the project. Here's a step-by-step guide on how to achieve this:

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
5. Click **+Assign Roles**. 
6. In the **Assign Roles to Group in Project** dialog that opens, click the **Roles** list and select **Developer**.
7. Click **Selected Environments** radio button under **Applicable Environments**
8. Select **Development** environment from the environment selecction dropdown 
7. Click **Assign**. This assigns the **Developer** role to the group. You should see the mapping level as **Project (Engineering Project)** and Applicable Environment as **Development** indicating the type of the asssignment:

    <!-- ![Mapping level](../assets/img/administer/mapping-level.png) -->

   This means that you have granted developer access to users in the Engineering Project Developer group in the scope of the Development environment of the Engineering Project.

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
