# Control Access in the Choreo Console

In the Choreo Console, you have the ability to manage access to projects and the actions that can be performed within them. Administrators have the capability to restrict project access to specific user groups. This feature is useful when you need certain user groups to have access to particular projects or for a set of projects.

Choreo uses **Roles**, **Groups**, and a **Mapping level** to control access to the Choreo Console as follows: 

- **Role** : Role is a collection of permissions. Choreo has a predefined set of roles with permissions assigned to them. [Learn more](../choreo-concepts/organization.md#roles)
- **Group** : Group is a collection of users. A user group requires a role or multiple roles to be assigned to it so that the users in those groups get the relevant permissions via the assigned roles. [Learn more](../choreo-concepts/organization.md#groups)

- **Mapping level** : A mapping level defines the extent at which a role-group mapping can be done. Choreo has two defined resource levels.
    - **Organization** : You can assign a role to a group or associate a group with a role within the organization. This ensures that    
                         all users in a group inherit the permissions granted by that role across all organizational resources.
                         For example, if a user has edit_project permission at the organization mapping level, that user can edit all the projects in the organization.
    - **Project** : You can assign a role to a group or associate a group with a role within a specific project resource. This ensures 
                    that users in the group inherit the permissions granted by that role only within the context of the specified project.
                    For example, If a user has edit_project permission at the project mapping level, that user can only edit the specified project.


In Choreo, authorization operates by assigning a role to a group at a specified level. The level at which the role is assigned determines the extent of permissions granted to users.

!!! info
    In Choreo, organization-level permissions take precedence over project-level permissions.

To elaborate further, refer to the following diagram. 

The following diagram depicts a role-group assignment at a specific resource level. In the diagram, an admin user has assigned the Developer role to all members of the Engineering group within the Engineering Project. This grants users in the Engineering group the ability to perform all actions allowed by the Developer role within the Engineering Project.

![Console access control](../assets/img/administer/access-control-to-console.png)
